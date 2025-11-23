package com.hospital.service;

import com.hospital.async.EmergencyAsyncRunner;
import com.hospital.caller.EmergencyApiCaller;
import com.hospital.dto.EmergencyApiResponse;
import com.hospital.entity.EmergencyLocation;
import com.hospital.parser.EmergencyCodeParser;
import com.hospital.repository.EmergencyCodeRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EmergencyApiService {

	private final EmergencyApiCaller emergencyApiCaller;
	private final EmergencyCodeParser emergencyCodeParser;
	private final EmergencyCodeRepository emergencyCodeRepository;

	@Autowired
	public EmergencyApiService(EmergencyApiCaller emergencyApiCaller,
	                           EmergencyCodeParser emergencyCodeParser,
	                           EmergencyCodeRepository emergencyCodeRepository) {
		this.emergencyApiCaller = emergencyApiCaller;
		this.emergencyCodeParser = emergencyCodeParser;
		this.emergencyCodeRepository = emergencyCodeRepository;
	}

	/**
	 * 응급실 코드 데이터 수집 및 저장
	 */
	public int saveEmergencyCodes() {
		log.info("응급실 코드 데이터 수집 시작");

		// 기존 데이터 삭제
		emergencyCodeRepository.deleteAllEmergencyCodes();
		log.info("기존 응급실 코드 데이터 전체 삭제 완료");

		try {
			// API 호출 (전국 응급실 데이터 - pageNo=1, numOfRows=500)
			EmergencyApiResponse response = emergencyApiCaller.callEmergencyApiByCityPage(1, 500);

			if (response == null || response.getBody() == null) {
				log.warn("API 응답이 없습니다.");
				return 0;
			}

			// 응급실 코드 파싱
			List<EmergencyLocation> emergencyCodes = emergencyCodeParser.parseCode(response);

			if (emergencyCodes.isEmpty()) {
				log.warn("파싱된 응급실 코드가 없습니다.");
				return 0;
			}

			// 배치 저장
			emergencyCodeRepository.saveAll(emergencyCodes);
			log.info("응급실 코드 데이터 저장 완료 - {}건", emergencyCodes.size());

			return emergencyCodes.size();

		} catch (Exception e) {
			log.error("응급실 코드 데이터 수집 실패: {}", e.getMessage(), e);
			throw new RuntimeException("응급실 코드 데이터 수집 실패", e);
		}
	}

}
