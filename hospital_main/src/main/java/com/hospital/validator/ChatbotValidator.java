package com.hospital.validator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hospital.dto.ChatbotResponse;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * 챗봇 응답 검증기
 */
@Slf4j
@Component
public class ChatbotValidator {

	// 유효한 응답 타입
	private static final Set<String> VALID_TYPES = Set.of(
		"suggest", "question", "inappropriate", "error"
	);

	// properties에서 주입
	@Value("${medical.subject.names}")
	private String subjectNames;

	// 초기화 후 Set으로 변환
	private Set<String> validDepartments;

	/**
	 * 진료과 목록 초기화
	 */
	@PostConstruct
	public void init() {
		this.validDepartments = Arrays.stream(subjectNames.split(","))
			.map(String::trim)
			.collect(Collectors.toSet());
		
		log.info("✅ 진료과 목록 로드 완료: {} 개", validDepartments.size());
	}

	/**
	 * 사용자 입력 메시지 검증
	 */
	public String validateUserMessage(String message) {
		if (message == null || message.trim().isEmpty()) {
			return "메시지를 입력해주세요.";
		}

		if (message.length() > 1000) {
			return "메시지가 너무 깁니다. 1000자 이내로 입력해주세요.";
		}

		return null; // 검증 통과
	}

	/**
	 * AI 응답 검증
	 */
	public String validateResponse(ChatbotResponse response) {
		// 1. 기본 필드 검증
		if (response.getType() == null || response.getMessage() == null) {
			log.warn("⚠️ 필수 필드 누락");
			return "응답 형식이 올바르지 않습니다.";
		}

		// 2. type 값 검증
		if (!VALID_TYPES.contains(response.getType())) {
			log.warn("⚠️ 잘못된 type: {}", response.getType());
			return "응답 형식이 올바르지 않습니다.";
		}

		// 3. recommendation 타입일 때 departments 검증
		if ("suggest".equals(response.getType())) {
			String departmentError = validateDepartments(response);
			if (departmentError != null) {
				return departmentError;
			}
		}

		return null; // 검증 통과
	}

	/**
	 * departments 검증
	 */
	private String validateDepartments(ChatbotResponse response) {
		List<String> departments = response.getDepartments();
		String message = response.getMessage();

		// 응급 상황이 아닌데 departments 없으면 에러
		if (!isEmergency(message) && (departments == null || departments.isEmpty())) {
			log.warn("⚠️ suggest이지만 departments 없음");
			return "진료과 정보가 누락되었습니다.";
		}

		// departments가 있으면 유효한 진료과인지 검증
		if (departments != null && !departments.isEmpty()) {
			for (String dept : departments) {
				if (!validDepartments.contains(dept)) {
					log.warn("⚠️ 잘못된 진료과: {}", dept);
					return "올바르지 않은 진료과입니다.";
				}
			}
		}

		return null; // 검증 통과
	}

	/**
	 * 응급 메시지인지 판별
	 */
	private boolean isEmergency(String message) {
		return message != null && (message.contains("119") || message.contains("응급실"));
	}
}
