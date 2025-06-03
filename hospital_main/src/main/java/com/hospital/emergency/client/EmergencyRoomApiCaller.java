package com.hospital.emergency.client;

import com.hospital.emergency.dto.EmergencyRoomApiResponse; // 새로 만든 DTO 임포트
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component // 스프링 빈으로 등록하여 다른 곳에서 주입받아 사용할 수 있도록 합니다.
public class EmergencyRoomApiCaller {

    // application.properties에서 API 기본 URL을 주입받습니다.
    @Value("${hospital.emergency.api.baseUrl}")
    private String baseUrl;

    // application.properties에서 서비스 키를 주입받습니다.
    @Value("${hospital.emergency.api.serviceKey}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    // RestTemplate은 외부 API 호출을 위한 스프링의 핵심 도구입니다.
    // RestTemplateConfig에서 빈으로 등록한 RestTemplate을 주입받습니다.
    public EmergencyRoomApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 사용자의 위도, 경도에 기반하여 주변 응급실 정보를 공공데이터 포털 API로부터 조회합니다.
     * @param latitude 사용자 위치의 위도 (WGS84_LAT)
     * @param longitude 사용자 위치의 경도 (WGS84_LON)
     * @return API 응답을 매핑한 EmergencyRoomApiResponse 객체 (호출 실패 시 null 반환)
     */
    public EmergencyRoomApiResponse callEmergencyRoomApi(double latitude, double longitude) {
        try {
            // 서비스 키를 URL 인코딩합니다. '+' 문자가 공백으로 인코딩되는 문제를 방지합니다.
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString())
                                                .replace("+", "%2B");

            // API 호출 URL을 동적으로 구성합니다.
            // API 문서에 따르면 'getEmgMedInfo'가 실제 오퍼레이션 경로입니다.
            URI uri = UriComponentsBuilder.fromUriString(baseUrl + "/getEmgMedInfo")
                    .queryParam("serviceKey", encodedServiceKey) // 인증키
                    .queryParam("WGS84_LAT", latitude)           // 필수 파라미터: 위도
                    .queryParam("WGS84_LON", longitude)          // 필수 파라미터: 경도
                    .queryParam("pageNo", 1)                     // 페이지 번호 (기본 1)
                    .queryParam("numOfRows", 100)                // 한 페이지당 목록 수 (넉넉하게 100개 요청)
                    .queryParam("_type", "json")                // JSON 형식으로 응답 요청 (이전 로그에 JSON 응답이 확인되었으므로)
                    .build(true) // 인코딩된 URI 생성 (true: 이미 인코딩된 쿼리 파라미터는 다시 인코딩하지 않음)
                    .toUri();

            System.out.println("Emergency Room API Request URL: " + uri.toString());

            // RestTemplate을 사용하여 GET 요청을 보내고, JSON 응답을 EmergencyRoomApiResponse DTO로 직접 매핑합니다.
            // RestTemplate은 내부적으로 Jackson 라이브러리를 사용하여 JSON -> DTO 변환을 자동으로 처리합니다.
            ResponseEntity<EmergencyRoomApiResponse> responseEntity = restTemplate.getForEntity(uri, EmergencyRoomApiResponse.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) { // HTTP 상태 코드가 200번대 (성공)인지 확인
                EmergencyRoomApiResponse apiResponse = responseEntity.getBody();
                if (apiResponse != null) {
                    System.out.println("API Call Successful. HTTP Status: " + responseEntity.getStatusCode());
                    System.out.println("API Result Code: " + apiResponse.getResponse().getHeader().getResultCode());
                    System.out.println("Total Count: " + apiResponse.getResponse().getBody().getTotalCount());
                    // rawResponse 로그는 DTO 변환 후에는 필요 없지만, 디버깅을 위해 잠시 추가할 수 있습니다.
                    // System.out.println("Raw JSON Response (after conversion to DTO): " + responseEntity.getBody());
                }
                return apiResponse;
            } else {
                System.err.println("API Call Failed. HTTP Status: " + responseEntity.getStatusCode());
                System.err.println("Response Body: " + responseEntity.getBody()); // 에러 응답 내용 로깅
                // HTTP 상태 코드가 성공이 아닐 경우 (예: 400, 500 에러)
            }
        } catch (Exception e) {
            // API 호출 중 발생할 수 있는 네트워크 오류, 파싱 오류 등을 catch합니다.
            System.err.println("Error calling Emergency Room API: " + e.getMessage());
            e.printStackTrace(); // 상세 스택 트레이스 출력
        }
        return null; // 호출 실패 시 null 반환 (서비스 계층에서 적절히 처리 필요)
    }
}