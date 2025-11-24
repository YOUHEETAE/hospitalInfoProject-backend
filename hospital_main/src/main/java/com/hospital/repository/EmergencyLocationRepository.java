package com.hospital.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.entity.EmergencyLocation;

public interface EmergencyLocationRepository extends JpaRepository<EmergencyLocation, String> {

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM emergency_Location", nativeQuery = true)
	void deleteAllEmergencyLocations();

	/**
	 * hpid 목록으로 좌표 및 주소 정보 배치 조회 (IN 쿼리)
	 * @param hpidList 응급실 병원 코드 목록
	 * @return [emergencyCode, coordinateX, coordinateY, emergencyAddress]
	 */
	@Query("SELECT e.emergencyCode, e.coordinateX, e.coordinateY, e.emergencyAddress FROM EmergencyLocation e WHERE e.emergencyCode IN :hpidList")
	List<Object[]> findCoordinatesByHpidList(@Param("hpidList") List<String> hpidList);

}
