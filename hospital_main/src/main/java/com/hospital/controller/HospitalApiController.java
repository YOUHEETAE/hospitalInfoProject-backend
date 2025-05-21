package com.hospital.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.service.HospitalApiService;

import java.util.List;

@RestController
public class HospitalApiController {

    private final HospitalApiService hospitalApiService;

    public HospitalApiController(HospitalApiService hospitalApiService) {
        this.hospitalApiService = hospitalApiService;
    }

    @GetMapping(value = "apiData", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getHospitalsJson() {
        // 서비스에서 고정된 시군구코드로 API 호출 후 JSON 리스트를 받아옴
        List<String> jsonResults = hospitalApiService.fetchAllHospitals();
        return jsonResults;
    }
}