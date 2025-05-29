package com.hospital.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hospital {
	    @JsonProperty("ykiho") // JSON의 "ykiho" 필드를 hospitalCode에 매핑
	    private String hospitalCode;

	    @JsonProperty("yadmNm") // JSON의 "yadmNm" 필드를 hospitalName에 매핑
	    private String hospitalName;

	    @JsonProperty("sidoCdNm") // JSON의 "sidoCdNm" 필드를 provinceName에 매핑
	    private String provinceName;

	    @JsonProperty("sgguCdNm") // JSON의 "sgguCdNm" 필드를 districtName에 매핑
	    private String districtName;

	    @JsonProperty("addr") // JSON의 "addr" 필드를 hospitalAddress에 매핑 (이것이 핵심!)
	    private String hospitalAddress;

	    @JsonProperty("telno") // JSON의 "telno" 필드를 hospitalTel에 매핑
	    private String hospitalTel;

	    // API 스펙을 다시 확인하여 "hospUrl"인지 "hmpg"인지 정확하게 설정하세요.
	    // 대부분의 공공데이터포털은 "hmpg"를 사용합니다.
	    @JsonProperty("hospUrl") // 또는 @JsonProperty("hmpg")
	    private String hospitalHomepage;

	    @JsonProperty("drTotCnt") // JSON의 "drTotCnt" 필드를 doctorNum에 매핑
	    private int doctorNum;

	    @JsonProperty("XPos") // JSON의 "XPos" 필드를 coordinateX에 매핑
	    private double coordinateX;

	    @JsonProperty("YPos") // JSON의 "YPos" 필드를 coordinateY에 매핑
	    private double coordinateY;
	
	
	public Hospital(String hospitalCode, String hospitalName, String provinceName, String districtName, String hospitalAddress,
			        String hospitalTel, String hospitalHomepage, int doctorNum, double coordinateX, double coordinateY) {
		this.hospitalCode = hospitalCode;
		this.hospitalName = hospitalName;
		this.provinceName = provinceName;
		this.districtName = districtName;
		this.hospitalAddress = hospitalAddress;
		this.hospitalTel = hospitalTel;
		this.hospitalHomepage = hospitalHomepage;
		this.doctorNum = doctorNum;
		this.coordinateX = coordinateX;
		this.coordinateY = coordinateY;
	}
	
}