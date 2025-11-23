package com.hospital.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.entity.DiseaseStats;
import com.hospital.service.DiseaseStatsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/web/statistics")
public class StatisticsWebController {

	private final DiseaseStatsService diseaseStatsService;

	@Autowired
	public StatisticsWebController(DiseaseStatsService diseaseStatsService) {
		this.diseaseStatsService = diseaseStatsService;
	}

	// 전체 질병 통계 조회
	@GetMapping(value = "/disease", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DiseaseStats> getAllDiseaseStats() {
		log.info("전체 질병 통계 조회 API 호출");
		return diseaseStatsService.getAllDiseaseStats();
	}

	// 기간별 질병 통계 조회
	@GetMapping(value = "/disease/period", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DiseaseStats> getDiseaseStatsByPeriod(@RequestParam String period) {
		log.info("기간별 질병 통계 조회 API 호출 - 기간: {}", period);
		return diseaseStatsService.getDiseaseStatsByPeriod(period);
	}

	// ICD 그룹별 질병 통계 조회
	@GetMapping(value = "/disease/group", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DiseaseStats> getDiseaseStatsByIcdGroup(@RequestParam String icdGroupName) {
		log.info("ICD 그룹별 질병 통계 조회 API 호출 - 그룹: {}", icdGroupName);
		return diseaseStatsService.getDiseaseStatsByIcdGroup(icdGroupName);
	}

	// 질병명으로 검색
	@GetMapping(value = "/disease/search", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DiseaseStats> searchDiseaseStatsByName(@RequestParam String keyword) {
		log.info("질병명 검색 API 호출 - 검색어: {}", keyword);
		return diseaseStatsService.searchDiseaseStatsByName(keyword);
	}

	// 기간 + ICD 그룹으로 조회
	@GetMapping(value = "/disease/filter", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DiseaseStats> getDiseaseStatsByPeriodAndGroup(
			@RequestParam String period,
			@RequestParam String icdGroupName) {
		log.info("기간 및 ICD 그룹별 질병 통계 조회 API 호출 - 기간: {}, 그룹: {}", period, icdGroupName);
		return diseaseStatsService.getDiseaseStatsByPeriodAndGroup(period, icdGroupName);
	}

	// 모든 기간 목록 조회
	@GetMapping(value = "/periods", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getAvailablePeriods() {
		log.info("가능한 기간 목록 조회 API 호출");
		return diseaseStatsService.getAvailablePeriods();
	}

	// 모든 ICD 그룹 목록 조회
	@GetMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getAvailableIcdGroups() {
		log.info("가능한 ICD 그룹 목록 조회 API 호출");
		return diseaseStatsService.getAvailableIcdGroups();
	}

	// 상위 N개 질병 통계 조회
	@GetMapping(value = "/disease/top", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DiseaseStats> getTopDiseasesByPeriod(
			@RequestParam String period,
			@RequestParam(defaultValue = "10") int limit) {
		log.info("상위 {}개 질병 통계 조회 API 호출 - 기간: {}", limit, period);
		return diseaseStatsService.getTopDiseasesByPeriod(period, limit);
	}

}
