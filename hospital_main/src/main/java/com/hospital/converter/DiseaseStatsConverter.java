package com.hospital.converter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hospital.dto.DiseaseStatsWebResponse;
import com.hospital.entity.DiseaseStats;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DiseaseStatsConverter {

	public DiseaseStatsConverter() {

	}

	private DiseaseStatsWebResponse.WeeklyData convertToWeeklyData(DiseaseStats entity) {
		return DiseaseStatsWebResponse.WeeklyData.builder().period(convertPeriodToDate(entity.getPeriod()))
				.count(convertStringToInteger(entity.getResultValue())).build();

	}

	private Integer convertStringToInteger(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0;
		}
		try {
			String cleanValue = value.trim().replace(",", "");
			return Integer.parseInt(cleanValue.trim());
		} catch (NumberFormatException e) {
			log.warn("숫자 변환 실패 : {}", value);
			return 0;
		}
	}

	private String convertPeriodToDate(String period) {
		if (period == null || period.trim().isEmpty()) {
			return "";
		}
		try {
			String yearStr = period.substring(0, 4);
			String weekStr = period.substring(6).replace("주", "").trim();
			int year = Integer.parseInt(yearStr);
			int week = Integer.parseInt(weekStr);
			LocalDate firstDay = LocalDate.of(year, 1, 1);
			LocalDate firstMonday = firstDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
			LocalDate targetDate = firstMonday.plusWeeks(week - 1);
			return targetDate.toString();
		} catch (Exception e) {
			log.warn("날짜 변환 실패: {}", period, e);
			return period; // 실패 시 원본 반환
		}
	}

	private List<DiseaseStatsWebResponse.WeeklyData> convertToWeeklyDataList(List<DiseaseStats> entities) {

		return entities.stream().map(this::convertToWeeklyData).sorted((a, b) -> a.getPeriod().compareTo(b.getPeriod()))
				.collect(Collectors.toList());

	}

	public DiseaseStatsWebResponse convertToDTO(List<DiseaseStats> entities) {
		if (entities == null || entities.isEmpty()) {
			return null;
		}

		DiseaseStats first = entities.get(0);

		// "계" 데이터 찾기
		Integer totalCount = entities.stream().filter(e -> "계".equals(e.getPeriod())).findFirst()
				.map(e -> convertStringToInteger(e.getResultValue())).orElse(0);

		// "계" 제외한 주차 데이터만
		List<DiseaseStats> weeklyEntities = entities.stream().filter(e -> !"계".equals(e.getPeriod()))
				.collect(Collectors.toList());

		return DiseaseStatsWebResponse.builder().icdGroupName(first.getIcdGroupName()).icdName(first.getIcdName())
				.weeklyData(convertToWeeklyDataList(weeklyEntities)).totalCount(totalCount).build();
	}

	public List<DiseaseStatsWebResponse> convertToDtos(List<DiseaseStats> entities) {
		if (entities == null || entities.isEmpty()) {
			return List.of();
		}

		// Step 1: 그룹화
		Map<String, List<DiseaseStats>> groupedData = entities.stream()
				.collect(Collectors.groupingBy(entity -> entity.getIcdGroupName() + "|" + entity.getIcdName()));

		// Step 2: 각 그룹을 DTO로 변환 + 정렬
		return groupedData.values().stream().map(this::convertToDTO)
				.sorted((a, b) -> b.getTotalCount().compareTo(a.getTotalCount())) // ✅ totalCount 내림차순
				.collect(Collectors.toList());
	}

}