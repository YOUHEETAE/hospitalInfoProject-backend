package com.hospital.emergency.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.hospital.emergency.dto.EmergencyRoomResponse;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import reactor.core.publisher.Mono;

import java.io.StringReader;
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
    
    // 외부 호출 메서드
    public String getNearbyEmergencyRoomsXml(double latitude, double longitude) {
        logger.info("=== EmergencyRoomService 시작 ===");
        logger.info("위도: {}, 경도: {}로 응급실 XML 조회 시작", latitude, longitude);

        try {
            String WGS84_LAT = String.format("%.6f", latitude);
            String WGS84_LON = String.format("%.6f", longitude);

            URI uri = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .queryParam("serviceKey", serviceKey) // 인코딩 자동 처리됨
                    .queryParam("WGS84_LAT", WGS84_LAT)
                    .queryParam("WGS84_LON", WGS84_LON)
                    .queryParam("pageNo", 1)
                    .queryParam("numOfRows", 10)
                    .queryParam("_type", "xml")
                    .build(true)
                    .toUri();

            logger.info("최종 URI: {}", uri);

            String xmlResponse = null;
            try {
                xmlResponse = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (Exception e) {
                logger.error("WebClient String 요청 중 오류 발생 (1차 시도): {}", e.getMessage(), e);
                xmlResponse = null;
            }

            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                logger.warn("⚠️ WebClient 응답이 NULL이거나 비어있습니다. 바이트 배열 방식으로 재시도합니다.");
                return getNearbyEmergencyRoomsXmlWithBytes(latitude, longitude);
            }

            logger.info("API 응답 길이 (1차 String): {} 문자", xmlResponse.length());
            logger.info("API 응답 데이터 전체 (1차 String):\n{}", 
                xmlResponse.length() > 2000 ? xmlResponse.substring(0, 2000) + "...\n(이하 생략)" : xmlResponse);

            if (xmlResponse.contains("???") || xmlResponse.contains("¿") ||
                xmlResponse.contains("����") || xmlResponse.contains("占쏙옙")) {
                logger.warn("⚠️ 한글 깨짐 감지됨. 바이트 배열 방식으로 재시도합니다.");
                return getNearbyEmergencyRoomsXmlWithBytes(latitude, longitude);
            }

            if (xmlResponse.contains("<resultCode>99</resultCode>") || 
                xmlResponse.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
                logger.warn("API 인증 실패 메시지 감지됨.");
                return "<error>API 인증 실패 - Service Key 확인 필요</error>";
            }

            logger.info("✅ 1차 WebClient 응답 성공. 한글 깨짐 없음.");
            return xmlResponse;

        } catch (Exception e) {
            logger.error("API 호출 중 예외 발생 (최초 시도): {}", e.getMessage(), e);
            return getNearbyEmergencyRoomsXmlWithBytes(latitude, longitude);
        }
    }
    
    // 바이트 배열 방식 재시도 메서드 (내부 전용)
    private String getNearbyEmergencyRoomsXmlWithBytes(double latitude, double longitude) {
        logger.info("=== getNearbyEmergencyRoomsXmlWithBytes 시작 (바이트 배열 방식으로 재시도) ===");
        
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
            logger.info("재시도 최종 URI: {}", uri);
            
            byte[] responseBytes = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .onErrorResume(e -> {
                        logger.error("WebClient 바이트 배열 요청 중 오류 발생 (재시도): {}", e.getMessage(), e);
                        return Mono.empty();
                    })
                    .block();
            
            if (responseBytes == null || responseBytes.length == 0) {
                logger.warn("⚠️ 바이트 배열 응답이 NULL이거나 비어있습니다.");
                return "<error>바이트 배열 응답 없음</error>";
            }
            
            logger.info("API로부터 받은 원본 바이트 배열 크기: {} bytes", responseBytes.length);
            logger.info("API로부터 받은 원본 바이트 (HEX) 일부: {}", 
                bytesToHex(java.util.Arrays.copyOfRange(responseBytes, 0, Math.min(responseBytes.length, 100)))); 

            String[] encodings = {"UTF-8", "EUC-KR", "MS949", "ISO-8859-1"};
            String bestEffortXml = null;
            
            for (String encoding : encodings) {
                try {
                    String currentAttempt = new String(responseBytes, encoding);
                    logger.info("🧪 {} 인코딩 시도 결과 전체:\n{}", encoding, 
                        currentAttempt.length() > 2000 ? currentAttempt.substring(0, 2000) + "...\n(이하 생략)" : currentAttempt);
                    
                    if (!currentAttempt.contains("???") && !currentAttempt.contains("¿") && 
                        !currentAttempt.contains("����") && !currentAttempt.contains("占쏙옙") &&
                        (currentAttempt.contains("병원") || currentAttempt.contains("의원") || 
                         currentAttempt.contains("<dutyName>") || currentAttempt.contains("NORMAL SERVICE"))) {
                        logger.info("✅ {} 인코딩으로 한글 깨짐 없이 성공!", encoding);
                        return currentAttempt;
                    }
                    if (bestEffortXml == null) {
                         bestEffortXml = currentAttempt;
                    }
                } catch (Exception e) {
                    logger.warn("❌ {} 인코딩 시도 중 오류 발생: {}", encoding, e.getMessage());
                }
            }
            
            logger.error("⚠️ 모든 인코딩 시도 실패. 원본 바이트를 UTF-8로 강제 디코딩하여 반환합니다.");
            return bestEffortXml != null ? bestEffortXml : new String(responseBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            logger.error("바이트 배열 방식 API 호출 중 최종 오류 발생: {}", e.getMessage(), e);
            return "<error>최종 API 호출 오류 발생: " + e.getMessage() + "</error>";
        }
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
    
    public EmergencyRoomResponse parseXmlToDto(String xml) throws Exception {
        logger.info("=== XML to DTO 파싱 시작 ===");
        
        // XML 내용 검증
        if (xml == null || xml.trim().isEmpty()) {
            throw new IllegalArgumentException("XML 내용이 null이거나 비어있습니다.");
        }
        
        // XML 형식 검증
        if (!xml.trim().startsWith("<?xml") && !xml.trim().startsWith("<response")) {
            logger.warn("XML이 표준 형식이 아닙니다. 내용 확인: {}", 
                xml.length() > 200 ? xml.substring(0, 200) + "..." : xml);
        }
        
        // XML 전처리 - BOM 제거 및 공백 정리
        String cleanXml = xml.trim();
        if (cleanXml.startsWith("\uFEFF")) {
            cleanXml = cleanXml.substring(1);
            logger.info("BOM 문자 제거됨");
        }
        
        // XML 파싱 전 로깅
        logger.info("파싱할 XML 길이: {} 문자", cleanXml.length());
        logger.info("파싱할 XML 내용 (처음 500자):\n{}", 
            cleanXml.length() > 500 ? cleanXml.substring(0, 500) + "..." : cleanXml);
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(EmergencyRoomResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            
            // 검증 모드 비활성화 (선택사항)
            unmarshaller.setEventHandler(event -> {
                logger.warn("JAXB 검증 경고: {}", event.getMessage());
                return true; // 경고 무시하고 계속 진행
            });

            StringReader reader = new StringReader(cleanXml);
            EmergencyRoomResponse response = (EmergencyRoomResponse) unmarshaller.unmarshal(reader);
            
            logger.info("✅ XML 파싱 성공!");
            if (response != null && response.getHeader() != null) {
                logger.info("응답 코드: {}, 메시지: {}", 
                    response.getHeader().getResultCode(), 
                    response.getHeader().getResultMsg());
            }
            
            return response;
            
        } catch (jakarta.xml.bind.UnmarshalException e) {
            logger.error("❌ JAXB Unmarshal 오류 발생: {}", e.getMessage());
            logger.error("원인: {}", e.getCause() != null ? e.getCause().getMessage() : "알 수 없음");
            
            // 상세한 오류 정보 출력
            if (e.getLinkedException() != null) {
                logger.error("연결된 예외: {}", e.getLinkedException().getMessage());
            }
            
            throw new Exception("XML 파싱 실패: " + e.getMessage(), e);
            
        } catch (Exception e) {
            logger.error("❌ 예상치 못한 파싱 오류: {}", e.getMessage(), e);
            throw new Exception("XML 파싱 중 예상치 못한 오류: " + e.getMessage(), e);
        }
    }
    
    public EmergencyRoomResponse getNearbyEmergencyRooms(double latitude, double longitude) throws Exception {
        String xml = getNearbyEmergencyRoomsXml(latitude, longitude);
        
        // 에러 XML인지 확인
        if (xml.startsWith("<error>")) {
            throw new Exception("API 호출 실패: " + xml);
        }
        
        return parseXmlToDto(xml);
    }
}