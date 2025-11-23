package com.hospital.repository;

import com.hospital.entity.DiseaseStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DiseaseStatsRepository extends JpaRepository<DiseaseStats, Long> {

	// 기간별 질병 통계 조회
	List<DiseaseStats> findByPeriod(String period);

	// ICD 그룹별 질병 통계 조회
	List<DiseaseStats> findByIcdGroupName(String icdGroupName);

	// 질병명으로 검색 (부분 일치)
	List<DiseaseStats> findByIcdNameContaining(String icdName);

	// 기간 + ICD 그룹으로 조회
	List<DiseaseStats> findByPeriodAndIcdGroupName(String period, String icdGroupName);

	// 모든 기간 목록 조회 (중복 제거)
	@Query("SELECT DISTINCT d.period FROM DiseaseStats d ORDER BY d.period DESC")
	List<String> findDistinctPeriods();

	// 모든 ICD 그룹 목록 조회 (중복 제거)
	@Query("SELECT DISTINCT d.icdGroupName FROM DiseaseStats d ORDER BY d.icdGroupName")
	List<String> findDistinctIcdGroupNames();

	// 상위 N개 질병 통계 조회 (결과값 내림차순)
	@Query("SELECT d FROM DiseaseStats d WHERE d.period = :period ORDER BY CAST(d.resultValue AS long) DESC")
	List<DiseaseStats> findTopDiseasesByPeriod(@Param("period") String period);

}
