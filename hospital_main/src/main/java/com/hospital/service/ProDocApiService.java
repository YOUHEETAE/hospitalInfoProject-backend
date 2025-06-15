package com.hospital.service;

import com.hospital.async.ProDocAsyncRunner;
import com.hospital.repository.HospitalMainApiRepository;
import com.hospital.repository.ProDocApiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 🧠 ProDocServiceImpl 전문의(ProDoc) 정보 수집 및 저장 기능 구현체
 */
@Service
public class ProDocApiService {
	
	private final HospitalMainApiRepository hospitalMainApiRepository;
	private final ProDocAsyncRunner proDocasyncRunner; // 전문의 API 비동기 실행기
	private final ProDocApiRepository proDocRepository; // 전문의 정보 저장소 (JPA)

	@Autowired
	public ProDocApiService(
			ProDocAsyncRunner proDocasyncRunner,
			ProDocApiRepository proDocRepository,HospitalMainApiRepository hospitalMainApiRepository) {
		
		this.proDocasyncRunner = proDocasyncRunner;
		this.proDocRepository = proDocRepository;
		this.hospitalMainApiRepository = hospitalMainApiRepository;
	}

	/**
	 * ✅ 병원 전체를 대상으로 API 호출 후 전문의 데이터 비동기 저장 실행 1. 기존 전문의 데이터 모두 삭제 2. 전체 병원코드 가져오기
	 * 3. 비동기 상태 초기화 및 총 작업 수 설정 4. 각 병원코드마다 runAsync() 호출
	 *
	 * @return 처리 대상 병원 수
	 */

	@Transactional
	public int fetchParseAndSaveProDocs() {
		// 기존 데이터 전체 삭제
		proDocRepository.deleteAllProDocs();

		proDocRepository.resetAutoIncrement();

		// 병원 코드 리스트 불러오기
		List<String> hospitalCodes = hospitalMainApiRepository.findAllHospitalCodes();

		// 비동기 상태 초기화
		proDocasyncRunner.resetCounter();
		proDocasyncRunner.setTotalCount(hospitalCodes.size());

		// 병원 코드별 API 호출
		for (String hospitalCode : hospitalCodes) {
			proDocasyncRunner.runAsync(hospitalCode); // 🔁 비동기 실행
		}

		return hospitalCodes.size(); // 전체 병원 수 반환
	}

	/**
	 * ✅ 완료된 병원 처리 수 조회
	 */
	
	public int getCompletedCount() {
		return proDocasyncRunner.getCompletedCount();
	}

	/**
	 * ✅ 실패한 병원 처리 수 조회
	 */
	
	public int getFailedCount() {
		return proDocasyncRunner.getFailedCount();
	}
}
