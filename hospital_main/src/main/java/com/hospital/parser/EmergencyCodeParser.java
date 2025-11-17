package com.hospital.parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hospital.dto.EmergencyApiItem;
import com.hospital.dto.EmergencyApiResponse;
import com.hospital.entity.EmergencyCode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmergencyCodeParser {

	/**
	 * EmergencyApiResponse에서 응급실 코드(hpid) 추출하여 EmergencyCode 엔티티 리스트로 변환
	 */
	public List<EmergencyCode> parseCode(EmergencyApiResponse apiResponse) {
		log.debug("응급실 코드 파싱 시작");

		if (apiResponse == null) {
			log.warn("API 응답이 null입니다.");
			return Collections.emptyList();
		}

		List<EmergencyCode> codes = extractAndConvertItems(apiResponse);
		log.debug("응급실 코드 파싱 완료 - 변환된 코드 수: {}", codes.size());
		return codes;
	}

	private List<EmergencyCode> extractAndConvertItems(EmergencyApiResponse response) {
		return Optional.ofNullable(response)
				.map(EmergencyApiResponse::getBody)
				.map(EmergencyApiResponse.Body::getItems)
				.map(EmergencyApiResponse.ApiItemsWrapper::getItem)
				.orElseGet(Collections::emptyList)
				.stream()
				.map(this::convertToEmergencyCode)
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	private EmergencyCode convertToEmergencyCode(EmergencyApiItem itemDto) {
		if (itemDto == null || itemDto.getHpid() == null || itemDto.getHpid().trim().isEmpty()) {
			log.warn("유효하지 않은 데이터 - hpid 없음");
			return null;
		}

		return EmergencyCode.builder()
				.emergencyCode(itemDto.getHpid())
				.build();
	}

}
