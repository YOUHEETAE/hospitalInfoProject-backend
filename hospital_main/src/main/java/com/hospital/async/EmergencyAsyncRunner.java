package com.hospital.async;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.caller.EmergencyApiCaller;
import com.hospital.config.RegionConfig;
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
public class EmergencyAsyncRunner {

    private final EmergencyApiCaller apiCaller;
    private final RegionConfig regionConfig;
    private final ObjectMapper objectMapper;
    private final TaskScheduler taskScheduler;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private ScheduledFuture<?> scheduledTask;

    // 통계
    private final AtomicInteger completedCount = new AtomicInteger(0);
    private final AtomicInteger failedCount = new AtomicInteger(0);
    private final AtomicInteger processedCount = new AtomicInteger(0);

    @Autowired
    public EmergencyAsyncRunner(EmergencyApiCaller apiCaller,
                               RegionConfig regionConfig,
                               TaskScheduler taskScheduler) {
        this.apiCaller = apiCaller;
        this.regionConfig = regionConfig;
        this.taskScheduler = taskScheduler;
        this.objectMapper = new ObjectMapper();
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
     * 모든 도시 데이터를 순차로 수집
     */
    public void collectAllCitiesData(Consumer<List<EmergencyWebResponse>> callback) {
        long startTime = System.currentTimeMillis();
        List<String> cities = regionConfig.getEmergencyCityNames();
        log.info("🔄 응급실 데이터 순차 수집 시작 - 도시 수: {}", cities.size());

        resetCounters();

        List<EmergencyWebResponse> allCityData = new ArrayList<>();

        for (String city : cities) {
            try {
                log.info("🔄 {} 데이터 수집 시작", city);

                List<EmergencyWebResponse> cityData = collectCityData(city);

                if (!cityData.isEmpty()) {
                    allCityData.addAll(cityData);
                    processedCount.addAndGet(cityData.size());
                    log.info("✅ {} 수집 완료 - {} 건", city, cityData.size());
                } else {
                    log.info("⚠️ {} 데이터 없음", city);
                }
                completedCount.incrementAndGet();

            } catch (Exception e) {
                failedCount.incrementAndGet();
                log.error("❌ {} 수집 실패: {}", city, e.getMessage());
            }
        }

        if (!allCityData.isEmpty()) {
            callback.accept(allCityData);
            long duration = System.currentTimeMillis() - startTime;
            log.info("✅ 순차 수집 완료 - 총 {} 건 (성공: {}, 실패: {}, 소요시간: {}ms)",
                    processedCount.get(), completedCount.get(), failedCount.get(), duration);
        } else {
            log.warn("⚠️ 수집된 데이터가 없습니다.");
        }
    }

    /**
     * 특정 도시의 응급실 데이터 수집 (한 페이지, 최대 100건)
     */
    private List<EmergencyWebResponse> collectCityData(String city) throws Exception {
        List<EmergencyWebResponse> cityData = new ArrayList<>();
        int numOfRows = 100;

        try {
            // 페이지 1만 호출
            List<JsonNode> responseList = apiCaller.callEmergencyApiByCityPage(city, 1, numOfRows);

            if (responseList == null || responseList.isEmpty()) {
                log.debug("📭 {} 페이지 1 - 응답 없음", city);
                return cityData;
            }

            for (JsonNode node : responseList) {
                try {
                    JsonNode bodyNode = node.path("body");
                    if (bodyNode.isMissingNode()) continue;

                    JsonNode itemsNode = bodyNode.path("items");
                    if (itemsNode.isMissingNode()) continue;

                    JsonNode itemNode = itemsNode.path("item");
                    if (itemNode.isMissingNode() || !itemNode.isArray()) continue;

                    EmergencyApiResponse[] apiArr = objectMapper.treeToValue(itemNode, EmergencyApiResponse[].class);
                    if (apiArr != null && apiArr.length > 0) {
                        for (EmergencyApiResponse apiResponse : apiArr) {
                            EmergencyWebResponse webResponse = EmergencyWebResponse.from(apiResponse);
                            cityData.add(webResponse);
                        }
                    }
                } catch (Exception parseEx) {
                    log.warn("⚠️ {} 페이지 1 JSON 파싱 오류: {}", city, parseEx.getMessage());
                }
            }

            log.debug("✅ {} 전체 수집 완료 - 총 {} 건", city, cityData.size());

        } catch (Exception e) {
            log.error("❌ {} 수집 실패: {}", city, e.getMessage());
            throw e;
        }

        return cityData;
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
        stats.put("totalCities", regionConfig.getEmergencyCityNames().size());
        return stats;
    }
}
