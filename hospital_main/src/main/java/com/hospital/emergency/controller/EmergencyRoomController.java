package com.hospital.emergency.controller;

import com.hospital.emergency.service.EmergencyRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emergency-rooms")
public class EmergencyRoomController {

    private static final Logger log = LoggerFactory.getLogger(EmergencyRoomController.class);
    
    private final EmergencyRoomService emergencyRoomService;

    public EmergencyRoomController(EmergencyRoomService emergencyRoomService) {
        this.emergencyRoomService = emergencyRoomService;
    }

    @GetMapping(value = "/nearby", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> getNearbyEmergencyRoomsXml(
            @RequestParam double latitude,
            @RequestParam double longitude) {

        log.info("=== 응급실 조회 요청 ===");
        log.info("좌표: 위도={}, 경도={}", latitude, longitude);

        try {
            // 서비스에서 XML 문자열을 직접 반환하도록 변경되었으므로, String 타입을 그대로 받습니다.
            String xmlResponse = emergencyRoomService.getNearbyEmergencyRoomsXml(latitude, longitude);

            log.info("서비스 응답 길이: {}", xmlResponse != null ? xmlResponse.length() : "null");

            // 서비스에서 null 또는 비어있는 응답을 반환할 경우
            if (xmlResponse == null || xmlResponse.trim().isEmpty()) {
                log.error("서비스에서 빈 응답 또는 null 반환. 응급실 정보를 가져올 수 없음.");
                return ResponseEntity.internalServerError()
                        .contentType(MediaType.APPLICATION_XML)
                        .body("<error>응급실 정보를 가져올 수 없습니다. API 응답이 비어있습니다.</error>");
            }

            // 서비스에서 명시적으로 오류 XML을 반환한 경우 (예: Service Key 인증 실패)
            // 서비스 계층에서 반환하는 특정 오류 메시지를 확인합니다.
            if (xmlResponse.contains("<e>API 인증 실패 - Service Key를 확인하세요</e>") ||
                xmlResponse.contains("<e>URL 구성 오류</e>") ||
                xmlResponse.contains("<e>API 통신 중 알 수 없는 오류 발생</e>")) {
                
                log.warn("서비스에서 오류 응답 감지: {}", xmlResponse);
                // API 인증 실패는 클라이언트 오류로 간주할 수 있지만, 여기서는 API 내부 오류로 반환합니다.
                // 상황에 따라 ResponseEntity.badRequest() 또는 다른 HTTP 상태 코드를 사용할 수 있습니다.
                return ResponseEntity.internalServerError() // 500 Internal Server Error
                        .contentType(MediaType.APPLICATION_XML)
                        .body("<error>외부 응급실 API 호출 중 문제가 발생했습니다: " + xmlResponse + "</error>");
            }

            // 모든 검증을 통과한 경우, 정상 응답으로 간주하여 반환합니다.
            log.info("정상 응답 반환");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(xmlResponse);

        } catch (Exception e) {
            // 예기치 않은 컨트롤러 내부 오류 발생 시
            log.error("응급실 정보 조회 중 컨트롤러 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_XML)
                    .body("<error>서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.</error>");
        }
    }
}