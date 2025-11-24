package com.hospital.dto;

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
    // 질병 정보
    private String icdGroupName; 
    private String icdName;      
    
    // 주차별 데이터
    private List<WeeklyData> weeklyData;
    
    //합계 데이터
    private Integer totalCount;
    
    
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