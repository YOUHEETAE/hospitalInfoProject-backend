package com.hospital.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class HospitalApiEntity {

	// DB의 Primary Key (자동 증가)
	private Long id;

	// --- 기본 정보 ---
	@JsonProperty("addr") // 주소
	private String address;

	@JsonProperty("clCd") // 종별코드 (예: 01)
	private String clinicCode;

	@JsonProperty("clCdNm") // 종별코드명 (예: 상급종합)
	private String clinicCodeName;

	@JsonProperty("emdongNm") // 읍면동명 (예: 구미동)
	private String emdongName;

	@JsonProperty("estbDd") // 설립일자 (예: 20030415)
	private String establishDate;

	@JsonProperty("hospNm") // 병원명 (가장 대표적인 병원 이름)
	private String hospName;

	@JsonProperty("hospUrl") // 병원 홈페이지 URL
	private String hospitalUrl;

	@JsonProperty("postNo") // 우편번호
	private String postNo;

	@JsonProperty("sgguCd") // 시군구 코드 (예: 310403)
	private String sigunguCode;

	@JsonProperty("sgguCdNm") // 시군구 코드명 (예: 성남분당구)
	private String sigunguCodeName;

	@JsonProperty("sidoCd") // 시도 코드 (예: 310000)
	private String sidoCode;

	@JsonProperty("sidoCdNm") // 시도 코드명 (예: 경기)
	private String sidoCodeName;

	@JsonProperty("telno") // 전화번호
	private String telNo;

	@JsonProperty("XPos") // X좌표 (경도)
	private Double xPos; // double 또는 String으로 처리 가능. API가 Double을 제공하는지 확인 필요

	@JsonProperty("YPos") // Y좌표 (위도)
	private Double yPos; // double 또는 String으로 처리 가능

	@JsonProperty("yadmNm") // 요양기관명 (hospNm과 유사할 수 있음)
	private String yadmName;

	@JsonProperty("ykiho") // 요양기관 기호
	private String ykiho;

	// --- 의사/간호사 수 정보 ---
	@JsonProperty("cmdcGdrCnt") // 한의사 일반의 수
	private Integer cmdcGdrCount; // Integer (정수)

	@JsonProperty("cmdcIntnCnt") // 한의사 인턴 수
	private Integer cmdcIntnCount;

	@JsonProperty("cmdcResdntCnt") // 한의사 레지던트 수
	private Integer cmdcResdntCount;

	@JsonProperty("cmdcSdrCnt") // 한의사 전문의 수
	private Integer cmdcSdrCount;

	@JsonProperty("detyGdrCnt") // 치과 일반의 수
	private Integer detyGdrCount;

	@JsonProperty("detyIntnCnt") // 치과 인턴 수
	private Integer detyIntnCount;

	@JsonProperty("detyResdntCnt") // 치과 레지던트 수
	private Integer detyResdntCount;

	@JsonProperty("detySdrCnt") // 치과 전문의 수
	private Integer detySdrCount;

	@JsonProperty("drTotCnt") // 의사 총 수
	private Integer drTotCount;

	@JsonProperty("mdeptGdrCnt") // 의과 일반의 수
	private Integer mdeptGdrCount;

	@JsonProperty("mdeptIntnCnt") // 의과 인턴 수
	private Integer mdeptIntnCount;

	@JsonProperty("mdeptResdntCnt") // 의과 레지던트 수
	private Integer mdeptResdntCount;

	@JsonProperty("mdeptSdrCnt") // 의과 전문의 수
	private Integer mdeptSdrCount;

	@JsonProperty("pnursCnt") // 전문간호사 수
	private Integer pnursCount;

	// --- 생성자 ---
	public HospitalApiEntity() {
	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getClinicCode() {
		return clinicCode;
	}

	public void setClinicCode(String clinicCode) {
		this.clinicCode = clinicCode;
	}

	public String getClinicCodeName() {
		return clinicCodeName;
	}

	public void setClinicCodeName(String clinicCodeName) {
		this.clinicCodeName = clinicCodeName;
	}

	public String getEmdongName() {
		return emdongName;
	}

	public void setEmdongName(String emdongName) {
		this.emdongName = emdongName;
	}

	public String getEstablishDate() {
		return establishDate;
	}

	public void setEstablishDate(String establishDate) {
		this.establishDate = establishDate;
	}

	public String getHospName() {
		return hospName;
	}

	public void setHospName(String hospName) {
		this.hospName = hospName;
	}

	public String getHospitalUrl() {
		return hospitalUrl;
	}

	public void setHospitalUrl(String hospitalUrl) {
		this.hospitalUrl = hospitalUrl;
	}

	public String getPostNo() {
		return postNo;
	}

	public void setPostNo(String postNo) {
		this.postNo = postNo;
	}

	public String getSigunguCode() {
		return sigunguCode;
	}

	public void setSigunguCode(String sigunguCode) {
		this.sigunguCode = sigunguCode;
	}

	public String getSigunguCodeName() {
		return sigunguCodeName;
	}

	public void setSigunguCodeName(String sigunguCodeName) {
		this.sigunguCodeName = sigunguCodeName;
	}

	public String getSidoCode() {
		return sidoCode;
	}

	public void setSidoCode(String sidoCode) {
		this.sidoCode = sidoCode;
	}

	public String getSidoCodeName() {
		return sidoCodeName;
	}

	public void setSidoCodeName(String sidoCodeName) {
		this.sidoCodeName = sidoCodeName;
	}

	public String getTelNo() {
		return telNo;
	}

	public void setTelNo(String telNo) {
		this.telNo = telNo;
	}

	public Double getXPos() {
		return xPos;
	}

	public void setXPos(Double xPos) {
		this.xPos = xPos;
	}

	public Double getYPos() {
		return yPos;
	}

	public void setYPos(Double yPos) {
		this.yPos = yPos;
	}

	public String getYadmName() {
		return yadmName;
	}

	public void setYadmName(String yadmName) {
		this.yadmName = yadmName;
	}

	public String getYkiho() {
		return ykiho;
	}

	public void setYkiho(String ykiho) {
		this.ykiho = ykiho;
	}

	public Integer getCmdcGdrCount() {
		return cmdcGdrCount;
	}

	public void setCmdcGdrCount(Integer cmdcGdrCount) {
		this.cmdcGdrCount = cmdcGdrCount;
	}

	public Integer getCmdcIntnCount() {
		return cmdcIntnCount;
	}

	public void setCmdcIntnCount(Integer cmdcIntnCount) {
		this.cmdcIntnCount = cmdcIntnCount;
	}

	public Integer getCmdcResdntCount() {
		return cmdcResdntCount;
	}

	public void setCmdcResdntCount(Integer cmdcResdntCount) {
		this.cmdcResdntCount = cmdcResdntCount;
	}

	public Integer getCmdcSdrCount() {
		return cmdcSdrCount;
	}

	public void setCmdcSdrCount(Integer cmdcSdrCount) {
		this.cmdcSdrCount = cmdcSdrCount;
	}

	public Integer getDetyGdrCount() {
		return detyGdrCount;
	}

	public void setDetyGdrCount(Integer detyGdrCount) {
		this.detyGdrCount = detyGdrCount;
	}

	public Integer getDetyIntnCount() {
		return detyIntnCount;
	}

	public void setDetyIntnCount(Integer detyIntnCount) {
		this.detyIntnCount = detyIntnCount;
	}

	public Integer getDetyResdntCount() {
		return detyResdntCount;
	}

	public void setDetyResdntCount(Integer detyResdntCount) {
		this.detyResdntCount = detyResdntCount;
	}

	public Integer getDetySdrCount() {
		return detySdrCount;
	}

	public void setDetySdrCount(Integer detySdrCount) {
		this.detySdrCount = detySdrCount;
	}

	public Integer getDrTotCount() {
		return drTotCount;
	}

	public void setDrTotCount(Integer drTotCount) {
		this.drTotCount = drTotCount;
	}

	public Integer getMdeptGdrCount() {
		return mdeptGdrCount;
	}

	public void setMdeptGdrCount(Integer mdeptGdrCount) {
		this.mdeptGdrCount = mdeptGdrCount;
	}

	public Integer getMdeptIntnCount() {
		return mdeptIntnCount;
	}

	public void setMdeptIntnCount(Integer mdeptIntnCount) {
		this.mdeptIntnCount = mdeptIntnCount;
	}

	public Integer getMdeptResdntCount() {
		return mdeptResdntCount;
	}

	public void setMdeptResdntCount(Integer mdeptResdntCount) {
		this.mdeptResdntCount = mdeptResdntCount;
	}

	public Integer getMdeptSdrCount() {
		return mdeptSdrCount;
	}

	public void setMdeptSdrCount(Integer mdeptSdrCount) {
		this.mdeptSdrCount = mdeptSdrCount;
	}

	public Integer getPnursCount() {
		return pnursCount;
	}

	public void setPnursCount(Integer pnursCount) {
		this.pnursCount = pnursCount;
	}

	// --- toString() 메서드 ---
	@Override
	public String toString() {
		return "HospitalApiEntity{" + "id=" + id + ", address='" + address + '\'' + ", clinicCode='" + clinicCode + '\''
				+ ", clinicCodeName='" + clinicCodeName + '\'' + ", cmdcGdrCount=" + cmdcGdrCount + ", cmdcIntnCount="
				+ cmdcIntnCount + ", cmdcResdntCount=" + cmdcResdntCount + ", cmdcSdrCount=" + cmdcSdrCount
				+ ", detyGdrCount=" + detyGdrCount + ", detyIntnCount=" + detyIntnCount + ", detyResdntCount="
				+ detyResdntCount + ", detySdrCount=" + detySdrCount + ", drTotCount=" + drTotCount + ", emdongName='"
				+ emdongName + '\'' + ", establishDate='" + establishDate + '\'' + ", hospName='" + hospName + '\''
				+ ", hospitalUrl='" + hospitalUrl + '\'' + ", mdeptGdrCount=" + mdeptGdrCount + ", mdeptIntnCount="
				+ mdeptIntnCount + ", mdeptResdntCount=" + mdeptResdntCount + ", mdeptSdrCount=" + mdeptSdrCount
				+ ", pnursCount=" + pnursCount + ", postNo='" + postNo + '\'' + ", sigunguCode='" + sigunguCode + '\''
				+ ", sigunguCodeName='" + sigunguCodeName + '\'' + ", sidoCode='" + sidoCode + '\'' + ", sidoCodeName='"
				+ sidoCodeName + '\'' + ", telNo='" + telNo + '\'' + ", xPos=" + xPos + ", yPos=" + yPos
				+ ", yadmName='" + yadmName + '\'' + ", ykiho='" + ykiho + '\'' + '}';
	}
}