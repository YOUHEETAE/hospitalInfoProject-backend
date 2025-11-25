package com.hospital.analyzer;

public enum RiskLevel {
    DANGER("위험"),
    SURGE("급증"),
    WARNING("경계"),
    CAUTION("주의"),
    INTEREST("관심"),
    INSUFFICIENT("데이터부족");

    private final String label;

    RiskLevel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
