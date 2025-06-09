package com.hospital.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.client.HospitalDetailApiCaller;
import com.hospital.entity.Hospital;
import com.hospital.service.HospitalDetailApiService;
import com.hospital.service.HospitalMainService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/hospitals")
public class HospitalApiController {
    
    private final HospitalMainService hospitalMainService;
    private final HospitalDetailApiService hospitalDetailApiService;
    private final HospitalDetailApiCaller hospitalDetailApiCaller;
    
    public HospitalApiController(HospitalMainService hospitalMainService,
                               HospitalDetailApiService hospitalDetailApiService, 
                               HospitalDetailApiCaller hospitalDetailApiCaller) {
        this.hospitalMainService = hospitalMainService;
        this.hospitalDetailApiService = hospitalDetailApiService;
        this.hospitalDetailApiCaller = hospitalDetailApiCaller;
    }
    
    /**
     * 병원 기본 정보를 DB에 저장
     */
    @PostMapping(value = "/save", produces = MediaType.TEXT_PLAIN_VALUE)
    public String saveHospitalsToDb() {
        int savedCount = 0;
        try {
            log.info("병원 기본 정보 저장 시작...");
            savedCount = hospitalMainService.fetchParseAndSaveHospitals();
            String result = "병원 정보 " + savedCount + "개 DB 저장 완료!";
            log.info(result);
            return result;
        } catch (Exception e) {
            log.error("병원 정보 DB 저장 중 오류 발생", e);
            return "병원 정보 DB 저장 중 오류 발생: " + e.getMessage();
        }
    }
    
    /**
     * DB에 저장된 모든 병원 정보 조회
     */
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Hospital> getAllHospitals() {
        log.info("DB에서 모든 병원 정보 조회 중...");
        return hospitalMainService.getAllHospitals();
    }
    
    /**
     * 병원 상세 정보 수집 시작 (비동기 처리)
     */
    @PostMapping(value = "/details/update", produces = MediaType.TEXT_PLAIN_VALUE)
    public String updateHospitalDetails() {
        try {
            int total = hospitalDetailApiService.updateAllHospitalDetails(); // 전체 병원 수 반환
            String result = String.format("병원 상세정보 저장 시작됨! 전체 병원 수: %d개\n(실시간 진행상황은 로그에서 확인 가능)", total);
            log.info(result);
            return result;
        } catch (Exception e) {
            log.error("병원 상세정보 업데이트 중 오류 발생", e);
            return "병원 상세정보 업데이트 중 오류 발생: " + e.getMessage();
        }
    }
    
    /**
     * 병원 상세 정보 수집 진행 상황 조회
     */
    @GetMapping(value = "/details/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getUpdateStatus() {
        Map<String, Object> status = new HashMap<>();
        
        int completed = hospitalDetailApiService.getCompletedCount();
        int failed = hospitalDetailApiService.getFailedCount();
        int total = completed + failed;
        
        status.put("completed", completed);
        status.put("failed", failed);
        status.put("total", total);
        status.put("inProgress", total > 0);
        
        if (total > 0) {
            double percentage = ((double) completed / total) * 100;
            status.put("completionPercentage", Math.round(percentage * 100.0) / 100.0);
        } else {
            status.put("completionPercentage", 0.0);
        }
        
        return ResponseEntity.ok(status);
    }
}