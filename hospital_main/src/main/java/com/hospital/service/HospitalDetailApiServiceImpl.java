package com.hospital.service;

import com.hospital.async.HospitalDetailAsyncRunner;
import com.hospital.repository.HospitalDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 🧠 HospitalDetailApiServiceImpl
 * 병원 상세정보 수집 및 저장 기능 구현체
 */
@Slf4j
@Service
public class HospitalDetailApiServiceImpl implements HospitalDetailApiService {
    
    private final HospitalMainService hospitalMainService; // 병원 목록 가져오는 서비스
    private final HospitalDetailAsyncRunner asyncRunner;   // 병원 상세정보 API 비동기 실행기
    private final HospitalDetailRepository hospitalDetailRepository; // 병원 상세정보 저장소 (JPA)
    
    @Autowired
    public HospitalDetailApiServiceImpl(HospitalMainService hospitalMainService,
                                      HospitalDetailAsyncRunner asyncRunner,
                                      HospitalDetailRepository hospitalDetailRepository) {
        this.hospitalMainService = hospitalMainService;
        this.asyncRunner = asyncRunner;
        this.hospitalDetailRepository = hospitalDetailRepository;
    }
    
    /**
     * ✅ 병원 전체를 대상으로 API 호출 후 병원 상세 데이터 비동기 저장 실행
     * 1. 기존 병원 상세 데이터 모두 삭제
     * 2. 전체 병원코드 가져오기
     * 3. 비동기 상태 초기화 및 총 작업 수 설정
     * 4. 각 병원코드마다 runAsync() 호출
     *
     * @return 처리 대상 병원 수
     */
    @Override
    @Transactional
    public int updateAllHospitalDetails() {
        log.info("병원 상세정보 업데이트 시작");
        
        // 기존 데이터 전체 삭제
        long deletedCount = hospitalDetailRepository.count();
        hospitalDetailRepository.deleteAll();
        log.info("기존 병원 상세정보 {}개 삭제 완료", deletedCount);
        
        // 병원 코드 리스트 불러오기
        List<String> hospitalCodes = hospitalMainService.getAllHospitalCodes();
        if (hospitalCodes.isEmpty()) {
            log.warn("처리할 병원 코드가 없습니다.");
            return 0;
        }
        
        log.info("총 {}개 병원의 상세정보를 처리합니다.", hospitalCodes.size());
        
        // 비동기 상태 초기화
        asyncRunner.resetCounter();
        asyncRunner.setTotalCount(hospitalCodes.size());
        
        // 병원 코드별 API 호출
        for (String hospitalCode : hospitalCodes) {
            asyncRunner.runAsync(hospitalCode); // 🔁 비동기 실행
        }
        
        log.info("비동기 처리 요청 완료. 진행상황은 로그에서 확인하세요.");
        return hospitalCodes.size(); // 전체 병원 수 반환
    }
    
    /**
     * ✅ 완료된 병원 처리 수 조회
     */
    @Override
    public int getCompletedCount() {
        return asyncRunner.getCompletedCount();
    }
    
    /**
     * ✅ 실패한 병원 처리 수 조회
     */
    @Override
    public int getFailedCount() {
        return asyncRunner.getFailedCount();
    }
    
    /**
     * ✅ 전체 작업 수 조회
     */
    @Override
    public int getTotalCount() {
        return asyncRunner.getCompletedCount() + asyncRunner.getFailedCount();
    }
}