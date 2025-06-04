package com.hospital.emergency.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

@Component
public class EmergencyRoomApiCaller {

    private static final Logger log = LoggerFactory.getLogger(EmergencyRoomApiCaller.class);

    @Value("${hospital.emergency.api.baseUrl}")
    private String baseUrl;

    @Value("${hospital.emergency.api.serviceKey}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    public EmergencyRoomApiCaller(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String callEmergencyRoomApiRaw(double latitude, double longitude) {
        try {
            // URI 구성 - 이중 인코딩 문제 해결을 위한 직접 구성
            URI uri = buildApiUriDirect(latitude, longitude);
            log.info("=== API 요청 시작 ===");
            log.info("요청 URL: {}", uri.toString());

            HttpHeaders headers = createHttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(headers);

            // API 호출
            ResponseEntity<byte[]> response = restTemplate.exchange(
                uri, HttpMethod.GET, entity, byte[].class);

            // 응답 로깅
            logResponseDetails(response);

            // 응답 처리
            return processResponse(response);

        } catch (Exception e) {
            log.error("API 호출 중 오류 발생", e);
            return "<e>API 호출 실패: " + e.getMessage() + "</e>";
        }
    }

    /**
     * 이중 인코딩 문제를 해결하기 위해 URI를 직접 구성하는 방식
     * RestTemplate이 추가 인코딩을 하지 않도록 완전히 인코딩된 URI 문자열을 직접 생성
     */
    private URI buildApiUriDirect(double latitude, double longitude) throws URISyntaxException, UnsupportedEncodingException {
        log.debug("Service Key 길이: {}, 앞 10자리: {}***", 
                  serviceKey.length(), 
                  serviceKey.length() > 10 ? serviceKey.substring(0, 10) : "짧음");
        
        // ServiceKey 인코딩 - + 문제 해결을 위한 수동 처리
        String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
        
        // 완전한 URL 문자열을 수동으로 구성
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("/getEgytLcinfoInqire");
        urlBuilder.append("?serviceKey=").append(encodedServiceKey);
        urlBuilder.append("&WGS84_LAT=").append(String.format("%.6f", latitude));
        urlBuilder.append("&WGS84_LON=").append(String.format("%.6f", longitude));
        urlBuilder.append("&pageNo=1");
        urlBuilder.append("&numOfRows=10");
        urlBuilder.append("&_type=xml");
        
        String completeUrl = urlBuilder.toString();
        log.info("수동 구성된 완전한 URL: {}", completeUrl);
        
        // URI 생성자를 직접 사용하여 RestTemplate의 추가 인코딩 방지
        URI uri = new URI(completeUrl);
        
        log.info("최종 URI: {}", uri.toString());
        return uri;
    }

    /**
     * 기존 UriComponentsBuilder 방식 (참고용으로 남겨둠)
     */
    @Deprecated
    private URI buildApiUriWithBuilder(double latitude, double longitude) throws UnsupportedEncodingException {
        String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
        
        // UriComponentsBuilder의 build(true) 사용으로 이미 인코딩된 값 보존 시도
        return org.springframework.web.util.UriComponentsBuilder
                .fromUriString(baseUrl + "/getEgytLcinfoInqire")
                .queryParam("serviceKey", encodedServiceKey)
                .queryParam("WGS84_LAT", String.format("%.6f", latitude))
                .queryParam("WGS84_LON", String.format("%.6f", longitude))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("_type", "xml")
                .build(true) // 이미 인코딩된 값 그대로 사용
                .toUri();
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Emergency-Service/1.0");
        headers.set("Accept", "application/xml, text/xml, */*");
        headers.set("Accept-Language", "ko-KR,ko;q=0.9");
        headers.set("Accept-Charset", "UTF-8");
        headers.set("Connection", "keep-alive");
        // GZIP 압축 비활성화로 디버깅 쉽게
        headers.set("Accept-Encoding", "identity");
        return headers;
    }

