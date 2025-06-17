package com.hospital.service;

import com.hospital.async.HospitalDetailAsyncRunner;
import com.hospital.repository.HospitalDetailApiRepository;
import com.hospital.repository.HospitalMainApiRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


//HospitalDetailApiServiceImpl 병원 상세정보 수집 및 저장 기능 구현체
@Slf4j
@Service
public class HospitalDetailApiService {

	private final HospitalMainApiRepository hospitalMainApiRepository;
	private final HospitalDetailAsyncRunner hospitalDetailAsyncRunner;
	private final HospitalDetailApiRepository hospitalDetailRepository;

	@Autowired
	public HospitalDetailApiService(HospitalMainApiRepository hospitalMainApiRepository,
			HospitalDetailAsyncRunner hospitalDetailAsyncRunner, HospitalDetailApiRepository hospitalDetailRepository) {
		this.hospitalDetailRepository = hospitalDetailRepository;
		this.hospitalDetailAsyncRunner = hospitalDetailAsyncRunner;
		this.hospitalMainApiRepository = hospitalMainApiRepository;
	}

	public int updateAllHospitalDetails() {
		try {
			// 기존 데이터 전체 삭제
			hospitalDetailRepository.deleteAllDetails();

			// 병원 코드 리스트 불러오기
			List<String> hospitalCodes = hospitalMainApiRepository.findAllHospitalCodes();
			if (hospitalCodes.isEmpty()) {
				throw new IllegalStateException("병원 기본정보가 없어 상세정보를 수집할 수 없습니다");
			}

			// 비동기 상태 초기화
			hospitalDetailAsyncRunner.resetCounter();
			hospitalDetailAsyncRunner.setTotalCount(hospitalCodes.size());

			// 병원 코드별 API 호출
			for (String hospitalCode : hospitalCodes) {
				hospitalDetailAsyncRunner.runAsync(hospitalCode);
			}

			return hospitalCodes.size();
			
		} catch (Exception e) {
			log.error("병원 상세정보 업데이트 실패", e);
			throw new RuntimeException("병원 상세정보 업데이트 중 오류 발생: " + e.getMessage(), e);
		}
	}

	public int getCompletedCount() {
		return hospitalDetailAsyncRunner.getCompletedCount();
	}

	public int getFailedCount() {
		return hospitalDetailAsyncRunner.getFailedCount();
	}

	public int getTotalCount() {
		return hospitalDetailAsyncRunner.getCompletedCount() + hospitalDetailAsyncRunner.getFailedCount();
	}
}