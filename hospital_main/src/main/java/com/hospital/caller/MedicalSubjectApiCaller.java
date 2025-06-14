package com.hospital.caller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.api.MedicalSubjectApiResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class MedicalSubjectApiCaller {

	@Value("${hospital.medicalSubject.api.base-url}")
	private String baseUrl;

	@Value("${hospital.medicalSubject.api.key}")
	private String serviceKey;

	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;

	// ✅ 생성자 주입 방식 (ObjectMapper는 Bean으로 등록되어 있음)
	public MedicalSubjectApiCaller(ObjectMapper objectMapper) {
		this.restTemplate = new RestTemplate();
		this.objectMapper = objectMapper;
	}

	/**
	 * ✅ 병원코드 기반 진료과목 정보 요청
	 * 
	 * @param apiPath     - 호출할 API 경로 (예: getDgsbjtInfo2.7)
	 * @param queryParams - 예: "ykiho=xxx" 형식의 쿼리 문자열
	 * @return 응답 객체 (MedicalSubjectApiResponse)
	 */
	public MedicalSubjectApiResponse callApi(String apiPath, String queryParams) {
		try {
			// 🔗 전체 API URL 구성
			String fullUrl = baseUrl + apiPath + "?serviceKey=" + serviceKey + "&_type=json&" + queryParams;

			// 📡 API 호출
			String response = restTemplate.getForObject(fullUrl, String.class);

			// 📦 JSON → DTO 객체 매핑
			return objectMapper.readValue(response, MedicalSubjectApiResponse.class);

		} catch (Exception e) {
			// ❌ 예외 처리 및 로그 출력
			log.error("❌ 진료과목 API 호출 실패: {}", e.getMessage(), e);
			throw new RuntimeException("진료과목 API 호출 중 오류 발생", e);
		}
	}
}
