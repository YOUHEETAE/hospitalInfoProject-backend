package com.hospital.parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hospital.dto.EmergencyLocationApiItem;
import com.hospital.dto.EmergencyLocationApiResponse;
import com.hospital.entity.EmergencyLocation;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EmergencyLocationApiParser {

	/**
	 * EmergencyApiResponse에서 응급실 코드(hpid) 추출하여 EmergencyLocation 엔티티 리스트로 변환
	 */
	public List<EmergencyLocation> parse(EmergencyLocationApiResponse apiResponse) {
		log.debug("응급실 코드 파싱 시작");

		if (apiResponse == null) {
			log.warn("API 응답이 null입니다.");
			return Collections.emptyList();
		}

		List<EmergencyLocation> Location = extractAndConvertItems(apiResponse);
		log.debug("응급실 코드 파싱 완료 - 변환된 코드 수: {}", Location.size());
		return Location;
	}

	private List<EmergencyLocation> extractAndConvertItems(EmergencyLocationApiResponse response) {
		return Optional.ofNullable(response).map(EmergencyLocationApiResponse::getBody)
				.map(EmergencyLocationApiResponse.Body::getItems)
				.map(EmergencyLocationApiResponse.ApiItemsWrapper::getItem).orElseGet(Collections::emptyList).stream()
				.map(this::convertToEmergencyLocation).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private EmergencyLocation convertToEmergencyLocation(EmergencyLocationApiItem itemDto) {
		if (itemDto == null || itemDto.getHpid() == null || itemDto.getHpid().trim().isEmpty()) {
			log.warn("유효하지 않은 데이터 - hpid 없음");
			return null;
		}

		return EmergencyLocation.builder().emergencyCode(itemDto.getHpid()).coordinateX(itemDto.getCoordinateX())
				.coordinateY(itemDto.getCoordinateY()).emergencyAddress(itemDto.getEmergencyAddress()).build();
	}

}
