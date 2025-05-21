package com.hospital.dto;

public class HospitalApiDTO {
	private String yadmNm; // 병원명
	private String addr; // 주소
	private String telno; // 전화번호
	private double xpos; // 경도
	private double ypos; // 위도

	// 기본 생성자 + getter/setter
	public HospitalApiDTO(String yadmNm, String addr, String telno, double xpos, double ypos) {
		this.yadmNm = yadmNm;
		this.addr = addr;
		this.telno = telno;
		this.xpos = xpos;
		this.ypos = ypos;

	}

	public String getYadmNm() {
		return yadmNm;
	}

	public void setYadmNm(String yadmNm) {
		this.yadmNm = yadmNm;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getTelno() {
		return telno;
	}

	public void setTelno(String telno) {
		this.telno = telno;
	}

	public double getXpos() {
		return xpos;
	}

	public void setXpos(double xpos) {
		this.xpos = xpos;
	}

	public double getYpos() {
		return ypos;
	}

	public void setYpos(double ypos) {
		this.ypos = ypos;
	}

}
