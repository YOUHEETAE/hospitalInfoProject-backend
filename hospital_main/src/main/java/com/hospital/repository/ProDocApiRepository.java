package com.hospital.repository;

import com.hospital.entity.ProDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProDocApiRepository extends JpaRepository<ProDoc, Long> {
}
