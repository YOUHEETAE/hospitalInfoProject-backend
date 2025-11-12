package com.hospital.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.service.DiseaseStatsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/stats")
public class StatisticsApiController {

	@Value("${api.admin.key}")
	private String adminApiKey;

	private final DiseaseStatsService diseaseStatsService;

	public StatisticsApiController(DiseaseStatsService diseaseStatsService) {
		this.diseaseStatsService = diseaseStatsService;
	}

	private boolean isValidApiKey(String apiKey) {
		if (apiKey == null || apiKey.trim().isEmpty()) {
			return false;
		}
		return adminApiKey.equals(apiKey);
	}

	private ResponseEntity<Map<String, Object>> unauthorizedResponse() {
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("error", "UNAUTHORIZED");
		response.put("message", "유효하지 않은 API 키입니다");
		response.put("timestamp", LocalDateTime.now());

		log.warn("API 키 인증 실패");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	// 질병 통계 데이터 저장 - JSON 응답
	@PostMapping(value = "/disease/save", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> updateDiseaseStats(
			@RequestHeader(value = "X-API-Key", required = false) String apiKey,
			@RequestParam(defaultValue = "2025") int startYear,
			@RequestParam(defaultValue = "2025") int endYear) {

		// API 키 검증
		if (!isValidApiKey(apiKey)) {
			return unauthorizedResponse();
		}

		log.info("질병 통계 데이터 저장 시작... (인증된 요청) - 기간: {}-{}", startYear, endYear);

		diseaseStatsService.updateDiseaseStats(startYear, endYear);

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "질병 통계 데이터 수집 시작됨");
		response.put("startYear", startYear);
		response.put("endYear", endYear);
		response.put("note", "진행상황은 로그 또는 /status API로 확인");
		response.put("timestamp", LocalDateTime.now());

		log.info("질병 통계 데이터 수집 시작됨! 기간: {}-{}", startYear, endYear);
		return ResponseEntity.ok(response);
	}

	// 질병 통계 수집 진행 상황 조회 - JSON 응답
	@GetMapping(value = "/disease/status", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Map<String, Object>> getDiseaseStatsStatus() {
		int completed = diseaseStatsService.getCompletedCount();
		int failed = diseaseStatsService.getFailedCount();
		int inserted = diseaseStatsService.getInsertedCount();

		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("completed", completed);
		response.put("failed", failed);
		response.put("inserted", inserted);
		response.put("timestamp", LocalDateTime.now());

		return ResponseEntity.ok(response);
	}

}
