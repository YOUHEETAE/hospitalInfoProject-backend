package com.hospital.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.hospital.async.DiseaseStatsAsyncRunner;
import com.hospital.entity.DiseaseStats;
import com.hospital.repository.DiseaseStatsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DiseaseStatsService {

	private final DiseaseStatsAsyncRunner diseaseStatsAsyncRunner;
	private final DiseaseStatsRepository diseaseStatsRepository;

	@Autowired
	public DiseaseStatsService(DiseaseStatsAsyncRunner diseaseStatsAsyncRunner,
	                           DiseaseStatsRepository diseaseStatsRepository) {
		this.diseaseStatsAsyncRunner = diseaseStatsAsyncRunner;
		this.diseaseStatsRepository = diseaseStatsRepository;
	}

	public void updateDiseaseStats(int startYear, int endYear) {
		log.info("질병 통계 데이터 수집 시작 - 기간: {}-{}", startYear, endYear);

		// 기존 데이터 삭제
		diseaseStatsRepository.deleteAllInBatch();

		// AsyncRunner 카운터 초기화
		diseaseStatsAsyncRunner.resetCounter();

		// 비동기 수집 시작
		diseaseStatsAsyncRunner.runAsync(startYear, endYear);

		log.info("질병 통계 데이터 수집 시작됨");
	}

	public int getCompletedCount() {
		return diseaseStatsAsyncRunner.getCompletedCount();
	}

	public int getFailedCount() {
		return diseaseStatsAsyncRunner.getFailedCount();
	}

	public int getInsertedCount() {
		return diseaseStatsAsyncRunner.getInsertedCount();
	}

	// 전체 질병 통계 조회
	public List<DiseaseStats> getAllDiseaseStats() {
		log.info("전체 질병 통계 조회");
		return diseaseStatsRepository.findAll();
	}

	// 기간별 질병 통계 조회
	public List<DiseaseStats> getDiseaseStatsByPeriod(String period) {
		log.info("기간별 질병 통계 조회 - 기간: {}", period);
		return diseaseStatsRepository.findByPeriod(period);
	}

	// ICD 그룹별 질병 통계 조회
	public List<DiseaseStats> getDiseaseStatsByIcdGroup(String icdGroupName) {
		log.info("ICD 그룹별 질병 통계 조회 - 그룹: {}", icdGroupName);
		return diseaseStatsRepository.findByIcdGroupName(icdGroupName);
	}

	// 질병명으로 검색
	public List<DiseaseStats> searchDiseaseStatsByName(String icdName) {
		log.info("질병명 검색 - 검색어: {}", icdName);
		return diseaseStatsRepository.findByIcdNameContaining(icdName);
	}

	// 기간 + ICD 그룹으로 조회
	public List<DiseaseStats> getDiseaseStatsByPeriodAndGroup(String period, String icdGroupName) {
		log.info("기간 및 ICD 그룹별 질병 통계 조회 - 기간: {}, 그룹: {}", period, icdGroupName);
		return diseaseStatsRepository.findByPeriodAndIcdGroupName(period, icdGroupName);
	}

	// 모든 기간 목록 조회
	public List<String> getAvailablePeriods() {
		log.info("가능한 기간 목록 조회");
		return diseaseStatsRepository.findDistinctPeriods();
	}

	// 모든 ICD 그룹 목록 조회
	public List<String> getAvailableIcdGroups() {
		log.info("가능한 ICD 그룹 목록 조회");
		return diseaseStatsRepository.findDistinctIcdGroupNames();
	}

	// 상위 N개 질병 통계 조회
	public List<DiseaseStats> getTopDiseasesByPeriod(String period, int limit) {
		log.info("상위 {}개 질병 통계 조회 - 기간: {}", limit, period);
		List<DiseaseStats> allStats = diseaseStatsRepository.findTopDiseasesByPeriod(period);
		return allStats.stream().limit(limit).toList();
	}
}
