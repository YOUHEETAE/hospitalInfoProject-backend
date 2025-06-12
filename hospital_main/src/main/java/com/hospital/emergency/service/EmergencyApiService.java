package com.hospital.emergency.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hospital.websocket.EmergencyApiWebSocketHandler;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class EmergencyApiService {
	private final AtomicBoolean schedulerRunning = new AtomicBoolean(false); // 상태 플래그

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;
    private final ObjectMapper jsonMapper;

    @Value("${hospital.emergency.api.baseUrl}")
    private String baseUrl;

    @Value("${hospital.emergency.api.serviceKey}")
    private String serviceKey;
    
    @Autowired
    private EmergencyApiWebSocketHandler webSocketHandler;

    
    

    public EmergencyApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.xmlMapper = new XmlMapper();
        this.jsonMapper = new ObjectMapper();
        
        
    }
    
    // 스케줄링 메서드 추가 - 30초마다 실행
    @Scheduled(fixedRate = 30000)
    public void updateEmergencyRoomData() {
        if (!schedulerRunning.get()) {
            return; // 수동 트리거 전이면 실행 안 함
        }
        try {
            System.out.println("응급실 데이터 업데이트 시작...");
            JsonNode data = callEmergencyApiAsJsonNode();
            String jsonString = jsonMapper.writeValueAsString(data);
            
            if (data != null && !data.isEmpty()) {
                webSocketHandler.broadcastEmergencyRoomData(jsonString);
                System.out.println("응급실 데이터 WebSocket 브로드캐스트 완료");
            } else {
                System.out.println("API 응답 데이터가 비어있습니다.");
            }
            
        } catch (Exception e) {
            System.err.println("응급실 데이터 업데이트 중 오류 발생:");
            e.printStackTrace();
        }
    }
    public void startScheduler() {
        schedulerRunning.set(true);
    }

    public void stopScheduler() {
        schedulerRunning.set(false);
    }
    
    // 초기 데이터 가져오기용 메서드 추가
    public JsonNode getEmergencyRoomData() {
        return callEmergencyApiAsJsonNode();
    }
    

    public JsonNode callEmergencyApiAsJsonNode() {
        String stage1 = "성남시";
        int pageNo = 1;
        int numOfRows = 10;

        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
            String encodedStage1 = URLEncoder.encode(stage1, StandardCharsets.UTF_8.toString());

            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .path("/getEmrrmRltmUsefulSckbdInfoInqire")
                    .queryParam("serviceKey", encodedServiceKey)
                    .queryParam("STAGE1", encodedStage1)
                    .queryParam("pageNo", pageNo)
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("_type", "xml")
                    .build(true)
                    .toUri();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("Accept", "application/xml, text/xml");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();

            if (responseBody == null || responseBody.isEmpty()) {
                throw new RuntimeException("API 응답이 비어있습니다.");
            }

            String trimmedResponse = responseBody.trim();
            if (trimmedResponse.startsWith("{") || trimmedResponse.startsWith("[")) {
                return jsonMapper.readTree(responseBody);
            } else if (trimmedResponse.startsWith("<")) {
                return xmlMapper.readTree(responseBody.getBytes(StandardCharsets.UTF_8));
            } else {
                throw new RuntimeException("예상하지 못한 응답 형식입니다.");
            }

        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }

   
}