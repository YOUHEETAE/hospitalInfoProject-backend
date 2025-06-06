package com.hospital.service;

import com.hospital.client.HospitalDetailApiCaller;
import com.hospital.entity.HospitalDetail;
import com.hospital.parser.HospitalDetailApiParser;
import com.hospital.repository.HospitalDetailRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class HospitalDetailApiService {

    private final HospitalDetailApiCaller apiCaller;
    private final HospitalCodeFetcher hospitalCodeFetcher;
    private final HospitalDetailApiParser parser;
    private final HospitalDetailRepository hospitalDetailRepository;

    public HospitalDetailApiService(HospitalDetailApiCaller apiCaller,
                                   HospitalCodeFetcher hospitalCodeFetcher,
                                   HospitalDetailApiParser parser,
                                   HospitalDetailRepository hospitalDetailRepository) {
        this.apiCaller = apiCaller;
        this.hospitalCodeFetcher = hospitalCodeFetcher;
        this.parser = parser;
        this.hospitalDetailRepository = hospitalDetailRepository;
    }

    @Transactional
    public void updateAllHospitalDetails(int pageNo, int numOfRows) {
        List<String> hospitalCodes = hospitalCodeFetcher.getAllHospitalCodes();
        List<HospitalDetail> allDetails = new ArrayList<>();

        for (String code : hospitalCodes) {
            String jsonResponse = apiCaller.fetchHospitalDetailByCode(code, pageNo, numOfRows);

            if (jsonResponse == null || jsonResponse.isEmpty()) {
                System.out.println("병원 코드 " + code + " 에 대한 데이터가 없거나 호출 실패, 넘어갑니다.");
                continue;
            }

            List<HospitalDetail> details = parser.parseToEntities(jsonResponse, code);
            allDetails.addAll(details);
        }

        hospitalDetailRepository.saveAll(allDetails);
    }
}
