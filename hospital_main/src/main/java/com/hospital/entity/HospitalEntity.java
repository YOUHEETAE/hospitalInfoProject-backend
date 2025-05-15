package com.hospital.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospital_main")
public class HospitalEntity {

    @Id
    @Column(name = "hospital_code")
    private String hospitalCode; // 병원 고유 코드
    
    @Column(name = "subject_name")
    private String hospitalSubject; // 병원 고유 코드

    @Column(name = "hospital_name")
    private String hospitalName;

    @Column(name = "hospital_address")
    private String hospitalAddress;

    @Column(name = "coordinate_x")
    private double coordinateX;

    @Column(name = "coordinate_y")
    private double coordinateY;

    @Column(name = "emergency_available")
    private String emergencyAvailable;

    @Column(name = "park_available")
    private Integer parkAvailable;

    // 생성자
    public HospitalEntity() {}

    // getter, setter
    public String getHospitalCode() {
        return hospitalCode;
    }

    public void setHospitalCode(String hospitalCode) {
        this.hospitalCode = hospitalCode;
    }
    
    public String getHospitalSubject() {
        return hospitalSubject;
    }

    public void setHospitalSubject(String hospitalSubject) {
        this.hospitalSubject = hospitalSubject;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public double getCoordinateX() {
        return coordinateX;
    }

    public void setCoordinateX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getCoordinateY() {
        return coordinateY;
    }

    public void setCoordinateY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public String getEmergencyAvailable() {
        return emergencyAvailable;
    }

    public void setEmergencyAvailable(String emergencyAvailable) {
        this.emergencyAvailable = emergencyAvailable;
    }

    public Integer getParkAvailable() {
        return parkAvailable;
    }

    public void setParkAvailable(Integer parkAvailable) {
        this.parkAvailable = parkAvailable;
    }

    // 도메인 로직: 응급실 가능 여부 확인
    public boolean isEmergencyAvailable(String emergencyRoomInfo) {
        if (emergencyRoomInfo == null || emergencyRoomInfo.isEmpty() || emergencyRoomInfo.equals("0")) {
            return true; // 필터링 안 함
        }
        return "1".equals(emergencyRoomInfo) && !"N".equals(this.emergencyAvailable);
    }

    // 도메인 로직: 주차 가능 여부 확인
    public boolean isParkingAvailable(Integer parkingInfo) {
        if (parkingInfo == null || parkingInfo == 0) {
            return true; // 필터링 안 함
        }
        return parkingInfo == 1 && this.parkAvailable != null && this.parkAvailable != 0;
    }
}