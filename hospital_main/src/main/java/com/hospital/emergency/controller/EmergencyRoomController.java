package com.hospital.emergency.controller;

import com.hospital.emergency.service.EmergencyRoomService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emergency")
public class EmergencyRoomController {

    private final EmergencyRoomService emergencyRoomService;

    public EmergencyRoomController(EmergencyRoomService emergencyRoomService) {
        this.emergencyRoomService = emergencyRoomService;
    }

    /**
     * 위도/경도를 기반으로 인근 응급실 XML 데이터를 반환합니다.
     * 
     * @param lat 위도 (latitude)
     * @param lon 경도 (longitude)
     * @return XML 형식의 응급실 정보
     */
    @GetMapping(value = "/nearby")
    public ResponseEntity<String> getNearbyEmergencyRooms(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon
    ) {
        String xmlData = emergencyRoomService.getNearbyEmergencyRoomsXml(lat, lon);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/xml; charset=UTF-8"));

        return ResponseEntity
                .ok()
                .headers(headers) // 실제 적용!
                .body(xmlData);   // 변수명도 맞춰줌
    }
    @GetMapping(value = "/nearby/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getNearbyEmergencyRoomsJson(
            @RequestParam("lat") double lat,
            @RequestParam("lon") double lon
    ) {
        try {
            var responseDto = emergencyRoomService.getNearbyEmergencyRooms(lat, lon);
            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            // 에러 발생 시 간단한 메시지와 함께 500 반환
            return ResponseEntity.status(500).body("API 호출 중 오류 발생: " + e.getMessage());
        }
    }

}
