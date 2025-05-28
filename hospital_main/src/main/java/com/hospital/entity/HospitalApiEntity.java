package com.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospital_main")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalApiEntity {

	@Id
	@JsonProperty("ykiho")
	@Column(name = "hospital_code")
	private String hospitalCode;

	@JsonProperty("yadmNm")
	@Column(name = "hospital_name")
	private String hospitalName;

	@JsonProperty("sidoCdNm")
	@Column(name = "province_name")
	private String provinceName;

	@JsonProperty("sgguCdNm")
	@Column(name = "district_name")
	private String districtName;

	@JsonProperty("addr")
	@Column(name = "hospital_address")
	private String hospitalAddress;

	@JsonProperty("telno")
	@Column(name = "hospital_tel")
	private String hospitalTel;

	@JsonProperty("hospUrl")
	@Column(name = "hospital_homepage")
	private String hospitalHomepage;

	@JsonProperty("drTotCnt")
	@Column(name = "doctor_num")
	private int doctorNum;

	@JsonProperty("XPos")
	@Column(name = "coordinate_x")
	private double coordinateX;

	@JsonProperty("YPos")
	@Column(name = "coordinate_y")
	private double coordinateY;

	public String getHospitalCode() {
		return hospitalCode;
	}

	public void setHospitalCode(String hospitalCode) {
		this.hospitalCode = hospitalCode;
	}

	public String getHospitalName() {
		return hospitalName;
	}

	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getHospitalAddress() {
		return hospitalAddress;
	}

	public void setHospitalAddress(String hospitalAddress) {
		this.hospitalAddress = hospitalAddress;
	}

	public String getHospitalTel() {
		return hospitalTel;
	}

	public void setHospitalTel(String hospitalTel) {
		this.hospitalTel = hospitalTel;
	}

	public String getHospitalHomepage() {
		return hospitalHomepage;
	}

	public void setHospitalHomepage(String hospitalHomepage) {
		this.hospitalHomepage = hospitalHomepage;
	}

	public int getDoctorNum() {
		return doctorNum;
	}

	public void setDoctorNum(int doctorNum) {
		this.doctorNum = doctorNum;
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

	@Override
	public String toString() {
		return "HospitalApiEntity{" + "hospitalCode='" + hospitalCode + '\'' + ", hospitalName='" + hospitalName + '\''
				+ ", provinceName='" + provinceName + '\'' + ", districtName='" + districtName + '\''
				+ ", hospitalAddress='" + hospitalAddress + '\'' + ", hospitalTel='" + hospitalTel + '\''
				+ ", hospitalHomepage='" + hospitalHomepage + '\'' + ", doctorNum=" + doctorNum + ", coordinateX="
				+ coordinateX + ", coordinateY=" + coordinateY + '}';
	}
}
