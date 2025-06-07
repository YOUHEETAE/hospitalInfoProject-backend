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
            
            for (HospitalDetail newDetail : details) {
                // 기존 엔티티 조회 (Optional)
                HospitalDetail existingDetail = hospitalDetailRepository.findById(newDetail.getHospitalCode())
                        .orElse(null);

                if (existingDetail != null) {
                    // 기존 엔티티가 있으면 업데이트
                    updateEntityWithNewData(existingDetail, newDetail);
                    hospitalDetailRepository.save(existingDetail);
                } else {
                    // 없으면 새로 저장
                    hospitalDetailRepository.save(newDetail);
                }
            }
        }
    }

    // 기존 엔티티 필드를 덮어쓰는 메서드
    private HospitalDetail updateEntityWithNewData(HospitalDetail existing, HospitalDetail newData) {
        existing.setEmyDayYn(newData.getEmyDayYn());
        existing.setEmyNightYn(newData.getEmyNightYn());
        existing.setParkQty(newData.getParkQty());
        existing.setLunchWeek(newData.getLunchWeek());
        existing.setRcvWeek(newData.getRcvWeek());
        existing.setRcvSat(newData.getRcvSat());
        existing.setTrmtMonStart(newData.getTrmtMonStart());
        existing.setTrmtMonEnd(newData.getTrmtMonEnd());
        existing.setTrmtTueStart(newData.getTrmtTueStart());
        existing.setTrmtTueEnd(newData.getTrmtTueEnd());
        existing.setTrmtWedStart(newData.getTrmtWedStart());
        existing.setTrmtWedEnd(newData.getTrmtWedEnd());
        existing.setTrmtThurStart(newData.getTrmtThurStart());
        existing.setTrmtThurEnd(newData.getTrmtThurEnd());
        existing.setTrmtFriStart(newData.getTrmtFriStart());
        existing.setTrmtFriEnd(newData.getTrmtFriEnd());
        return existing;
    }
}
