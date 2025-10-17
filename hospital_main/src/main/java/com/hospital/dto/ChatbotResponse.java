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
     * 응답 타입: question, recommendation, inappropriate
     */
    @JsonProperty("type")
    private String type;

    /**
     * AI 메시지
     */
    @JsonProperty("message")
    private String message;
    

    /**
     * 여러 진료과 목록(recommendation 일 때)
     */
    @JsonProperty("departments")
    private List<String> departments;

    /**
     * 응답 타입 확인 메서드
     */
    public boolean isQuestion() {
        return "question".equals(type);
    }

    public boolean isRecommendation() {
        return "recommendation".equals(type);
    }

    public boolean isInappropriate() {  
        return "inappropriate".equals(type);
    }

}