    private void logResponseDetails(ResponseEntity<byte[]> response) {
        log.info("=== API 응답 정보 ===");
        log.info("HTTP 상태: {}", response.getStatusCode());
        
        // Content-Length와 실제 받은 데이터 크기 비교
        String contentLength = response.getHeaders().getFirst("Content-Length");
        byte[] body = response.getBody();
        int actualLength = body != null ? body.length : 0;
        
        log.info("Content-Length 헤더: {}", contentLength);
        log.info("실제 응답 크기: {} bytes", actualLength);
        
        if (contentLength != null && !contentLength.equals(String.valueOf(actualLength))) {
            log.warn("⚠️ Content-Length({})와 실제 크기({})가 다릅니다!", contentLength, actualLength);
        }

        // 응답 헤더 출력
        response.getHeaders().forEach((key, values) -> {
            log.debug("  {}: {}", key, values);
        });

        // 바이트 데이터 상세 분석
        if (body != null && body.length > 0) {
            StringBuilder hex = new StringBuilder();
            StringBuilder ascii = new StringBuilder();
            
            int maxBytes = Math.min(body.length, 50);
            for (int i = 0; i < maxBytes; i++) {
                hex.append(String.format("%02X ", body[i]));
                // ASCII 문자 출력 (출력 가능한 문자만)
                char c = (char) body[i];
                ascii.append(c >= 32 && c <= 126 ? c : '.');
            }
            
            log.info("응답 바이트 (16진수): {}", hex.toString());
            log.info("응답 바이트 (ASCII): {}", ascii.toString());
        }
    }

    private String processResponse(ResponseEntity<byte[]> response) throws Exception {
        byte[] responseBody = response.getBody();
        
        if (responseBody == null) {
            log.error("응답 본문이 null입니다");
            return "<e>API 응답이 null입니다</e>";
        }

        if (responseBody.length == 0) {
            log.error("응답 본문이 비어있습니다");
            return "<e>API 응답이 비어있습니다</e>";
        }

        // 0x00 바이트나 매우 짧은 응답 처리
        if (responseBody.length <= 10) {
            log.warn("비정상적으로 짧은 응답: {} bytes", responseBody.length);
            
            // 모든 바이트가 0x00인지 확인
            boolean allZero = true;
            for (byte b : responseBody) {
                if (b != 0) {
                    allZero = false;
                    break;
                }
            }
            
            if (allZero) {
                log.error("응답이 모두 NULL 바이트입니다. Service Key 인증 실패 가능성 높음");
                return "<e>API 인증 실패 - Service Key를 확인하세요</e>";
            }
            
            String shortResponse = new String(responseBody, StandardCharsets.UTF_8);
            log.warn("짧은 응답 내용: '{}'", shortResponse);
            return "<e>예상보다 짧은 응답: " + shortResponse + "</e>";
        }

        // 정상적인 응답 처리
        boolean isGzipped = isGzipCompressed(responseBody);
        log.info("GZIP 압축 여부: {}", isGzipped);

        String xmlResponse;
        if (isGzipped) {
            xmlResponse = decompressGzipResponse(responseBody);
        } else {
            xmlResponse = new String(responseBody, StandardCharsets.UTF_8);
        }

        log.info("최종 XML 응답 길이: {} 문자", xmlResponse.length());
        log.info("XML 응답 시작 (500자): {}", 
            xmlResponse.length() > 500 ? xmlResponse.substring(0, 500) + "..." : xmlResponse);

        return xmlResponse;
    }

    private boolean isGzipCompressed(byte[] data) {
        return data.length >= 2 && data[0] == (byte) 0x1f && data[1] == (byte) 0x8b;
    }

    private String decompressGzipResponse(byte[] compressedData) throws Exception {
        try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(compressedData));
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            
            return baos.toString(StandardCharsets.UTF_8);
        }
    }
}