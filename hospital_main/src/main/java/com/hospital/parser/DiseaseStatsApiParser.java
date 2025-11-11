package com.hospital.parser;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hospital.dto.DiseaseStatsApiItem;
import com.hospital.dto.DiseaseStatsApiResponse;
import com.hospital.entity.DiseaseStats;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DiseaseStatsApiParser {


	public List<DiseaseStats> parseDiseaseStats(DiseaseStatsApiResponse apiResponseDto) {
		log.debug("질병 현황 데이터 파싱 시작");
		// 아이템 추출 및 변환
		List<DiseaseStats> diseaseStats = extractAndConvertItems(apiResponseDto);

		log.debug("질병 현황 데이터 파싱 완료 : {}건", diseaseStats.size());
		return diseaseStats;
	}

	private List<DiseaseStats> extractAndConvertItems(DiseaseStatsApiResponse response) {

		return Optional.ofNullable(response).map(DiseaseStatsApiResponse::getResponse)
				.map(DiseaseStatsApiResponse.Response::getBody).map(DiseaseStatsApiResponse.Body::getItems)
				.map(DiseaseStatsApiResponse.ApiItemsWrapper::getItem).orElseGet(Collections::emptyList).stream()
				.map(this::convertToDiseaseStats).filter(Objects::nonNull).collect(Collectors.toList());
	}

	private DiseaseStats convertToDiseaseStats(DiseaseStatsApiItem itemDto) {

		if (itemDto == null) {
			log.warn("유효하지 않은 병원 데이터: {}", itemDto);
			return null;
		}

		return DiseaseStats.builder().period(itemDto.getPeriod()).icdGroupName(itemDto.getIcdGroupNm())
				.icdName(itemDto.getIcdNm()).resultValue(itemDto.getResultVal()).build();

	}
}
