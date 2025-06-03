package com.hospital.emergency.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyRoomApiItem {
    @JacksonXmlProperty(localName = "cnt")
    private Integer cnt; // 응급실 병상수 (진료과목코드, 실제 응답에서는 'cnt'로 표시됨)
    @JacksonXmlProperty(localName = "distance")
    private Double distance; // 요청 좌표와의 거리 (km)
    @JacksonXmlProperty(localName = "dutyAddr")
    private String dutyAddr; // 주소
    @JacksonXmlProperty(localName = "dutyDiv")
    private String dutyDiv; // 진료구분 (A:종합병원, B:병원 등)
    @JacksonXmlProperty(localName = "dutyDivName")
    private String dutyDivName; // 진료구분명
    @JacksonXmlProperty(localName = "dutyName")
    private String dutyName; // 기관명
    @JacksonXmlProperty(localName = "dutyTel1")
    private String dutyTel1; // 대표전화1
    @JacksonXmlProperty(localName = "endTime")
    private String endTime; // 진료종료시간 (HHMM)
    @JacksonXmlProperty(localName = "hpid")
    private String hpid; // 기관ID (고유 식별자)
    @JacksonXmlProperty(localName = "latitude")
    private Double latitude; // WGS84 위도
    @JacksonXmlProperty(localName = "longitude")
    private Double longitude; // WGS84 경도
    @JacksonXmlProperty(localName = "rnum")
    private Integer rnum; // 순번
    @JacksonXmlProperty(localName = "startTime")
    private String startTime; // 진료시작시간 (HHMM)

    // API 응답에 따라 다른 필요한 필드들도 추가할 수 있습니다.
    // 예를 들어, emClsName (응급의료기관 구분명), emrmChrtCnt (응급실 차트수), etc.
}