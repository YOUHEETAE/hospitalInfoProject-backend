package com.hospital.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.hospital.service.PharmacyApiService;

@RestController
@RequestMapping("/api/pharmacy") // ✔️ 약국 관련 API prefix
@RequiredArgsConstructor
public class PharmacyApiController {

    private final PharmacyApiService pharmacyService;

    /**
     * 약국 데이터 저장 트리거 API
     * - 성남 지역 전체(구 포함) 약국 데이터를 API 호출 후 저장
     * - GET http://localhost:8888/hospital_main/api/pharmacy/save
     */
    @GetMapping(value = "/save", produces = "text/plain;charset=UTF-8")
    public String savePharmacyData() {
        String[] sgguCodes = {"310401", "310402", "310403"};
        int totalSaved = 0;

        for (String sgguCd : sgguCodes) {
            totalSaved += pharmacyService.fetchAndSaveByDistrict(sgguCd);
        }

        return String.format("✅ 약국 데이터 저장 완료! 총 %d건 저장됨 (성남시 전체)", totalSaved);
    }

}
