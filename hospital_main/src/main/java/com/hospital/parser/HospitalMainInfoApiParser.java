package com.hospital.parser; // 요청하신 패키지

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException; // 예외 처리를 위해 필요
import com.hospital.entity.Hospital; // **지금은 Hospital 엔티티를 임포트합니다.** (다음 단계에서 HospitalBasicInfo로 변경 예정)
import org.springframework.stereotype.Component; // 스프링 빈으로 등록하기 위해 필요

import java.util.ArrayList;
import java.util.List;

@Component // 이 클래스를 스프링 빈으로 등록하여 다른 곳에서 주입받아 사용할 수 있도록 합니다.
public class HospitalMainInfoApiParser { // 요청하신 클래스 이름

    private final ObjectMapper objectMapper; // JSON 파싱을 위해 필요

    // 생성자 주입을 통해 ObjectMapper를 주입받습니다.
    // 이 객체는 AppConfig에서 빈으로 등록되었기 때문에 스프링이 자동으로 주입해줍니다.
    public HospitalMainInfoApiParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * API 응답 JsonNode에서 병원 기본 정보 리스트를 파싱하여 반환합니다.
     * `ObjectMapper`의 `FAIL_ON_UNKNOWN_PROPERTIES` 설정은 AppConfig에서 이미 되어있으므로,
     * Hospital 엔티티에 없는 필드는 자동으로 무시됩니다.
     *
     * @param rootNode API 응답의 루트 JsonNode
     * @return 파싱된 Hospital 엔티티 리스트 (현재는 Hospital, 추후 HospitalBasicInfo로 변경 예정)
     * @throws JsonProcessingException JSON 매핑 오류 시
     */
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
        // itemsNode.isMissingNode()이거나 비어있을 때는 빈 리스트를 반환하거나 아무것도 추가하지 않습니다.
        // 현재 로직은 이 경우에 currentBatch가 비어있게 되어 자연스럽게 처리됩니다.

        return hospitals;
    }
}