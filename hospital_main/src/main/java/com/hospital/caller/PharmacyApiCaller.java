package com.hospital.caller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.hospital.dto.PharmacyApiResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class PharmacyApiCaller {

    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    @Value("${hospital.pharmacy.api.base-url}")
    private String baseUrl;

    @Value("${hospital.pharmacy.api.key}")
    private String serviceKey;

    public PharmacyApiCaller(RestTemplate restTemplate, XmlMapper xmlMapper) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
    }
    
    /**
     * 약국 API 호출 (페이지 단위)
     * @param pageNo 페이지 번호
     * @param numOfRows 한 페이지당 데이터 수
     * @return PharmacyApiResponse
     */
    public PharmacyApiResponse callPharmacyApiByPage(int pageNo, int numOfRows) {
        try {
            String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());

            URI uri = UriComponentsBuilder.fromUriString(baseUrl)
                    .queryParam("serviceKey", encodedServiceKey)
                    .queryParam("pageNo", pageNo)
                    .queryParam("numOfRows", numOfRows)
                    .build(true)
                    .toUri();

            log.debug("약국 API 호출 URI: {}", uri);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            headers.add("Accept", "application/xml, text/xml");
            headers.add("Accept-Charset", "UTF-8");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET, entity, byte[].class);
            byte[] responseBytes = response.getBody();

            if (responseBytes == null || responseBytes.length == 0) {
                log.warn("약국 API 응답이 비어있음");
                throw new RuntimeException("약국 API 응답이 비어있습니다");
            }

            String responseBody = new String(responseBytes, StandardCharsets.UTF_8);
            log.debug("약국 API 응답 길이: {} bytes", responseBody.length());

            // XML 응답을 PharmacyApiResponse DTO로 매핑
            PharmacyApiResponse mappedResponse = xmlMapper.readValue(responseBody, PharmacyApiResponse.class);

            // resultCode 검사
            if (mappedResponse.getHeader() == null) {
                log.error("약국 API 응답 헤더가 없음");
                throw new RuntimeException("약국 API 응답 헤더가 없습니다");
            }

            String resultCode = mappedResponse.getHeader().getResultCode();
            String resultMsg = mappedResponse.getHeader().getResultMsg();

            if (!"00".equals(resultCode)) {
                log.error("약국 API 응답 오류 - 코드: {}, 메시지: {}", resultCode, resultMsg);
                throw new RuntimeException("약국 API 응답 오류: " + resultCode + " - " + resultMsg);
            }

            // 성공 로그
            if (mappedResponse.getBody() != null && mappedResponse.getBody().getItems() != null) {
                log.debug("약국 데이터 {} 건 수신 완료", mappedResponse.getBody().getItems().size());
            }

            return mappedResponse;

        } catch (HttpClientErrorException e) {
            // 4xx 클라이언트 오류
            log.error("약국 API 클라이언트 오류 - 상태코드: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("약국 API 클라이언트 오류(page: " + pageNo + "): " + e.getMessage(), e);

        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            log.error("약국 API 서버 오류 - 상태코드: {}, 응답: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("약국 API 서버 오류(page: " + pageNo + "): " + e.getMessage(), e);

        } catch (Exception e) {
            // 기타 예외 (XML 파싱 오류 포함)
            log.error("약국 API 호출 중 예외 발생(page: {}): {}", pageNo, e.getMessage(), e);
            throw new RuntimeException("약국 API 호출 중 오류 발생(page: " + pageNo + "): " + e.getMessage(), e);
        }
    }
}