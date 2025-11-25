package com.hospital.converter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hospital.analyzer.DiseaseTrendAnalyzer;
import com.hospital.analyzer.RiskLevel;
import com.hospital.dto.DiseaseStatsWebResponse;
import com.hospital.entity.DiseaseStats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class DiseaseStatsConverter {

	private final DiseaseTrendAnalyzer trendAnalyzer;

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
		LocalDate today = LocalDate.now();
		LocalDate thisWeekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

		return entities.stream()
				.map(this::convertToWeeklyData)
				.filter(data -> {
					try {
						LocalDate dataDate = LocalDate.parse(data.getPeriod());
						return dataDate.isBefore(thisWeekMonday);
					} catch (Exception e) {
						log.warn("날짜 파싱 실패로 데이터 제외: {}", data.getPeriod());
						return false;
					}
				})
				.sorted((a, b) -> a.getPeriod().compareTo(b.getPeriod()))
				.collect(Collectors.toList());

	}

	public DiseaseStatsWebResponse convertToDTO(List<DiseaseStats> entities) {
		if (entities == null || entities.isEmpty()) {
			return null;
		}

		DiseaseStats first = entities.get(0);

		List<DiseaseStats> weeklyEntities = entities.stream().filter(e -> !"계".equals(e.getPeriod()))
				.collect(Collectors.toList());

		List<DiseaseStatsWebResponse.WeeklyData> weeklyDataList = convertToWeeklyDataList(weeklyEntities);

		Integer totalCount = weeklyDataList.stream()
				.mapToInt(DiseaseStatsWebResponse.WeeklyData::getCount)
				.sum();

		RiskLevel riskLevel = trendAnalyzer.analyzeTrend(
				first.getIcdName(),
				first.getIcdGroupName(),
				weeklyDataList
		);

		Integer recentChange = null;
		Double recentChangeRate = null;
		if (weeklyDataList.size() >= 2) {
			int lastWeek = weeklyDataList.get(weeklyDataList.size() - 1).getCount();
			int prevWeek = weeklyDataList.get(weeklyDataList.size() - 2).getCount();
			recentChange = lastWeek - prevWeek;
			if (prevWeek > 0) {
				recentChangeRate = (double) recentChange / prevWeek;
				recentChangeRate = Math.round(recentChangeRate * 1000.0) / 1000.0;
			} else {
				recentChangeRate = lastWeek > 0 ? 1.0 : 0.0;
			}
		}

		return DiseaseStatsWebResponse.builder()
				.icdGroupName(first.getIcdGroupName())
				.icdName(first.getIcdName())
				.weeklyData(weeklyDataList)
				.totalCount(totalCount)
				.riskLevel(riskLevel)
				.recentChange(recentChange)
				.recentChangeRate(recentChangeRate)
				.build();
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