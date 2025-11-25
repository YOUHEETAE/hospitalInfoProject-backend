package com.hospital.analyzer;

import com.hospital.dto.DiseaseStatsWebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
@PropertySource("classpath:disease-classification.properties")
public class DiseaseTrendAnalyzer {

    private final Set<String> highVolumeDiseases;
    private final Set<String> criticalDiseases;
    private final Set<String> clusterDiseases;

    public DiseaseTrendAnalyzer(
            @Value("#{'${disease.classification.high-volume}'.split(',')}") List<String> highVolume,
            @Value("#{'${disease.classification.critical}'.split(',')}") List<String> critical,
            @Value("#{'${disease.classification.cluster}'.split(',')}") List<String> cluster) {
        this.highVolumeDiseases = new HashSet<>(highVolume);
        this.criticalDiseases = new HashSet<>(critical);
        this.clusterDiseases = new HashSet<>(cluster);
    }

    private static final double WEIGHT_AVG = 0.2;
    private static final double WEIGHT_RECENT = 0.8;

    private static final int MIN_COUNT_HIGH_VOLUME = 100;
    private static final int MIN_COUNT_CLUSTER = 5;
    private static final int MIN_COUNT_CRITICAL = 1;
    private static final int MIN_COUNT_DEFAULT = 20;

    private static final int SEVERE_LIMIT_HIGH_VOLUME = 500;
    private static final int SEVERE_LIMIT_CLUSTER = 15;
    private static final int SEVERE_LIMIT_CRITICAL = 3;
    private static final int ANALYSIS_WEEKS = 6;

    public RiskLevel analyzeTrend(
            String diseaseName,
            String icdGroupName,
            List<DiseaseStatsWebResponse.WeeklyData> weeklyData) {

        if (weeklyData == null || weeklyData.isEmpty()) {
            return RiskLevel.INSUFFICIENT;
        }

        List<DiseaseStatsWebResponse.WeeklyData> recentWeeks = getRecentWeeks(weeklyData, ANALYSIS_WEEKS);
        if (recentWeeks.size() < 2) {
            return RiskLevel.INSUFFICIENT;
        }

        List<Double> changeRates = calculateChangeRates(recentWeeks);
        if (changeRates.isEmpty()) {
            return RiskLevel.INSUFFICIENT;
        }

        double avgRate = changeRates.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double recentRate = changeRates.isEmpty() ? 0.0 : changeRates.get(changeRates.size() - 1);
        int recentCount = recentWeeks.get(recentWeeks.size() - 1).getCount();

        if ("제1급".equals(icdGroupName)) {
            return recentCount >= 1 ? RiskLevel.DANGER : RiskLevel.INTEREST;
        }

        int minThreshold = getMinCountThreshold(diseaseName);
        if (recentCount < minThreshold) {
            return RiskLevel.INTEREST;
        }

        double weightedRate = (avgRate * WEIGHT_AVG) + (recentRate * WEIGHT_RECENT);
        boolean isIncreasing = recentRate > avgRate;
        int consecutive = countConsecutiveIncreases(changeRates);

        int severeLimit = getSevereMinCountThreshold(diseaseName, icdGroupName);
        double[] rates = getRateThresholds(diseaseName);
        double severeRate = rates[0];
        double warningRate = rates[1];
        double cautionRate = rates[2];

        if (highVolumeDiseases.contains(diseaseName) && recentCount >= severeLimit) {
            if (weightedRate >= severeRate) {
                return RiskLevel.SURGE;
            }
            return RiskLevel.WARNING;
        }

        if (weightedRate >= severeRate || (consecutive >= 5 && avgRate >= severeRate * 0.6)) {
            return RiskLevel.SURGE;
        }

        if (weightedRate >= warningRate || (consecutive >= 4 && avgRate >= warningRate * 0.6)) {
            return RiskLevel.WARNING;
        }

        if (weightedRate >= cautionRate || (isIncreasing && avgRate >= cautionRate * 0.5)) {
            return RiskLevel.CAUTION;
        }

        return RiskLevel.INTEREST;
    }

    private int getMinCountThreshold(String diseaseName) {
        if (highVolumeDiseases.contains(diseaseName)) return MIN_COUNT_HIGH_VOLUME;
        if (criticalDiseases.contains(diseaseName)) return MIN_COUNT_CRITICAL;
        if (clusterDiseases.contains(diseaseName)) return MIN_COUNT_CLUSTER;
        return MIN_COUNT_DEFAULT;
    }

    private int getSevereMinCountThreshold(String diseaseName, String icdGroupName) {
        if (highVolumeDiseases.contains(diseaseName)) return SEVERE_LIMIT_HIGH_VOLUME;
        if (criticalDiseases.contains(diseaseName)) return SEVERE_LIMIT_CRITICAL;
        if (clusterDiseases.contains(diseaseName)) return SEVERE_LIMIT_CLUSTER;
        return "제2급".equals(icdGroupName) ? 30 : 50;
    }

    private double[] getRateThresholds(String diseaseName) {
        if (highVolumeDiseases.contains(diseaseName)) {
            return new double[]{30.0, 20.0, 10.0};
        }
        return new double[]{40.0, 25.0, 15.0};
    }

    private int countConsecutiveIncreases(List<Double> changeRates) {
        int count = 0;
        for (int i = changeRates.size() - 1; i >= 0; i--) {
            if (changeRates.get(i) > 0) count++;
            else break;
        }
        return count;
    }

    private List<DiseaseStatsWebResponse.WeeklyData> getRecentWeeks(
            List<DiseaseStatsWebResponse.WeeklyData> weeklyData, int weeks) {
        int size = weeklyData.size();
        int startIndex = Math.max(0, size - weeks);
        return weeklyData.subList(startIndex, size);
    }

    private List<Double> calculateChangeRates(List<DiseaseStatsWebResponse.WeeklyData> weeklyData) {
        List<Double> rates = new ArrayList<>();
        for (int i = 1; i < weeklyData.size(); i++) {
            int prev = weeklyData.get(i - 1).getCount();
            int current = weeklyData.get(i).getCount();
            if (prev == 0) {
                rates.add(current > 0 ? 100.0 : 0.0);
            } else {
                double rate = ((double) (current - prev) / prev) * 100;
                rates.add(rate);
            }
        }
        return rates;
    }

}
