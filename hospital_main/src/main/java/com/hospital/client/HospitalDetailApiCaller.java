package com.hospital.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.hospital.service.HospitalCodeFetcher;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class HospitalDetailApiCaller {

    private final RestTemplate restTemplate;
    private final HospitalCodeFetcher hospitalCodeFetcher; 

    private static final String BASE_URL = "https://apis.data.go.kr/B551182/MadmDtlInfoService2.7/getDtlInfo2.7";
    private static final String SERVICE_KEY = "6IeDxLyk3cFOR8Fpgdyar0bwLjz07UptNyvbUC3KT3SRjcKdjyHG8Rt+DJ90JVPGwgH+GalAJveVPKnlYSKIfg==";

    public HospitalDetailApiCaller(RestTemplate restTemplate, HospitalCodeFetcher hospitalCodeFetcher) {
        this.restTemplate = restTemplate;
        this.hospitalCodeFetcher = hospitalCodeFetcher;
    }

    public String fetchHospitalDetailByCode(String hospitalCode, int pageNo, int numOfRows) {
        try {
            String encodedServiceKey = URLEncoder.encode(SERVICE_KEY, StandardCharsets.UTF_8.toString());

            var uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("serviceKey", encodedServiceKey)
                    .queryParam("ykiho", hospitalCode)
                    .queryParam("pageNo", pageNo)
                    .queryParam("numOfRows", numOfRows)
                    .queryParam("_type", "json")
                    .build(true)
                    .toUri();

            return restTemplate.getForObject(uri, String.class);

        } catch (Exception e) {
            System.err.println("HospitalDetail API 호출 중 오류 발생 (code: " + hospitalCode + "): " + e.getMessage());
            return null;
        }
    }
    // 여러 병원 코드에 대해 API 호출을 수행하는 메서드 추가
    public List<String> fetchAllHospitalDetails(int pageNo, int numOfRows) {
        List<String> hospitalCodes = hospitalCodeFetcher.getAllHospitalCodes();
        List<String> responses = new ArrayList<>();

        for (String code : hospitalCodes) {
            String response = fetchHospitalDetailByCode(code, pageNo, numOfRows);
            if (response != null && !response.isEmpty()) {
                responses.add(response);
            } else {
                System.out.println("병원 코드 " + code + " 에 대한 데이터가 없거나 호출 실패, 넘어갑니다.");
            }
        }
        return responses;
    }
}
