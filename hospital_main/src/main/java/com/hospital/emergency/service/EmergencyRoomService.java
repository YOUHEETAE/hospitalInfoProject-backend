package com.hospital.emergency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class EmergencyRoomService {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyRoomService.class);

    private final WebClient webClient;

    @Value("${hospital.emergency.api.serviceKey}")
    private String serviceKey;

    private final String baseUrl = "https://apis.data.go.kr/B552657/ErmctInfoInqireService/getEgytLcinfoInqire";

    public EmergencyRoomService(WebClient webClient) {
        this.webClient = webClient;
    }

    public String getNearbyEmergencyRoomsXml(double latitude, double longitude) {
        logger.info("=== EmergencyRoomService 시작 ===");
        logger.info("위도: {}, 경도: {}로 응급실 XML 조회 시작", latitude, longitude);

        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
            String WGS84_LAT = String.format("%.6f", latitude);
            String WGS84_LON = String.format("%.6f", longitude);

            String finalUrl = baseUrl +
                    "?serviceKey=" + encodedServiceKey +
                    "&WGS84_LAT=" + WGS84_LAT +
                    "&WGS84_LON=" + WGS84_LON +
                    "&pageNo=1" +
                    "&numOfRows=10" +
                    "&_type=xml";

            URI uri = new URI(finalUrl);
            logger.info("최종 URI: {}", uri);

            // WebClient로 요청 전송
            byte[] responseBytes = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .onErrorResume(e -> {
                        logger.error("WebClient 오류 발생: {}", e.getMessage(), e);
                        return Mono.empty();
                    })
                    .block();

            if (responseBytes == null) {
                logger.warn("⚠️ WebClient 응답이 NULL입니다.");
                return "<e>WebClient 응답 없음</e>";
            }

            logger.info("API로부터 받은 실제 응답 크기: {} bytes", responseBytes.length);

            if (responseBytes.length == 0) {
                logger.warn("⚠️ 응답이 비어 있음");
                return "<e>응답 없음</e>";
            } else if (responseBytes.length == 2 && responseBytes[0] == 0x00 && responseBytes[1] == 0x00) {
                logger.warn("⚠️ 2바이트 NULL 응답 감지됨");
                logger.info("응답 바이트 (HEX): {}", bytesToHex(responseBytes));
                return "<e>API 인증 실패 - Service Key 확인 필요</e>";
            }

            String xmlResponse = new String(responseBytes, StandardCharsets.UTF_8);
            logger.info("API 응답 일부: {}", xmlResponse.length() > 500 ? xmlResponse.substring(0, 500) + "..." : xmlResponse);

            if (xmlResponse.contains("<e>API 인증 실패 - Service Key를 확인하세요</e>")) {
                logger.warn("API 인증 실패 메시지 포함됨");
            }

            return xmlResponse;

        } catch (Exception e) {
            logger.error("API 호출 중 오류 발생", e);
            return "<e>WebClient 통신 오류 발생</e>";
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
