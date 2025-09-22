package com.hospital.entity;

import java.util.Set;

import org.hibernate.annotations.DynamicUpdate;

// JPA 관련 임포트 추가
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = { "hospitalDetail", "medicalSubjects", "proDocs" })
@Entity
@DynamicUpdate
@Table(name = "hospital_main")
@NamedEntityGraphs({
		@NamedEntityGraph(name = "hospital-with-detail", attributeNodes = @NamedAttributeNode("hospitalDetail")),
		@NamedEntityGraph(name = "hospital-with-medical-subjects", attributeNodes = @NamedAttributeNode("medicalSubjects")),
		@NamedEntityGraph(name = "hospital-with-pro-docs", attributeNodes = @NamedAttributeNode("proDocs")),
		// 모든 연관관계를 한번에 로딩
		@NamedEntityGraph(name = "hospital-with-all", attributeNodes = { @NamedAttributeNode("hospitalDetail"),
				@NamedAttributeNode("medicalSubjects"), @NamedAttributeNode("proDocs") }) })
public class HospitalMain {


	
	@Id
	@Column(name = "hospital_code", nullable = false, length = 255) // 컬럼 이름과 속성 지정
	private String hospitalCode;

	@Column(name = "hospital_name", nullable = false, length = 255)
	private String hospitalName;

	@Column(name = "province_name", length = 100)
	private String provinceName;

	@Column(name = "district_name", length = 100)
	private String districtName;

	@Column(name = "hospital_address", length = 500)
	private String hospitalAddress;

	@Column(name = "hospital_tel", length = 20)
	private String hospitalTel;

	@Column(name = "hospital_homepage", length = 255)
	private String hospitalHomepage;

	@Column(name = "total_doctors")
	private String totalDoctors;

	@Column(name = "coordinate_x")
	private Double coordinateX;

	@Column(name = "coordinate_y")
	private Double coordinateY;
	
	@Column(name = "med_general_cnt")
    private String medGeneralCnt;  // mdeptGdrCnt
    
    @Column(name = "med_intern_cnt")
    private String medInternCnt;  // mdeptIntnCnt
    
    @Column(name = "med_resident_cnt")
    private String medResidentCnt;  // mdeptResdntCnt
    
    @Column(name = "med_specialist_cnt")
    private String medSpecialistCnt;  // mdeptSdrCnt
    
    // 치과
    @Column(name = "dent_general_cnt")
    private String dentGeneralCnt;  // detyGdrCnt
    
    @Column(name = "dent_intern_cnt")
    private String dentInternCnt;  // detyIntnCnt
    
    @Column(name = "dent_resident_cnt")
    private String dentResidentCnt;  // detyResdntCnt
    
    @Column(name = "dent_specialist_cnt")
    private String dentSpecialistCnt;  // detySdrCnt
    
    // 한방
    @Column(name = "oriental_general_cnt")
    private String orientalGeneralCnt;  // cmdcGdrCnt
    
    @Column(name = "oriental_intern_cnt")
    private String orientalInternCnt;  // cmdcIntnCnt
    
    @Column(name = "oriental_resident_cnt")
    private String orientalResidentCnt;  // cmdcResdntCnt
    
    @Column(name = "oriental_specialist_cnt")
    private String orientalSpecialistCnt;  // cmdcSdrCnt
    
    // 기타
    @Column(name = "midwife_cnt")
    private String midwifeCnt;  // pnursCnt
    
    @Lob
    @Column(name = "subject_names", length = 1000)
    private String subjectNames; // ["내과", "외과", "정형외과"]
    
    

	@OneToOne(mappedBy = "hospital",

			fetch = FetchType.LAZY)
	private HospitalDetail hospitalDetail;

	@OneToMany(mappedBy = "hospital",

			fetch = FetchType.LAZY)
	private Set<MedicalSubject> medicalSubjects;

	@OneToMany(mappedBy = "hospital",

			fetch = FetchType.LAZY)
	private Set<ProDoc> proDocs;
}
