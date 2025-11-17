package com.hospital.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "pharmacy")
public class Pharmacy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "pharmacy_name", nullable = false)
	private String name; // 약국명

	@Column(name = "address", length = 500)
	private String address; // 주소

	@Column(name = "phone")
	private String phone; // 대표 전화번호

	@Column(name = "fax")
	private String fax; // 팩스 번호

	@Column(name = "etc", length = 500)
	private String etc; // 비고 (휴게시간 등)

	@Column(name = "map_info", length = 500)
	private String mapInfo; // 약도/위치 설명

	@Column(name = "post_code1")
	private String postCode1; // 우편번호 앞자리

	@Column(name = "post_code2")
	private String postCode2; // 우편번호 뒷자리

	@Column(name = "latitude")
	private Double latitude; // 위도

	@Column(name = "longitude")
	private Double longitude; // 경도

	@Column(name = "ykiho", unique = true)
	private String ykiho; // 병원/약국 고유 식별자 (hpid)

	// === 운영 시간 (월요일) ===
	@Column(name = "mon_open")
	private String mondayOpen; // 월요일 오픈 시간

	@Column(name = "mon_close")
	private String mondayClose; // 월요일 마감 시간

	// === 운영 시간 (화요일) ===
	@Column(name = "tue_open")
	private String tuesdayOpen; // 화요일 오픈 시간

	@Column(name = "tue_close")
	private String tuesdayClose; // 화요일 마감 시간

	// === 운영 시간 (수요일) ===
	@Column(name = "wed_open")
	private String wednesdayOpen; // 수요일 오픈 시간

	@Column(name = "wed_close")
	private String wednesdayClose; // 수요일 마감 시간

	// === 운영 시간 (목요일) ===
	@Column(name = "thu_open")
	private String thursdayOpen; // 목요일 오픈 시간

	@Column(name = "thu_close")
	private String thursdayClose; // 목요일 마감 시간

	// === 운영 시간 (금요일) ===
	@Column(name = "fri_open")
	private String fridayOpen; // 금요일 오픈 시간

	@Column(name = "fri_close")
	private String fridayClose; // 금요일 마감 시간

	// === 운영 시간 (토요일) ===
	@Column(name = "sat_open")
	private String saturdayOpen; // 토요일 오픈 시간

	@Column(name = "sat_close")
	private String saturdayClose; // 토요일 마감 시간

	// === 운영 시간 (일요일) ===
	@Column(name = "sun_open")
	private String sundayOpen; // 일요일 오픈 시간

	@Column(name = "sun_close")
	private String sundayClose; // 일요일 마감 시간

	// === 운영 시간 (공휴일) ===
	@Column(name = "holiday_open")
	private String holidayOpen; // 공휴일 오픈 시간

	@Column(name = "holiday_close")
	private String holidayClose; // 공휴일 마감 시간

	
	//약국 데이터가 유효한지 검사
	public boolean isValid() {
		// 필수 필드 검증
		if (isEmptyString(ykiho) || isEmptyString(name)) {
			return false;
		}

		// 좌표 유효성 검사 (한국 좌표 범위)
		return latitude != null && longitude != null && latitude >= 33.0 && latitude <= 43.0 && longitude >= 124.0
				&& longitude <= 132.0;
	}

	
	//문자열이 비어있는지 검사 
	private boolean isEmptyString(String str) {
		return str == null || str.trim().isEmpty();
	}

}
