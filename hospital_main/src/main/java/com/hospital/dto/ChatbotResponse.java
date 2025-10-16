package com.hospital.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Gemini AI 응답 파싱용 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotResponse {
    
    /**
     * 응답 타입: question, recommendation, multiple, emergency, inappropriate, completed
     */
    @JsonProperty("type")
    private String type;

    /**
     * AI 메시지
     */
    @JsonProperty("message")
    private String message;

    /**
     * 추가 질문 목록 (type=question일 때)
     */
    @JsonProperty("questions")
    private List<String> questions;

    /**
     * 가능한 진료과 목록 (type=question일 때)
     */
    @JsonProperty("possibleDepartments")
    private List<String> possibleDepartments;

    /**
     * 추천 진료과 (type=recommendation일 때)
     */
    @JsonProperty("department")
    private String department;

    /**
     * 확신도: high, medium, low (type=recommendation일 때)
     */
    @JsonProperty("confidence")
    private String confidence;

    /**
     * 추천 이유 (type=recommendation일 때)
     */
    @JsonProperty("reason")
    private String reason;

    /**
     * 여러 진료과 목록 (type=multiple일 때)
     */
    @JsonProperty("departments")
    private List<String> departments;

    /**
     * 추가 안내 (type=multiple일 때)
     */
    @JsonProperty("guidance")
    private String guidance;

    /**
     * 응급 레벨 (type=emergency일 때)
     */
    @JsonProperty("urgencyLevel")
    private String urgencyLevel;

    /**
     * 응급 연락처 (type=emergency일 때)
     */
    @JsonProperty("emergencyNumber")
    private String emergencyNumber;

    /**
     * 대화 계속 가능 여부 (type=completed일 때)
     */
    @JsonProperty("canContinue")
    private Boolean canContinue;

    /**
     * 응답 타입 확인 메서드
     */
    public boolean isQuestion() {
        return "question".equals(type);
    }

    public boolean isRecommendation() {
        return "recommendation".equals(type);
    }

    public boolean isMultiple() {
        return "multiple".equals(type);
    }

    public boolean isEmergency() {
        return "emergency".equals(type);
    }

    public boolean isInappropriate() {  // ← 추가!
        return "inappropriate".equals(type);
    }

    public boolean isCompleted() {
        return "completed".equals(type);
    }
}