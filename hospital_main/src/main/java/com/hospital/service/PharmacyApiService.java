package com.hospital.service;

import com.hospital.async.PharmacyAsyncRunner;
import com.hospital.repository.PharmacyApiRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PharmacyApiService {

    private final PharmacyAsyncRunner pharmacyAsyncRunner;
    private final PharmacyApiRepository pharmacyApiRepository;

    @Autowired
    public PharmacyApiService(PharmacyAsyncRunner pharmacyAsyncRunner,
                              PharmacyApiRepository pharmacyApiRepository) {
        this.pharmacyAsyncRunner = pharmacyAsyncRunner;
        this.pharmacyApiRepository = pharmacyApiRepository;
    }

    /**
     * 전국 약국 데이터 수집 (비동기 처리)
     * - 시도 구분 없이 전국 데이터를 페이지 단위로 수집
     * - numOfRows=500, pageNo를 증가시키며 호출
     */
    public int savePharmacy() {
        log.info("전국 약국 데이터 수집 시작");

        // 기존 데이터 삭제
        pharmacyApiRepository.deleteAllPharmacies();
        pharmacyApiRepository.resetAutoIncrement();
        log.info("기존 약국 데이터 전체 삭제 완료");

        // 카운터 초기화 및 비동기 실행 (단일 작업)
        pharmacyAsyncRunner.setTotalCount(1);
        pharmacyAsyncRunner.runAsync();

        log.info("전국 약국 데이터 비동기 처리 시작");
        return 1;
    }

    public int getCompletedCount() {
		return pharmacyAsyncRunner.getCompletedCount();
	}

	public int getFailedCount() {
		return pharmacyAsyncRunner.getFailedCount();
	}

	public int getInsertedCount() {
		return pharmacyAsyncRunner.getInsertedCount();
	}
}