package com.hospital.converter;

import com.hospital.dto.PharmacyWebResponse;
import com.hospital.entity.Pharmacy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PharmacyConverter {

	/**
	 * Entity -> DTO 변환
	 */
	public PharmacyWebResponse convertToDTO(Pharmacy pharmacy) {
		if (pharmacy == null) {
			return null;
		}

		// 오늘 요일 확인
		DayOfWeek today = LocalDate.now().getDayOfWeek();

		// 오늘의 운영 시간 계산
		Map<String, String> todaySchedule = getTodaySchedule(pharmacy, today);

		return PharmacyWebResponse.builder()
				// 기본 정보
				.pharmacyCode(pharmacy.getYkiho())
				.pharmacyName(pharmacy.getName())
				.pharmacyAddress(pharmacy.getAddress())
				.pharmacyTel(pharmacy.getPhone())
				.pharmacyFax(pharmacy.getFax())
				.pharmacyEtc(pharmacy.getEtc())
				.pharmacyMapInfo(pharmacy.getMapInfo())

				// 좌표 정보
				.coordinateX(pharmacy.getLongitude())
				.coordinateY(pharmacy.getLatitude())

				// 오늘 운영 시간
				.todayOpen(formatTime(todaySchedule.get("open")))
				.todayClose(formatTime(todaySchedule.get("close")))

				// 주간 운영 시간
				.weeklySchedule(convertToWeeklySchedule(pharmacy))
				.build();
	}

	/**
	 * Entity List -> DTO List 변환
	 */
	public List<PharmacyWebResponse> toResponseList(List<Pharmacy> pharmacies) {
		if (pharmacies == null) {
			return null;
		}

		return pharmacies.stream()
				.map(this::convertToDTO)
				.collect(Collectors.toList());
	}

	/**
	 * 오늘의 운영 시간 가져오기
	 */
	private Map<String, String> getTodaySchedule(Pharmacy pharmacy, DayOfWeek today) {
		Map<String, String> schedule = new LinkedHashMap<>();

		switch (today) {
			case MONDAY:
				schedule.put("open", pharmacy.getMondayOpen());
				schedule.put("close", pharmacy.getMondayClose());
				break;
			case TUESDAY:
				schedule.put("open", pharmacy.getTuesdayOpen());
				schedule.put("close", pharmacy.getTuesdayClose());
				break;
			case WEDNESDAY:
				schedule.put("open", pharmacy.getWednesdayOpen());
				schedule.put("close", pharmacy.getWednesdayClose());
				break;
			case THURSDAY:
				schedule.put("open", pharmacy.getThursdayOpen());
				schedule.put("close", pharmacy.getThursdayClose());
				break;
			case FRIDAY:
				schedule.put("open", pharmacy.getFridayOpen());
				schedule.put("close", pharmacy.getFridayClose());
				break;
			case SATURDAY:
				schedule.put("open", pharmacy.getSaturdayOpen());
				schedule.put("close", pharmacy.getSaturdayClose());
				break;
			case SUNDAY:
				schedule.put("open", pharmacy.getSundayOpen());
				schedule.put("close", pharmacy.getSundayClose());
				break;
		}

		return schedule;
	}

	/**
	 * 주간 운영 시간 생성
	 */
	private Map<String, Map<String, String>> convertToWeeklySchedule(Pharmacy pharmacy) {
		Map<String, Map<String, String>> schedule = new LinkedHashMap<>();

		schedule.put("월요일", createDaySchedule(pharmacy.getMondayOpen(), pharmacy.getMondayClose()));
		schedule.put("화요일", createDaySchedule(pharmacy.getTuesdayOpen(), pharmacy.getTuesdayClose()));
		schedule.put("수요일", createDaySchedule(pharmacy.getWednesdayOpen(), pharmacy.getWednesdayClose()));
		schedule.put("목요일", createDaySchedule(pharmacy.getThursdayOpen(), pharmacy.getThursdayClose()));
		schedule.put("금요일", createDaySchedule(pharmacy.getFridayOpen(), pharmacy.getFridayClose()));
		schedule.put("토요일", createDaySchedule(pharmacy.getSaturdayOpen(), pharmacy.getSaturdayClose()));
		schedule.put("일요일", createDaySchedule(pharmacy.getSundayOpen(), pharmacy.getSundayClose()));
		schedule.put("공휴일", createDaySchedule(pharmacy.getHolidayOpen(), pharmacy.getHolidayClose()));

		return schedule;
	}

	/**
	 * 요일별 운영 시간 맵 생성
	 */
	private Map<String, String> createDaySchedule(String startTime, String endTime) {
		Map<String, String> daySchedule = new LinkedHashMap<>();
		daySchedule.put("open", formatTime(startTime));
		daySchedule.put("close", formatTime(endTime));
		return daySchedule;
	}

	/**
	 * 시간 포맷팅 (HHMM -> HH:MM)
	 */
	private String formatTime(String timeStr) {
		if (timeStr == null || timeStr.trim().isEmpty()) {
			return "";
		}

		// 4자리 숫자 형식인 경우만 변환 (예: "0900" -> "09:00")
		if (timeStr.matches("\\d{4}")) {
			return timeStr.substring(0, 2) + ":" + timeStr.substring(2, 4);
		}

		return timeStr;
	}
}
