package com.hospital.repository;

import com.hospital.entity.DiseaseStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseStatsRepository extends JpaRepository<DiseaseStats, Long> {

}
