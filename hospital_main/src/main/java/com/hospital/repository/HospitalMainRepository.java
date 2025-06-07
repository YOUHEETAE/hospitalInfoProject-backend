package com.hospital.repository;

import com.hospital.entity.Hospital;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

public interface HospitalMainRepository extends JpaRepository<Hospital, String> {

    @Query("SELECT h.hospitalCode FROM Hospital h")
    List<String> findAllHospitalCodes();

    // 병원 코드로 병원 조회
    Optional<Hospital> findByHospitalCode(String hospitalCode);
}
