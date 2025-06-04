package com.hospital.emergency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections; // 필요에 따라 추가

@Service
public class EmergencyRoomService {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyRoomService.class);
    private final RestTemplate restTemplate;

    // application.properties 또는 application.yml에서 service.key 값을 주입받습니다.
    @Value("${hospital.emergency.api.serviceKey}")
    private String serviceKey;

    private final String baseUrl = "https://apis.data.go.kr/B552657/ErmctInfoInqireService/getEgytLcinfoInqire";

    // RestTemplate은 생성자 주입으로 변경 (권장)
    public EmergencyRoomService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 지정된 위도와 경도 주변의 응급실 정보를 XML 형태로 조회합니다.
     * Service Key 이중 인코딩 문제를 해결하기 위해 java.net.URI 객체를 직접 생성하여 사용합니다.
     *
     * @param latitude 조회할 중심 위도
     * @param longitude 조회할 중심 경도
     * @return API로부터 받은 응급실 정보 XML 문자열. 오류 발생 시 null 또는 에러 메시지 포함 XML 반환.
     */
    public String getNearbyEmergencyRoomsXml(double latitude, double longitude) {
        logger.info("=== EmergencyRoomService 시작 ===");
        logger.info("위도: {}, 경도: {}로 응급실 XML 조회 시작", latitude, longitude);

        String xmlResponse = null;
        try {
            // 1. Service Key URL 인코딩 처리
            // '+' 문자가 '%2B'로 올바르게 인코딩되도록 URLEncoder.encode() 사용
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());

            // 2. 위도, 경도를 소수점 6자리까지 포맷팅 (API 요구사항에 따라)
            String WGS84_LAT = String.format("%.6f", latitude);
            String WGS84_LON = String.format("%.6f", longitude);

            // 3. 최종 API 요청 URL 문자열 직접 구성
            // 이렇게 구성하면 RestTemplate의 자동 인코딩에 영향을 받지 않습니다.
            String finalUrlString = baseUrl
                    + "?serviceKey=" + encodedServiceKey
                    + "&WGS84_LAT=" + WGS84_LAT
                    + "&WGS84_LON=" + WGS84_LON
                    + "&pageNo=1"
                    + "&numOfRows=10"
                    + "&_type=xml"; // XML 응답 타입 명시

            logger.info("생성된 API 요청 URL: {}", finalUrlString);

            // 4. 구성된 문자열을 java.net.URI 객체로 변환
            // URI 생성자는 이미 인코딩된 문자열을 그대로 사용합니다.
            URI uri = new URI(finalUrlString);
            logger.info("RestTemplate에 전달될 최종 URI 객체: {}", uri);

            logger.info("=== 외부 API 요청 시작 ===");

            // 5. RestTemplate을 사용하여 API 호출 및 응답 받기
            // 응답을 byte[] 형태로 받아 원시 데이터 길이를 확인하고 NULL 바이트 여부 등을 검사합니다.
            byte[] responseBytes = restTemplate.getForObject(uri, byte[].class);

            logger.info("=== 외부 API 응답 수신 ===");

            if (responseBytes != null) {
                logger.info("API로부터 받은 실제 응답 크기: {} bytes", responseBytes.length);

                // API 응답 데이터 검증
                if (responseBytes.length == 0) {
                    logger.warn("⚠️ API 응답이 비어있습니다. 예상치 못한 응답일 수 있습니다.");
                    // 빈 응답이더라도 서비스 키 인증 실패 케이스일 수 있으므로 추가 로그 검토 필요
                } else if (responseBytes.length == 2 && responseBytes[0] == 0x00 && responseBytes[1] == 0x00) {
                    // 이전 로그에서 확인된 2바이트 NULL 응답 케이스
                    logger.warn("⚠️ 비정상적으로 짧은 응답 (2 bytes) 및 NULL 바이트 감지됨.");
                    logger.info("응답 바이트 (16진수): {}", bytesToHex(responseBytes));
                    logger.info("응답 바이트 (ASCII): {}", new String(responseBytes, StandardCharsets.US_ASCII));
                    logger.error("응답이 모두 NULL 바이트입니다. Service Key 인증 실패 가능성 높음.");
                    // API 인증 실패 메시지를 반환하여 상위 계층에서 처리할 수 있도록 합니다.
                    return "<e>API 인증 실패 - Service Key를 확인하세요</e>";
                } else {
                    // 정상적인 XML 응답으로 간주하고 UTF-8로 디코딩
                    xmlResponse = new String(responseBytes, StandardCharsets.UTF_8);
                    logger.info("API 응답 XML (일부): {}", xmlResponse.length() > 500 ? xmlResponse.substring(0, 500) + "..." : xmlResponse);

                    // API 응답 내용에 따라 서비스 키 오류가 포함되어 있는지 한 번 더 확인
                    if (xmlResponse.contains("<e>API 인증 실패 - Service Key를 확인하세요</e>")) {
                        logger.warn("Service: API 응답 내용에서 서비스 키 인증 실패 메시지가 감지되었습니다.");
                    }
                }
            } else {
                logger.warn("⚠️ API 응답이 NULL입니다.");
                // RestTemplate.getForObject()가 null을 반환하는 경우는 매우 드뭅니다.
                // 주로 네트워크 오류나 서버 연결 문제 시 예외를 던집니다.
            }

        } catch (URISyntaxException e) {
            logger.error("잘못된 URI 문법으로 API 호출 실패: {}", e.getMessage(), e);
            // URI 구성 중 문법 오류가 발생하면 이 예외가 발생합니다.
            return "<e>URL 구성 오류</e>"; // 예외 발생 시 반환할 오류 메시지
        } catch (Exception e) {
            logger.error("외부 응급실 API 호출 중 예상치 못한 오류 발생", e);
            // 네트워크 연결 문제, API 서버 오류 등 기타 모든 예외를 여기서 처리합니다.
            return "<e>API 통신 중 알 수 없는 오류 발생</e>"; // 예외 발생 시 반환할 오류 메시지
        }

        logger.info("=== EmergencyRoomService 종료 ===");
        return xmlResponse;
    }

    // 바이트 배열을 16진수 문자열로 변환하는 유틸리티 메서드
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}