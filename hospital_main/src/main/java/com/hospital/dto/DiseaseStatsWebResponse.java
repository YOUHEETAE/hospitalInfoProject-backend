package com.hospital.dto;

import com.hospital.analyzer.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiseaseStatsWebResponse {
    private String icdGroupName;
    private String icdName;
    private List<WeeklyData> weeklyData;
    private Integer totalCount;
    private RiskLevel riskLevel;
    private Integer recentChange;
    private Double recentChangeRate;
    
    
    // 내부 클래스
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeeklyData {
        private String period;
        private Integer count;  
    }
}