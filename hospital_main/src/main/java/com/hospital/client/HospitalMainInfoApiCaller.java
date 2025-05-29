package com.hospital.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder; // URL 빌더 사용
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.net.URI;
import java.io.UnsupportedEncodingException; // URLEncoder.encode 사용 시 필요
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component // 이 클래스를 Spring 빈으로 등록합니다.
public class HospitalMainInfoApiCaller {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    @Value("${api.data.go.kr.base-url}")
    private String baseUrl;

    @Value("${api.data.go.kr.service-key}")
    private String serviceKey;

    public HospitalMainInfoApiCaller(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

  
    public JsonNode callApi(String apiPath, String queryParams) {
        String encodedServiceKey;
        try {
            encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("서비스 키 인코딩 실패: " + e.getMessage(), e);
        }

        URI uri = UriComponentsBuilder.fromUriString(baseUrl + apiPath)
                                     .query(queryParams)
                                     .queryParam("serviceKey", encodedServiceKey) // 서비스 키 추가
                                     .queryParam("_type", "json") // 응답 타입을 JSON으로 강제
                                     .build(true) // 인코딩된 쿼리 파라미터를 허용합니다.
                                     .toUri();

        String responseJson;
        try {
            responseJson = restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            throw new RuntimeException("API 호출 중 오류 발생: " + uri.toString() + ", " + e.getMessage(), e);
        }
        JsonNode rootNode = null;

        // API 응답의 resultCode 검사 (여기서 처리)
        try {
        	rootNode = objectMapper.readTree(responseJson);
            JsonNode headerNode = rootNode.path("response").path("header");
            String resultCode = headerNode.path("resultCode").asText();
            String resultMsg = headerNode.path("resultMsg").asText();

            if (!"00".equals(resultCode)) {
                throw new RuntimeException("API 응답 오류: " + resultCode + " - " + resultMsg + " (URL: " + uri.toString() + ")");
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("API 응답 JSON 파싱 오류: " + e.getMessage() + " (응답: " + responseJson + ")", e);
        }
        return rootNode;
    }
    
}