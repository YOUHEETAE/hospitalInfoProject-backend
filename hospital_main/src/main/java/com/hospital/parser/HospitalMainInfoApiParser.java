
package com.hospital.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.HospitalMainApiItem; 
import com.hospital.dto.HospitalMainApiResponse; 
import com.hospital.entity.Hospital;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Optional 임포트

@Component
public class HospitalMainInfoApiParser {

    private final ObjectMapper objectMapper;

    public HospitalMainInfoApiParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // ★★★ JsonNode에서 HospitalMainApiResponse로 매개변수 타입 변경 ★★★
    public List<Hospital> parseHospitals(HospitalMainApiResponse apiResponseDto) {
        List<Hospital> hospitals = new ArrayList<>();

        // API 응답 구조에 따라 items 객체에서 item 리스트를 추출
        // items가 null이거나 item 리스트가 null일 수 있으므로 Optional로 안전하게 처리
        Optional.ofNullable(apiResponseDto)
                .map(HospitalMainApiResponse::getResponse)
                .map(HospitalMainApiResponse.Response::getBody)
                .map(HospitalMainApiResponse.Body::getItems)
                .map(HospitalMainApiResponse.ApiItemsWrapper::getItem)
                .orElseGet(ArrayList::new) // item 리스트가 없으면 빈 리스트 반환
                .forEach(itemDto -> {
                    Hospital hospital = new Hospital();
                    // HospitalMainApiItem의 필드를 Hospital 엔티티의 필드로 매핑
                    hospital.setHospitalCode(itemDto.getYkiho());
                    hospital.setHospitalName(itemDto.getYadmNm());
                    hospital.setProvinceName(itemDto.getSidoCdNm());
                    hospital.setDistrictName(itemDto.getSgguCdNm());
                    hospital.setHospitalAddress(itemDto.getAddr());
                    hospital.setHospitalTel(itemDto.getTelno());
                    hospital.setHospitalHomepage(itemDto.getHospUrl());
                    hospital.setDoctorNum(itemDto.getDrTotCnt());
                    hospital.setCoordinateX(itemDto.getXPos());
                    hospital.setCoordinateY(itemDto.getYPos());
                  

                    hospitals.add(hospital);
                });

        return hospitals;
    }
}