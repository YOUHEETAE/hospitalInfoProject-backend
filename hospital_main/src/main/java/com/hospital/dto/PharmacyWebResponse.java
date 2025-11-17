package com.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PharmacyWebResponse {

	// 기본 정보
	private String pharmacyCode; // 약국 고유 코드 (hpid)
	private String pharmacyName; // 약국명
	private String pharmacyAddress; // 주소
	private String pharmacyTel; // 전화번호
	private String pharmacyFax; // 팩스 번호
	private String pharmacyEtc; // 비고 (휴게시간 등)
	private String pharmacyMapInfo; // 약도/위치 설명

	// 좌표 정보
	private Double coordinateX; // 경도
	private Double coordinateY; // 위도

	// 오늘 운영 시간
	private String todayOpen; // 오늘 오픈 시간
	private String todayClose; // 오늘 마감 시간

	// 주간 운영 시간
	private Map<String, Map<String, String>> weeklySchedule; // 요일별 운영 시간
}
