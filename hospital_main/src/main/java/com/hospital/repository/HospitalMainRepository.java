package com.hospital.repository;

import com.hospital.entity.Hospital;

import java.util.List;

public interface HospitalMainRepository {

	int[] insertHospitals(List<Hospital> hospitals);

	void createHospitalTable();
    List<Hospital> findAllHospitals(); 
}