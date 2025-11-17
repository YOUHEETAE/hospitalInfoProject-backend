package com.hospital.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PharmacyApiItem {

    @JsonProperty("hpid")
    private String hpid; // 기관 ID

    @JsonProperty("dutyName")
    private String dutyName; // 약국명

    @JsonProperty("dutyAddr")
    private String dutyAddr; // 주소

    @JsonProperty("dutyTel1")
    private String dutyTel1; // 대표 전화번호

    @JsonProperty("dutyFax")
    private String dutyFax; // 팩스 번호

    @JsonProperty("dutyEtc")
    private String dutyEtc; // 비고 (휴게시간 등)

    @JsonProperty("dutyMapimg")
    private String dutyMapimg; // 약도 (위치 설명)

    @JsonProperty("postCdn1")
    private String postCdn1; // 우편번호 앞자리

    @JsonProperty("postCdn2")
    private String postCdn2; // 우편번호 뒷자리

    @JsonProperty("wgs84Lat")
    private Double wgs84Lat; // 위도 (WGS84)

    @JsonProperty("wgs84Lon")
    private Double wgs84Lon; // 경도 (WGS84)

    @JsonProperty("rnum")
    private Integer rnum; // 행 번호

    // === 운영 시간 (월요일) ===
    @JsonProperty("dutyTime1s")
    private String dutyTime1s; // 월요일 시작 시간

    @JsonProperty("dutyTime1c")
    private String dutyTime1c; // 월요일 종료 시간

    // === 운영 시간 (화요일) ===
    @JsonProperty("dutyTime2s")
    private String dutyTime2s; // 화요일 시작 시간

    @JsonProperty("dutyTime2c")
    private String dutyTime2c; // 화요일 종료 시간

    // === 운영 시간 (수요일) ===
    @JsonProperty("dutyTime3s")
    private String dutyTime3s; // 수요일 시작 시간

    @JsonProperty("dutyTime3c")
    private String dutyTime3c; // 수요일 종료 시간

    // === 운영 시간 (목요일) ===
    @JsonProperty("dutyTime4s")
    private String dutyTime4s; // 목요일 시작 시간

    @JsonProperty("dutyTime4c")
    private String dutyTime4c; // 목요일 종료 시간

    // === 운영 시간 (금요일) ===
    @JsonProperty("dutyTime5s")
    private String dutyTime5s; // 금요일 시작 시간

    @JsonProperty("dutyTime5c")
    private String dutyTime5c; // 금요일 종료 시간

    // === 운영 시간 (토요일) ===
    @JsonProperty("dutyTime6s")
    private String dutyTime6s; // 토요일 시작 시간

    @JsonProperty("dutyTime6c")
    private String dutyTime6c; // 토요일 종료 시간

    // === 운영 시간 (일요일) ===
    @JsonProperty("dutyTime7s")
    private String dutyTime7s; // 일요일 시작 시간

    @JsonProperty("dutyTime7c")
    private String dutyTime7c; // 일요일 종료 시간

    // === 운영 시간 (공휴일) ===
    @JsonProperty("dutyTime8s")
    private String dutyTime8s; // 공휴일 시작 시간

    @JsonProperty("dutyTime8c")
    private String dutyTime8c; // 공휴일 종료 시간
}
