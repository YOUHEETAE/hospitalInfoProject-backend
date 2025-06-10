package com.hospital.repository;

import com.hospital.entity.MedicalSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalSubjectApiRepository extends JpaRepository<MedicalSubject, Long> {

    // ✅ 병원코드로 해당 병원의 모든 진료과목 조회
    List<MedicalSubject> findByHospitalCode(String hospitalCode);

    // ✅ 병원코드 리스트로 해당 병원들의 모든 진료과목 조회
    List<MedicalSubject> findByHospitalCodeIn(List<String> hospitalCodes);

    // ✅ 병원코드 + 과목명으로 중복 확인
    boolean existsByHospitalCodeAndSubjectName(String hospitalCode, String subjectName);

    // ✅ 병원코드 기준 기존 진료과목 전체 삭제
    void deleteByHospitalCode(String hospitalCode);
}
