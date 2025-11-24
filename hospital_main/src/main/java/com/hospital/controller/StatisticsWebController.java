package com.hospital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.dto.DiseaseStatsWebResponse;
import com.hospital.service.DiseaseStatsWebService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/web/statistics")
public class StatisticsWebController {

	private final DiseaseStatsWebService diseaseStatsService;

	@Autowired
	public StatisticsWebController(DiseaseStatsWebService diseaseStatsService) {
		this.diseaseStatsService = diseaseStatsService;
	}

	@GetMapping(value = "/diseaseData", produces = "application/json")
	public ResponseEntity<List<DiseaseStatsWebResponse>> getDiseaseData() {
		log.info("GET /web/statistics/diseaseData - 질병 통계 데이터 요청");

		List<DiseaseStatsWebResponse> response = diseaseStatsService.diseaseStatsData();

		log.info("질병 통계 데이터 응답 완료: {} 질병", response.size());
		return ResponseEntity.ok(response);
	}

}
