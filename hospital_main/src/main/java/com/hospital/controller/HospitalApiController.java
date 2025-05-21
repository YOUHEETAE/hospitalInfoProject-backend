package com.hospital.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping; 
import org.springframework.web.bind.annotation.RestController;

import com.hospital.entity.HospitalApiEntity;
import com.hospital.service.HospitalApiService;

@RestController
@RequestMapping("/api/hospitals") 
public class HospitalApiController {

    private final HospitalApiService hospitalApiService;

    public HospitalApiController(HospitalApiService hospitalApiService) {
        this.hospitalApiService = hospitalApiService;
    }

    @GetMapping(value = "/save", produces = MediaType.TEXT_PLAIN_VALUE)
    public String saveHospitalsToDb() {
        int savedCount = 0;
        try {
            System.out.println("Starting to fetch, parse, and save hospitals to DB...");
            savedCount = hospitalApiService.fetchParseAndSaveHospitals();
            return "병원 정보 " + savedCount + "개 DB 저장 완료!";
        } catch (Exception e) {
            System.err.println("Error occurred during DB save: " + e.getMessage());
            return "병원 정보 DB 저장 중 오류 발생: " + e.getMessage();
        }
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HospitalApiEntity> getAllHospitals() {
        System.out.println("Fetching all hospitals from DB...");
        return hospitalApiService.getAllHospitals();
    }
}