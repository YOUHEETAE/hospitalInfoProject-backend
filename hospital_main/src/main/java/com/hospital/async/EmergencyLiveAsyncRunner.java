package com.hospital.async;

import com.hospital.caller.EmergencyApiCaller;
import com.hospital.dto.EmergencyApiItem;
import com.hospital.dto.EmergencyApiResponse;
import com.hospital.dto.EmergencyWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Service
@Slf4j
public class EmergencyLiveAsyncRunner {

    private final EmergencyApiCaller apiCaller;
    private final TaskScheduler taskScheduler;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledTask;

    // 통계
    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger processedCount = new AtomicInteger(0);

    @Autowired
    public EmergencyLiveAsyncRunner(EmergencyApiCaller apiCaller,
                               TaskScheduler taskScheduler) {
        this.apiCaller = apiCaller;
        this.taskScheduler = taskScheduler;
    }

    /**
     * 3분마다 반복 실행하는 스케줄러 시작
     */
    public void runAsyncForAllCities(Consumer<List<EmergencyWebResponse>> callback) {
        if (running.compareAndSet(false, true)) {
            log.info("✅ 응급실 3분 주기 스케줄러 시작");

            // 즉시 첫 실행
            taskScheduler.schedule(() -> collectAllCitiesData(callback), Instant.now());

            // 3분마다 반복 실행
            scheduledTask = taskScheduler.scheduleWithFixedDelay(() -> {
                if (running.get()) {
                    try {
                        collectAllCitiesData(callback);
                    } catch (Exception e) {
                        log.error("스케줄 실행 중 오류 발생: {}", e.getMessage(), e);
                    }
                }
            }, Instant.now().plusSeconds(180), Duration.ofMinutes(3));

        } else {
            log.warn("이미 스케줄러가 실행 중입니다.");
        }
    }

    /**
     * 전국 응급실 데이터를 한 번에 수집
     */
    public void collectAllCitiesData(Consumer<List<EmergencyWebResponse>> callback) {
        long startTime = System.currentTimeMillis();
        log.info("🔄 응급실 데이터 수집 시작");

        resetCounters();

        List<EmergencyWebResponse> allData = new ArrayList<>();

        try {
            log.info("🔄 전국 응급실 데이터 호출 시작 (pageNo=1, numOfRows=500)");

            EmergencyApiResponse response = apiCaller.callApi(1, 500);

            if (response == null || response.getBody() == null) {
                log.warn("⚠️ API 응답 없음");
                return;
            }

            // EmergencyApiResponse를 EmergencyWebResponse 리스트로 변환
            List<EmergencyWebResponse> data = parseResponse(response);

            if (!data.isEmpty()) {
                allData.addAll(data);
                processedCount.addAndGet(data.size());
                completedCount.incrementAndGet();
                log.info("✅ 데이터 수집 완료 - {} 건", data.size());
            } else {
                log.warn("⚠️ 파싱된 데이터 없음");
            }

        } catch (Exception e) {
            failedCount.incrementAndGet();
            log.error("❌ 데이터 수집 실패: {}", e.getMessage());
        }

        if (!allData.isEmpty()) {
            callback.accept(allData);
            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ 수집 완료 - 총 {}건 (소요시간: {}ms)", processedCount.get(), duration);
        } else {
            log.warn("⚠️ 수집된 데이터가 없습니다.");
        }
    }

    /**
     * API 응답을 EmergencyWebResponse 리스트로 변환
     */
    private List<EmergencyWebResponse> parseResponse(EmergencyApiResponse apiResponse) {
        List<EmergencyWebResponse> result = new ArrayList<>();

        if (apiResponse == null || apiResponse.getBody() == null) {
            return result;
        }

        EmergencyApiResponse.Body body = apiResponse.getBody();
        if (body == null || body.getItems() == null) {
            return result;
        }

        List<EmergencyApiItem> items = body.getItems().getItem();
        if (items == null || items.isEmpty()) {
            return result;
        }

        for (EmergencyApiItem item : items) {
            try {
                EmergencyWebResponse webResponse = EmergencyWebResponse.from(item);
                result.add(webResponse);
            } catch (Exception e) {
                log.warn("⚠️ 응급실 데이터 변환 오류: {}", e.getMessage());
            }
        }

        return result;
    }

    /**
     * 스케줄러 중지
     */
    public void stopAsync() {
        if (running.compareAndSet(true, false)) {
            log.info("🔄 응급실 스케줄러 중지 요청");

            if (scheduledTask != null && !scheduledTask.isDone()) {
                boolean cancelled = scheduledTask.cancel(false);
                log.info("📋 스케줄 태스크 취소 결과: {}", cancelled);
            }

            log.info("✅ 응급실 스케줄러 중지 완료");
        } else {
            log.debug("⚠️ 스케줄러가 이미 중지되어 있습니다.");
        }
    }

    // 상태 조회 메서드들
    public boolean isRunning() {
        return running.get() && scheduledTask != null && !scheduledTask.isDone();
    }

    public int getCompletedCount() { return completedCount.get(); }
    public int getFailedCount() { return failedCount.get(); }
    public int getProcessedCount() { return processedCount.get(); }

    private void resetCounters() {
        completedCount.set(0);
        failedCount.set(0);
        processedCount.set(0);
    }

    /**
     * 통계 정보 반환
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("running", isRunning());
        stats.put("completed", completedCount.get());
        stats.put("failed", failedCount.get());
        stats.put("processed", processedCount.get());
        return stats;
    }
}
