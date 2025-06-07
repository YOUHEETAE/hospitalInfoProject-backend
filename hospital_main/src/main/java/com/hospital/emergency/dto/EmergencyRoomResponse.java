package com.hospital.emergency.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

import java.util.List;

@Data
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class EmergencyRoomResponse {

    @XmlElement(name = "header")
    private Header header;

    @XmlElement(name = "body")
    private Body body;

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Header {
        @XmlElement(name = "resultCode")
        private String resultCode;

        @XmlElement(name = "resultMsg")
        private String resultMsg;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Body {
        @XmlElement(name = "items")
        private Items items;

        @XmlElement(name = "numOfRows")
        private Integer numOfRows;

        @XmlElement(name = "pageNo")
        private Integer pageNo;

        @XmlElement(name = "totalCount")
        private Integer totalCount;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Items {
        @XmlElement(name = "item")
        private List<Item> item;
    }

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Item {
        @XmlElement(name = "cnt")
        private Integer cnt;

        @XmlElement(name = "distance")
        private Double distance;

        @XmlElement(name = "dutyAddr")
        private String dutyAddr;

        @XmlElement(name = "dutyDiv")
        private String dutyDiv;

        @XmlElement(name = "dutyDivName")
        private String dutyDivName;

        @XmlElement(name = "dutyName")
        private String dutyName;

        @XmlElement(name = "dutyTel1")
        private String dutyTel1;

        @XmlElement(name = "endTime")
        private String endTime;

        @XmlElement(name = "hpid")
        private String hpid;

        @XmlElement(name = "latitude")
        private Double latitude;

        @XmlElement(name = "longitude")
        private Double longitude;

        @XmlElement(name = "rnum")
        private Integer rnum;

        @XmlElement(name = "startTime")
        private String startTime;
    }
}