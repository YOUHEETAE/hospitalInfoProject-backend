package com.hospital.repository;

import com.hospital.entity.Hospital;

import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HospitalMainRepository extends JpaRepository<Hospital, String>{
	 @Query("SELECT h.hospitalCode FROM Hospital h")
	    List<String> findAllHospitalCodes();
	 
	
}
