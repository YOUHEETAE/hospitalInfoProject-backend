package com.hospital.emergency.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import com.hospital.dto.web.EmergencyResponse;
import com.hospital.emergency.service.EmergencyApiService;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyApiController {

	private final EmergencyApiService emergencyApiService;


	public EmergencyApiController(EmergencyApiService emergencyApiService) {
		this.emergencyApiService = emergencyApiService;

		
	}

	@GetMapping(value = "/start", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getEmergencyInfo() {
	    
	    emergencyApiService.startScheduler(); // 스케줄러 시작
	    
	    Map<String, Object> response = new HashMap<>();
	    try {
	        // updateEmergencyRoomData() 메서드 직접 호출
	        emergencyApiService.updateEmergencyRoomData();
	        
	        // 그리고 DTO 데이터도 반환
	        List<EmergencyResponse> emergencyList = emergencyApiService.getEmergencyRoomDataAsDto();
	        
	        response.put("success", true);
	        response.put("message", "응급실 정보 조회 및 업데이트 완료");
	        response.put("data", emergencyList); // DTO 형태로 반환
	        response.put("timestamp", System.currentTimeMillis());
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        System.err.println("응급실 정보 조회 중 오류 발생: " + e.getMessage());
	        e.printStackTrace();
	        response.put("success", false);
	        response.put("message", "응급실 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
	        response.put("error", e.getClass().getSimpleName());
	        response.put("timestamp", System.currentTimeMillis());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	    }
	}
	

	/*
	 * 완전 서비스 종료 (스케줄러 + WebSocket)
	 */
	@GetMapping("/stop")
	public ResponseEntity<String> shutdownCompleteService() {
	    try {
	        emergencyApiService.shutdownCompleteService();
	        return ResponseEntity.ok("✅ 응급실 서비스 완전 종료 완료");
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("❌ 서비스 종료 실패: " + e.getMessage());
	    }
    }
}
