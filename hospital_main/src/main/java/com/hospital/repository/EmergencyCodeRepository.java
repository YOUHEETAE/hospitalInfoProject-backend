package com.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.entity.EmergencyLocation;

public interface EmergencyCodeRepository extends JpaRepository<EmergencyLocation, String> {

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM emergency_code", nativeQuery = true)
	void deleteAllEmergencyCodes();

}
