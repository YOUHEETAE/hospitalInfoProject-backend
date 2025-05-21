package com.hospital.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// SimpleClientHttpRequestFactory 임포트 추가
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Service
public class HospitalApiService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalApiService.class);

    // RestTemplate 인스턴스: 필드 레벨에서 한 번만 생성하여 재사용
    // 여기에서 타임아웃을 설정합니다.
    private final RestTemplate restTemplate; // final로 선언은 유지

    // 생성자에서 RestTemplate을 초기화하며 타임아웃 설정
    public HospitalApiService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 연결 타임아웃 10초 (10000 밀리초)
        factory.setReadTimeout(30000);    // 읽기 타임아웃 30초 (30000 밀리초)
        this.restTemplate = new RestTemplate(factory); // 설정된 factory를 사용하여 RestTemplate 생성
    }

    private final String baseUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList";
    private final List<String> sigunguCodes = Arrays.asList("310401", "310402", "310403");
    private final String serviceKey = "6IeDxLyk3cFOR8Fpgdyar0bwLjz07UptNyvbUC3KT3SRjcKdjyHG8Rt+DJ90JVPGwgH+GalAJveVPKnlYSKIfg==";

    /**
     * 지정된 시군구 코드에 해당하는 병원 기본 정보를 API에서 가져옵니다.
     * 각 시군구 코드별로 API를 호출하며, 오류 발생 시 로깅합니다.
     * @return 각 API 호출에서 반환된 응답 문자열 리스트
     */
    public List<String> fetchAllHospitals() {
        int pageNo = 1;
        int numOfRows = 20;

        List<String> allResults = new ArrayList<>();

        for (String sigunguCode : sigunguCodes) {
            try {
                String encodedSigunguCode = URLEncoder.encode(sigunguCode, StandardCharsets.UTF_8.toString());
                String encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());

                String finalUrlString = baseUrl
                        + "?serviceKey=" + encodedServiceKey
                        + "&pageNo=" + pageNo
                        + "&numOfRows=" + numOfRows
                        + "&sgguCd=" + encodedSigunguCode;

                logger.info("Request URL (String): {}", finalUrlString);

                URI uri = new URI(finalUrlString);

                String response = restTemplate.getForObject(uri, String.class);

                if (response != null && !response.isEmpty()) {
                    allResults.add(response);
                    logger.debug("API 응답 수신 (sigunguCode: {}): {}", sigunguCode, response.substring(0, Math.min(response.length(), 200)));
                } else {
                    logger.warn("API 응답이 비어있거나 null입니다. (sigunguCode: {})", sigunguCode);
                }

            } catch (URISyntaxException e) {
                logger.error("잘못된 URI 문법으로 API 호출 실패 (sigunguCode: {}): {}", sigunguCode, e.getMessage(), e);
            } catch (HttpClientErrorException e) {
                logger.error("API 호출 중 클라이언트 오류 발생 (sigunguCode: {}, HTTP Status: {}): {}",
                             sigunguCode, e.getStatusCode(), e.getResponseBodyAsString(), e);
            } catch (HttpServerErrorException e) {
                logger.error("API 호출 중 서버 오류 발생 (sigunguCode: {}, HTTP Status: {}): {}",
                             sigunguCode, e.getStatusCode(), e.getResponseBodyAsString(), e);
            } catch (ResourceAccessException e) {
                logger.error("API 호출 중 네트워크/I/O 오류 발생 (sigunguCode: {}): {}",
                             sigunguCode, e.getMessage(), e);
            } catch (RestClientException e) {
                logger.error("API 호출 중 RestClientException 발생 (sigunguCode: {}): {}",
                             sigunguCode, e.getMessage(), e);
            } catch (Exception e) {
                logger.error("API 호출 중 예상치 못한 오류 발생 (sigunguCode: {}): {}",
                             sigunguCode, e.getMessage(), e);
            }
        }

        return allResults;
    }
}