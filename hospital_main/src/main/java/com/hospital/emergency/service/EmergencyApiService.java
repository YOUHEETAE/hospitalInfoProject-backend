package com.hospital.emergency.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.hospital.config.RegionConfig;
import com.hospital.dto.web.EmergencyResponse;
import com.hospital.emergency.caller.EmergencyApiCaller;
import com.hospital.entity.HospitalMain;
import com.hospital.repository.HospitalMainApiRepository;
import com.hospital.websocket.EmergencyApiWebSocketHandler;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class EmergencyApiService {

    private final AtomicBoolean schedulerRunning = new AtomicBoolean(false);
    private final ObjectMapper objectMapper;
    private final EmergencyApiCaller emergencyApiCaller;
    private final HospitalMainApiRepository hospitalMainApiRepository;
    private final RegionConfig regionConfig; 

    private ScheduledFuture<?> scheduledTask;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private EmergencyApiWebSocketHandler webSocketHandler;

    // 병원명 키로 병원 데이터 캐시 (간단히 전체 병원명 → 병원 정보)
    private Map<String, HospitalMain> hospitalCache = new HashMap<>();

    public EmergencyApiService(EmergencyApiCaller apiCaller, 
                              HospitalMainApiRepository hospitalMainApiRepository,
                              RegionConfig regionConfig) { 
        this.emergencyApiCaller = apiCaller;
        this.objectMapper = new ObjectMapper();
        this.hospitalMainApiRepository = hospitalMainApiRepository;
        this.regionConfig = regionConfig; 
    }

    /**
     * 서비스 시작 시점에 병원 데이터를 미리 로딩해 캐시에 저장
     */
    @PostConstruct
    public void initHospitalDataCache() {
        System.out.println("서비스 시작 - 병원 데이터 초기 로딩 시작");
        
        List<HospitalMain> hospitals = hospitalMainApiRepository.findAll();

        for (HospitalMain hospital : hospitals) {
            hospitalCache.put(hospital.getHospitalName(), hospital);
        }

        System.out.println("병원 데이터 초기 로딩 완료, 총 병원 수: " + hospitalCache.size());
    }

    /**
     * 병원명 일부 포함 검색 (부분 매칭) 메서드
     * - 캐시에서 부분 매칭하는 병원 리스트 반환
     */
    public List<HospitalMain> findHospitalsByNameContains(String partialName) {
        if (partialName == null || partialName.isEmpty()) {
            return Collections.emptyList();
        }

        List<HospitalMain> result = new ArrayList<>();
        for (String key : hospitalCache.keySet()) {
            if (key.contains(partialName)) {
                result.add(hospitalCache.get(key));
            }
        }
        return result;
    }

    public void updateEmergencyRoomData() {
        if (!schedulerRunning.get()) return;

        try {
            System.out.println("응급실 데이터 업데이트 시작...");
            List<EmergencyResponse> list = getEmergencyRoomDataAsDto();

            if (!list.isEmpty()) {
                String jsonString = objectMapper.writeValueAsString(list);
                webSocketHandler.broadcastEmergencyRoomData(jsonString);
                System.out.println("응급실 DTO 데이터 WebSocket 브로드캐스트 완료");
            } else {
                System.out.println("API 응답 데이터가 비어있습니다.");
            }
        } catch (Exception e) {
            System.err.println("응급실 데이터 업데이트 중 오류 발생:");
            e.printStackTrace();
        }
    }

    /**
     * 응급실 API에서 받은 JSON 데이터를 DTO 리스트로 변환하고,
     * 미리 로딩한 병원 데이터 캐시에서 좌표와 주소를 붙임
     */
    public List<EmergencyResponse> getEmergencyRoomDataAsDto() {
        JsonNode data = emergencyApiCaller.callEmergencyApiAsJsonNode(
                regionConfig.getEmergencyCityName(), 1, 10);

        if (data == null || !data.has("body") || !data.get("body").has("items") || !data.get("body").get("items").has("item")) {
            System.out.println("응급실 API 응답 데이터 구조가 예상과 다름 또는 데이터 없음");
            return Collections.emptyList();
        }

        JsonNode itemArray = data.get("body").get("items").get("item");

        try {
            EmergencyResponse[] responses = objectMapper.treeToValue(itemArray, EmergencyResponse[].class);
            List<EmergencyResponse> responseList = Arrays.asList(responses);

            for (EmergencyResponse response : responseList) {
                // 캐시에서 부분 매칭 병원 검색
                List<HospitalMain> hospitals = findHospitalsByNameContains(response.getDutyName());

                if (!hospitals.isEmpty()) {
                    HospitalMain hospitalData = hospitals.get(0); // 첫 번째 결과 사용
                    response.setCoordinates(hospitalData.getCoordinateX(), hospitalData.getCoordinateY());
                    response.setEmergencyAddress(hospitalData.getHospitalAddress());
                } else {
                    response.setCoordinates(null, null);
                    response.setEmergencyAddress(null);
                }
            }

            return responseList;

        } catch (Exception e) {
            System.err.println("응급실 DTO 변환 중 오류:");
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void startScheduler() {
        schedulerRunning.set(true);
        scheduledTask = taskScheduler.scheduleAtFixedRate(() -> updateEmergencyRoomData(), Duration.ofSeconds(30));
        System.out.println("30초마다 실행하는 스케줄러 시작!");
    }

    public void stopScheduler() {
        schedulerRunning.set(false);
        if (scheduledTask != null && !scheduledTask.isCancelled()) {
            scheduledTask.cancel(true);
        }
    }

    public JsonNode getEmergencyRoomData() {
        return emergencyApiCaller.callEmergencyApiAsJsonNode(regionConfig.getEmergencyCityName(), 1, 10);
    }

    public void shutdownCompleteService() {
        stopScheduler();
        webSocketHandler.closeAllSessions();
        System.out.println("응급실 서비스 완전 종료 완료");
    }
}
