
package com.hospital.parser;

import com.hospital.dto.HospitalMainApiItem;
import com.hospital.dto.HospitalMainApiResponse;
import com.hospital.entity.HospitalMain;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional; // Optional 임포트
import java.util.stream.Collectors;

@Component
@Slf4j
public class HospitalMainApiParser {

	public List<HospitalMain> parseHospitals(HospitalMainApiResponse apiResponseDto) {
		log.debug("병원 데이터 파싱 시작");

	

		// 아이템 추출 및 변환
		List<HospitalMain> hospitals = extractAndConvertItems(apiResponseDto);

		log.debug("병원 데이터 파싱 완료: {}건", hospitals.size());
		return hospitals;
	}


	private List<HospitalMain> extractAndConvertItems(HospitalMainApiResponse response) {
		return Optional.ofNullable(response).map(HospitalMainApiResponse::getResponse)
				.map(HospitalMainApiResponse.Response::getBody).map(HospitalMainApiResponse.Body::getItems)
				.map(HospitalMainApiResponse.ApiItemsWrapper::getItem).orElseGet(ArrayList::new).stream()
				.map(this::convertToHospital).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private HospitalMain convertToHospital(HospitalMainApiItem itemDto) {
		if (itemDto == null || itemDto.getYkiho() == null || itemDto.getYkiho().trim().isEmpty()) {
			log.warn("유효하지 않은 병원 데이터: {}", itemDto);
			return null;
		}

		return HospitalMain.builder().hospitalCode(itemDto.getYkiho()).hospitalName(itemDto.getYadmNm())

				.hospitalAddress(itemDto.getAddr()).hospitalTel(itemDto.getTelno())

				.totalDoctors(itemDto.getDrTotCnt()).coordinateX(itemDto.getXPos()).coordinateY(itemDto.getYPos())
				.build();
	}
}