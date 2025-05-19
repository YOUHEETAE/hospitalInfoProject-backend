package com.hospital.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class HospitalApiServiceTest {

    @Test
    public void testCallHospitalApi() {
        RestTemplate restTemplate = new RestTemplate();

        // 👉 여기에 디코딩된 실제 API키 넣기 (주의: 노출 금지)
        String apiKey = "";
        String url = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList"
                   + "?ServiceKey=" + apiKey
                   + "&pageNo=1"
                   + "&numOfRows=30";
                

        String response = restTemplate.getForObject(url, String.class);

        // 확인
        assertNotNull(response);
        System.out.println("응답 결과:\n" + response);
    }
}
