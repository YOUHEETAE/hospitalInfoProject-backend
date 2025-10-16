package com.hospital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.caller.AIApiCaller;
import com.hospital.dto.ChatbotResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 챗봇 비즈니스 로직 서비스
 */
@Slf4j
@Service

public class ChatbotService {

	private final AIApiCaller aiApiCaller;
	private final ObjectMapper objectMapper;

	@Value("${chatbot.system-prompt-file}")
	private Resource systemPromptFile;

	private String systemPrompt;

	public ChatbotService(AIApiCaller aiApiCaller, ObjectMapper objectMapper) {
		this.aiApiCaller = aiApiCaller;
		this.objectMapper = objectMapper;
	}

	/**
	 * 시스템 프롬프트 로드 (서버 시작 시 1회)
	 */
	@PostConstruct
	public void init() {
		try {
			this.systemPrompt = systemPromptFile.getContentAsString(StandardCharsets.UTF_8);
			log.info("✅ 시스템 프롬프트 로드 완료: {} bytes", systemPrompt.length());
		} catch (IOException e) {
			log.error("❌ 시스템 프롬프트 로드 실패", e);
			throw new RuntimeException("시스템 프롬프트 파일을 읽을 수 없습니다", e);
		}
	}

	/**
	 * 챗봇 메시지 처리 (메인 메서드)
	 */
	public ChatbotResponse chat(String userMessage) {
		log.info("💬 챗봇 요청: {}", userMessage);

		try {
			// 1. 시스템 프롬프트 + 사용자 메시지 결합
			String fullMessage = buildFullMessage(userMessage);

			// 2. Gemini API 호출 (Caller 사용)
			String aiResponseText = aiApiCaller.generateContent(fullMessage);

			if (aiResponseText == null || aiResponseText.isEmpty()) {
				log.warn("⚠️ AI 응답이 비어있습니다");
				return createErrorResponse("응답을 생성할 수 없습니다.");
			}

			// 3. JSON 파싱
			ChatbotResponse chatbotResponse = parseAiResponse(aiResponseText);

			log.info("✅ 챗봇 응답 완료: type={}", chatbotResponse.getType());
			return chatbotResponse;

		} catch (Exception e) {
			log.error("❌ 챗봇 처리 중 오류 발생", e);
			return createErrorResponse("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}
	}

	/**
	 * 대화 히스토리 포함 채팅
	 */
	public ChatbotResponse chatWithHistory(String userMessage, String conversationHistory) {
		log.info("💬 챗봇 요청 (히스토리 포함): {}", userMessage);

		try {
			// 시스템 프롬프트 + 대화 히스토리 + 새 메시지
			String fullMessage = buildFullMessageWithHistory(userMessage, conversationHistory);

			// Gemini API 호출 (Caller 사용)
			String aiResponseText = aiApiCaller.generateContent(fullMessage);

			if (aiResponseText == null || aiResponseText.isEmpty()) {
				return createErrorResponse("응답을 생성할 수 없습니다.");
			}

			ChatbotResponse chatbotResponse = parseAiResponse(aiResponseText);
			log.info("✅ 챗봇 응답 완료 (히스토리 포함): type={}", chatbotResponse.getType());
			return chatbotResponse;

		} catch (Exception e) {
			log.error("❌ 챗봇 처리 중 오류 발생 (히스토리 포함)", e);
			return createErrorResponse("일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
		}
	}

	/**
	 * 시스템 프롬프트 + 사용자 메시지 결합
	 */
	private String buildFullMessage(String userMessage) {
		return systemPrompt + "\n\n===사용자 메시지===\n" + userMessage;
	}

	/**
	 * 대화 히스토리 포함 메시지 구성
	 */
	private String buildFullMessageWithHistory(String userMessage, String history) {
		return systemPrompt + "\n\n===이전 대화===\n" + history + "\n\n===사용자 메시지===\n" + userMessage;
	}

	/**
	 * AI 응답 텍스트를 ChatbotResponse로 파싱
	 */
	private ChatbotResponse parseAiResponse(String responseText) {
		try {
			// JSON 부분만 추출 (``` 코드 블록 제거)
			String jsonText = extractJson(responseText);

			// JSON 파싱
			return objectMapper.readValue(jsonText, ChatbotResponse.class);

		} catch (Exception e) {
			log.error("❌ JSON 파싱 실패: {}", responseText, e);

			// 파싱 실패 시 일반 메시지로 처리
			return ChatbotResponse.builder().type("recommendation").message(responseText).department("내과")
					.confidence("low").build();
		}
	}

	/**
	 * 응답에서 JSON 부분만 추출
	 */
	private String extractJson(String text) {
		// ```json ... ``` 형태 제거
		text = text.trim();

		if (text.startsWith("```json")) {
			text = text.substring(7);
		} else if (text.startsWith("```")) {
			text = text.substring(3);
		}

		if (text.endsWith("```")) {
			text = text.substring(0, text.length() - 3);
		}

		return text.trim();
	}

	/**
	 * 에러 응답 생성
	 */
	private ChatbotResponse createErrorResponse(String errorMessage) {
		return ChatbotResponse.builder().type("error").message(errorMessage).build();
	}
}