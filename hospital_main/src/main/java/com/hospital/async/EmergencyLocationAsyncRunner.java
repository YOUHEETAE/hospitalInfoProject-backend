package com.hospital.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.hospital.caller.EmergencyLocationApiCaller;
import com.hospital.dto.EmergencyLocationApiResponse;
import com.hospital.entity.EmergencyLocation;
import com.hospital.parser.EmergencyLocationApiParser;
import com.hospital.repository.EmergencyLocationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmergencyLocationAsyncRunner {

	private final AtomicInteger completedCount = new AtomicInteger(0);
	private final AtomicInteger failedCount = new AtomicInteger(0);
	private final AtomicInteger insertedCount = new AtomicInteger(0);

	private final EmergencyLocationApiCaller apiCaller;
	private final EmergencyLocationApiParser parser;
	private final EmergencyLocationRepository emergencyLocationRepository;

	private static final int BATCH_SIZE = 100;

	@Autowired
	public EmergencyLocationAsyncRunner(EmergencyLocationApiCaller apiCaller, EmergencyLocationApiParser parser,
			EmergencyLocationRepository emergencyLocationRepository) {
		this.apiCaller = apiCaller;
		this.parser = parser;
		this.emergencyLocationRepository = emergencyLocationRepository;
	}

	@Async("apiExecutor")
	public void runAsync(int pageNo, int numOfRow) {
		try {
			log.info("응급실 위치 수집 시작");

			List<EmergencyLocation> allLocations = new ArrayList<>();
			boolean hasMorePages = true;

			while (hasMorePages) {
				log.debug("페이지 {} 호출 중...", pageNo);

				EmergencyLocationApiResponse response = apiCaller.callApi(pageNo, numOfRow);
				List<EmergencyLocation> Location = parser.parse(response);

				if (Location.isEmpty()) {
					log.info("페이지 {}: 더 이상 데이터 없음", pageNo);
					break;
				}

				allLocations.addAll(Location);
				log.info("페이지 {} 수집 완료: {}건", pageNo, Location.size());

				pageNo++;

				// 100건보다 적으면 마지막 페이지로 간주
				hasMorePages = Location.size() >= 100;
			}

			// 배치 저장
			int insertedTotal = 0;
			for (int i = 0; i < allLocations.size(); i += BATCH_SIZE) {
				int end = Math.min(i + BATCH_SIZE, allLocations.size());
				List<EmergencyLocation> batch = allLocations.subList(i, end);
				emergencyLocationRepository.saveAll(batch);
				insertedTotal += batch.size();
				log.info("배치 저장: {}건 완료 (누적: {}건)", batch.size(), insertedTotal);
			}

			insertedCount.addAndGet(insertedTotal);
			completedCount.incrementAndGet();

			log.info("응급실 위치 수집 완료 , 총 {}건 저장",  insertedTotal);

		} catch (Exception e) {
			failedCount.incrementAndGet();
			log.error("응급실 위치 수집 실패 , 오류: {}", e.getMessage(), e);
		}
	}

	// 상태 관리 메서드
	public int getCompletedCount() {
		return completedCount.get();
	}

	public int getFailedCount() {
		return failedCount.get();
	}

	public int getInsertedCount() {
		return insertedCount.get();
	}

	public void resetCounter() {
		completedCount.set(0);
		failedCount.set(0);
		insertedCount.set(0);
	}
}
