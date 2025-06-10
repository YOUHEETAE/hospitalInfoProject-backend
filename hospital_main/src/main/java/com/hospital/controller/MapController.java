package com.hospital.controller;


import com.hospital.dto.web.HospitalResponseDTO;
import com.hospital.service.HospitalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class MapController {

    private final HospitalService hospitalService;

    @Autowired
    public MapController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }
    
    
    @GetMapping(value ="/mapData", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<HospitalResponseDTO> getHospitals(@RequestParam String sub, @RequestParam double userLat, 
    		                              @RequestParam double userLng ,@RequestParam double radius,      		                              
    		                              @RequestParam(required = false) List<String> tags) {
        return hospitalService.getHospitals(sub, userLat, userLng, radius, tags); // 서비스에서 병원 데이터 가져오기
    }
}