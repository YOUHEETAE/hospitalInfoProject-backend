package com.hospital.emergency.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "response") // XML 루트 엘리먼트가 <response> 임을 명시
public class EmergencyRoomApiResponse {
    @JacksonXmlProperty(localName = "header") // XML의 <header> 엘리먼트에 매핑
    private Header header;
    @JacksonXmlProperty(localName = "body")   // XML의 <body> 엘리먼트에 매핑
    private Body body;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode") // XML의 <resultCode> 엘리먼트에 매핑
        private String resultCode;
        @JacksonXmlProperty(localName = "resultMsg")  // XML의 <resultMsg> 엘리먼트에 매핑
        private String resultMsg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Body {
        @JacksonXmlProperty(localName = "items") // XML의 <items> 엘리먼트에 매핑
        private Items items;
        @JacksonXmlProperty(localName = "numOfRows") // XML의 <numOfRows> 엘리먼트에 매핑
        private Integer numOfRows;
        @JacksonXmlProperty(localName = "pageNo") // XML의 <pageNo> 엘리먼트에 매핑
        private Integer pageNo;
        @JacksonXmlProperty(localName = "totalCount") // XML의 <totalCount> 엘리먼트에 매핑
        private Integer totalCount;
    }

    // <items> 아래 <item>이 단일 객체일 수도 있고 리스트일 수도 있어서 Items 클래스를 별도로 정의합니다.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Items {
        // <items> 태그 아래 <item> 태그가 여러 개 올 경우 리스트로 매핑됩니다.
        // 만약 <items> 태그 바로 아래 <item> 태그 없이 내용물이 있다면 @JacksonXmlElementWrapper(useWrapping = false)를 사용해야 하지만,
        // API 응답 구조상 <item> 태그로 묶여있으므로 현재는 필요 없습니다.
        @JacksonXmlProperty(localName = "item")
        private java.util.List<EmergencyRoomApiItem> item;
    }
}