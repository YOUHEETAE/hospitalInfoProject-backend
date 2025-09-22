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
@JsonIgnoreProperties(ignoreUnknown = true) // DTO에 정의되지 않은 필드는 무시
public class HospitalMainApiItem {

    @JsonProperty("ykiho")
    private String ykiho; // 요양기관기호 (API 필드명)

    @JsonProperty("yadmNm")
    private String yadmNm; // 요양기관명 (API 필드명)

    @JsonProperty("sidoCdNm")
    private String sidoCdNm; // 시도명 (API 필드명)

    @JsonProperty("sgguCdNm")
    private String sgguCdNm; // 시군구명 (API 필드명)

    @JsonProperty("addr")
    private String addr; // 주소 (API 필드명)

    @JsonProperty("telno")
    private String telno; // 전화번호 (API 필드명)

    @JsonProperty("hospUrl")
    private String hospUrl; // 홈페이지 (API 필드명)

    @JsonProperty("drTotCnt")
    private String drTotCnt; // 의사총수 (API 필드명)

    @JsonProperty("XPos")
    private double xPos; // X좌표 (API 필드명)

    @JsonProperty("YPos")
    private double yPos; // Y좌표 (API 필드명)
    
    @JsonProperty("mdeptGdrCnt")
    private String mdeptGdrCnt; // 의과 일반의 인원수
    
    @JsonProperty("mdeptIntnCnt")
    private String mdeptIntnCnt; // 의과 인턴 인원수
    
    @JsonProperty("mdeptResdntCnt")
    private String mdeptResdntCnt; // 의과 레지던트 인원수
    
    
    @JsonProperty("mdeptSdrCnt")
    private String mdeptSdrCnt; // 의과 전문의 인원수
    
    @JsonProperty("detyGdrCnt")
    private String detyGdrCnt; // 치과 일반의 인원수
    
    @JsonProperty("detyIntnCnt")
    private String detyIntnCnt; // 치과 인턴 인원수
    
    @JsonProperty("detyResdntCnt")
    private String detyResdntCnt; // 치과 레지던트 인원수
    
    @JsonProperty("detySdrCnt")
    private String detySdrCnt; // 치과 전문의 인원수
    
    @JsonProperty("cmdcGdrCnt")
    private String cmdcGdrCnt; // 한방 일반의 인원수
    
    @JsonProperty("cmdcIntnCnt")
    private String cmdcIntnCnt; // 한방 인턴 인원수
    
    @JsonProperty("cmdcResdntCnt")
    private String cmdcResdntCnt; // 한방 레지던트 인원수
    
    @JsonProperty("cmdcSdrCnt")
    private String cmdcSdrCnt; // 한방 전문의 인원수
    
    @JsonProperty("pnursCnt")
    private String pnursCnt; // 조산사 인원수
    
    
}