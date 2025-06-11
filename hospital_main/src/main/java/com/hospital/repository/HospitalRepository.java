package com.hospital.repository;

import com.hospital.entity.HospitalMain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


public interface HospitalRepository extends JpaRepository<HospitalMain, String> {
    
    // 특정 진료과목을 가진 병원들의 모든 정보 조회
    @EntityGraph(attributePaths = {"hospitalDetail", "proDocs"})
    @Query("SELECT DISTINCT h FROM HospitalMain h " +
           "JOIN h.medicalSubjects ms " +
           "WHERE ms.subjectName LIKE %:subjectName%")
    List<HospitalMain> findHospitalsBySubject(@Param("subjectName") String sub);
    
   
}