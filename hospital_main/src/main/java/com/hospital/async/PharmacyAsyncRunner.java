package com.hospital.async;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.util.concurrent.RateLimiter;
import com.hospital.caller.PharmacyApiCaller;
import com.hospital.dto.PharmacyApiResponse;
import com.hospital.entity.Pharmacy;
import com.hospital.parser.PharmacyApiParser;
import com.hospital.repository.PharmacyApiRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PharmacyAsyncRunner {

    private final RateLimiter rateLimiter = RateLimiter.create(5.0);
    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger insertedCount = new AtomicInteger(0);
    private int totalCount = 0;

    private final PharmacyApiCaller apiCaller;
    private final PharmacyApiParser parser;
    private final PharmacyApiRepository pharmacyApiRepository;

    private static final int BATCH_SIZE = 100;

    @Autowired
    public PharmacyAsyncRunner(PharmacyApiCaller apiCaller,
                               PharmacyApiParser parser,
                               PharmacyApiRepository pharmacyApiRepository) {
        this.apiCaller = apiCaller;
        this.parser = parser;
        this.pharmacyApiRepository = pharmacyApiRepository;
    }

    @Async("apiExecutor")
    public void runAsync() {
        long startTime = System.currentTimeMillis();
        log.info("🔄 전국 약국 데이터 호출 시작 (pageNo=1~, numOfRows=500)");

        try {
            List<Pharmacy> allPharmacies = new ArrayList<>();
            int pageNo = 1;
            int numOfRows = 500;
            boolean hasMorePages = true;

            while (hasMorePages) {
                rateLimiter.acquire();

                log.debug("약국 API 호출 - 페이지: {}, 행 수: {}", pageNo, numOfRows);
                PharmacyApiResponse response = apiCaller.callPharmacyApiByPage(pageNo, numOfRows);
                List<Pharmacy> pharmacies = parser.parsePharmacies(response);

                if (pharmacies.isEmpty()) {
                    log.info("✅ 페이지 {}: 더 이상 데이터 없음 (처리 종료)", pageNo);
                    break;
                }

                allPharmacies.addAll(pharmacies);
                log.info("📄 페이지 {} 완료: {}건 수집 (누적: {}건)", pageNo, pharmacies.size(), allPharmacies.size());

                // 페이지 단위 대기
                Thread.sleep(200);
                pageNo++;

                // 받은 데이터가 numOfRows보다 적으면 마지막 페이지
                hasMorePages = pharmacies.size() >= numOfRows;
            }

            // 배치 저장
            int insertedTotal = 0;
            for (int i = 0; i < allPharmacies.size(); i += BATCH_SIZE) {
                int end = Math.min(i + BATCH_SIZE, allPharmacies.size());
                List<Pharmacy> batch = allPharmacies.subList(i, end);
                pharmacyApiRepository.saveAll(batch);
                insertedTotal += batch.size();
            }

            insertedCount.addAndGet(insertedTotal);
            completedCount.incrementAndGet();

            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ 전국 약국 데이터 수집 완료: 총 {}건 저장 (소요시간: {}ms)", insertedTotal, duration);

        } catch (Exception e) {
            failedCount.incrementAndGet();
            log.error("❌ 약국 데이터 수집 실패: {}", e.getMessage(), e);
        }
    }

    // ✅ 상태 관리 메서드
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

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        resetCounter();
    }

    public int getTotalCount() {
        return totalCount;
    }
}