package com.hospital.repository;

import com.hospital.entity.HospitalApiEntity;
import java.util.List;

public interface HospitalApiRepository {

	int[] insertHospitals(List<HospitalApiEntity> hospitals);

	void createHospitalTable();
    List<HospitalApiEntity> findAllHospitals(); 
}