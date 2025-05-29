package com.hospital.parser; // 요청하신 패키지

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException; // 예외 처리를 위해 필요
import com.hospital.entity.Hospital; 
import org.springframework.stereotype.Component; // 스프링 빈으로 등록하기 위해 필요

import java.util.ArrayList;
import java.util.List;

@Component 
public class HospitalMainInfoApiParser { // 요청하신 클래스 이름

    private final ObjectMapper objectMapper; // JSON 파싱을 위해 필요

   
    public HospitalMainInfoApiParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Hospital> parseHospitals(JsonNode rootNode) throws JsonProcessingException {
        List<Hospital> hospitals = new ArrayList<>();
        JsonNode itemsNode = rootNode.path("response").path("body").path("items").path("item");

        if (itemsNode.isArray()) {
            for (JsonNode itemNode : itemsNode) {
                // JsonNode를 Hospital 엔티티로 변환
                Hospital hospital = objectMapper.treeToValue(itemNode, Hospital.class);
                hospitals.add(hospital);
            }
        } else if (itemsNode.isObject() && !itemsNode.isMissingNode()) {
            // 결과가 단일 객체일 경우 (item이 1개일 때)
            Hospital hospital = objectMapper.treeToValue(itemsNode, Hospital.class);
            hospitals.add(hospital);
        }
       

        return hospitals;
    }
}