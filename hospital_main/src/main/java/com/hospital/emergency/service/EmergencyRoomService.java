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
import java.nio.charset.Charset; // Charset 임포트 추가

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
    
    // 이 메서드가 외부에서 호출되는 주된 메서드입니다.
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
            
            // WebClient로 String 직접 받기 (기본 인코딩 처리 시도)
            String xmlResponse = null;
            try {
                xmlResponse = webClient.get()
                        .uri(uri)
                        // Accept-Charset 헤더는 사실상 WebClient의 bodyToMono(String.class)에 큰 영향이 없습니다.
                        // 이 메서드는 Content-Type 헤더의 charset을 우선시합니다.
                        // .header("Accept", "application/xml; charset=UTF-8") // 이 헤더는 서버에 "나는 UTF-8로 받을 준비가 되어있다"고 알리는 역할입니다.
                        // .header("Accept-Charset", "UTF-8") // 이 헤더도 마찬가지입니다.
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();
            } catch (Exception e) {
                 logger.error("WebClient String 요청 중 오류 발생 (1차 시도): {}", e.getMessage(), e);
                 // 1차 시도 실패 시 바로 바이트 배열 재시도 로직으로 넘어감
                 xmlResponse = null; // 오류 발생 시 null로 설정하여 재시도 유도
            }


            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                logger.warn("⚠️ WebClient 응답이 NULL이거나 비어있습니다. 바이트 배열 방식으로 재시도합니다.");
                return getNearbyEmergencyRoomsXmlWithBytes(latitude, longitude); // 바이트 배열 방식으로 재시도
            }
            
            logger.info("API 응답 길이 (1차 String): {} 문자", xmlResponse.length());
            logger.info("API 응답 일부 (1차 String): {}", xmlResponse.length() > 500 ? 
                xmlResponse.substring(0, 500) + "..." : xmlResponse);
            
            // 인코딩 문제 감지: 깨진 문자가 포함되어 있다면 바이트 배열 방식으로 재시도
            if (xmlResponse.contains("???") || xmlResponse.contains("¿") ||
                xmlResponse.contains("����") || // 특정 깨진 한글 패턴 (UTF-8이 아닌 것을 UTF-8로 읽었을 때)
                xmlResponse.contains("占쏙옙") || // 이클립스 등에서 UTF-8이 아닌 것을 UTF-8로 읽었을 때
                xmlResponse.matches(".*[\\p{InHangul_Jamo}\\p{InHangul_Compatibility_Jamo}\\p{InHangul_Syllables}].*") // 한글 포함 여부 확인 (긍정적인 판단)
                ) {
                
                logger.warn("⚠️ 1차 WebClient 응답에서 한글 깨짐 또는 이상 문자 감지됨. 바이트 배열 방식으로 재시도합니다.");
                return getNearbyEmergencyRoomsXmlWithBytes(latitude, longitude); // 바이트 배열 방식으로 재시도
            }
            
            // 인증 실패 메시지 체크 (원활한 흐름을 위해 맨 마지막에 배치)
            if (xmlResponse.contains("<resultCode>99</resultCode>") || 
                xmlResponse.contains("SERVICE_KEY_IS_NOT_REGISTERED_ERROR")) {
                logger.warn("API 인증 실패 메시지 감지됨.");
                return "<error>API 인증 실패 - Service Key 확인 필요</error>";
            }

            logger.info("✅ 1차 WebClient 응답 성공. 한글 깨짐 없음.");
            return xmlResponse; // 문제가 없다면 1차 응답 반환
            
        } catch (Exception e) {
            logger.error("API 호출 중 예외 발생 (최초 시도): {}", e.getMessage(), e);
            // 최초 시도에서 예외 발생 시 바이트 배열 방식으로 재시도
            return getNearbyEmergencyRoomsXmlWithBytes(latitude, longitude);
        }
    }
    
    // 이 메서드는 이제 getNearbyEmergencyRoomsXml 내부에서만 호출됩니다.
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
                        return Mono.empty(); // 오류 발생 시 빈 Mono 반환
                    })
                    .block();
            
            if (responseBytes == null || responseBytes.length == 0) {
                logger.warn("⚠️ 바이트 배열 응답이 NULL이거나 비어있습니다.");
                return "<error>바이트 배열 응답 없음</error>";
            }
            
            logger.info("API로부터 받은 원본 바이트 배열 크기: {} bytes", responseBytes.length);
            logger.info("API로부터 받은 원본 바이트 (HEX) 일부: {}", 
                bytesToHex(java.util.Arrays.copyOfRange(responseBytes, 0, Math.min(responseBytes.length, 100)))); // 앞 100바이트만 출력

            // 여러 인코딩 시도
            String[] encodings = {"UTF-8", "EUC-KR", "MS949", "ISO-8859-1"}; // ISO-8859-1은 거의 없을 듯 하지만, 포함해 둠
            String bestEffortXml = null;
            
            for (String encoding : encodings) {
                try {
                    String currentAttempt = new String(responseBytes, encoding);
                    logger.info("🧪 {} 인코딩 시도 결과 일부: {}", encoding, 
                        currentAttempt.length() > 200 ? currentAttempt.substring(0, 200) : currentAttempt);
                    
                    // 한글이 제대로 표시되는지 확인 (가장 확실한 방법은 눈으로 확인)
                    // 여기서는 '?'가 없고, 실제 한글 내용이 포함되어 있다면 성공으로 간주
                    if (!currentAttempt.contains("???") && !currentAttempt.contains("¿") && 
                        !currentAttempt.contains("����") && !currentAttempt.contains("占쏙옙") &&
                        (currentAttempt.contains("병원") || currentAttempt.contains("의원") || 
                         currentAttempt.contains("<dutyName>") || currentAttempt.contains("NORMAL SERVICE"))) {
                        logger.info("✅ {} 인코딩으로 한글 깨짐 없이 성공!", encoding);
                        return currentAttempt; // 성공적인 인코딩 반환
                    }
                    // 혹시 모를 대비책: 첫 번째로 오류 없이 변환된 문자열을 저장
                    if (bestEffortXml == null) {
                         bestEffortXml = currentAttempt;
                    }
                } catch (Exception e) {
                    logger.warn("❌ {} 인코딩 시도 중 오류 발생: {}", encoding, e.getMessage());
                }
            }
            
            logger.error("⚠️ 모든 인코딩 시도 실패. 원본 바이트를 UTF-8로 강제 디코딩하여 반환합니다.");
            // 모든 시도 실패 시, 첫 번째 오류 없는 변환 문자열 (bestEffortXml)이 있다면 반환
            // 없다면 UTF-8로 강제 반환 (최후의 수단)
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
}