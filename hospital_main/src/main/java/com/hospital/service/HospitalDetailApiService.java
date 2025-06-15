package com.hospital.service;

import com.hospital.async.HospitalDetailAsyncRunner;
import com.hospital.repository.HospitalDetailApiRepository;
import com.hospital.repository.HospitalMainApiRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 🧠 HospitalDetailApiServiceImpl
 * 병원 상세정보 수집 및 저장 기능 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HospitalDetailApiService  {
    
	private final HospitalMainApiRepository hospitalMainApiRepository;
    private final HospitalDetailAsyncRunner asyncRunner;   // 병원 상세정보 API 비동기 실행기
    private final HospitalDetailApiRepository hospitalDetailRepository; // 병원 상세정보 저장소 (JPA)
   

    public int updateAllHospitalDetails() {
        // 기존 데이터 전체 삭제
        hospitalDetailRepository.deleteAllDetails();
        
        // 병원 코드 리스트 불러오기
        List<String> hospitalCodes = hospitalMainApiRepository.findAllHospitalCodes();
        
        // 비동기 상태 초기화
        asyncRunner.resetCounter();
        asyncRunner.setTotalCount(hospitalCodes.size());
        
        // 병원 코드별 API 호출
        for (String hospitalCode : hospitalCodes) {
            asyncRunner.runAsync(hospitalCode); // 🔁 비동기 실행
        }
        
        return hospitalCodes.size(); // 전체 병원 수 반환
    }
    
    /**
     * 완료된 병원 처리 수 조회
     */
    
    public int getCompletedCount() {
        return asyncRunner.getCompletedCount();
    }
    
    /**
     * 실패한 병원 처리 수 조회
     */
 
    public int getFailedCount() {
        return asyncRunner.getFailedCount();
    }
    
    /**
     * 전체 작업 수 조회
     */

    public int getTotalCount() {
        return asyncRunner.getCompletedCount() + asyncRunner.getFailedCount();
    }
}