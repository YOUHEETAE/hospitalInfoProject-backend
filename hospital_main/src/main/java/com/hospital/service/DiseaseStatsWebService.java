package com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.converter.DiseaseStatsConverter;
import com.hospital.dto.DiseaseStatsWebResponse;
import com.hospital.entity.DiseaseStats;
import com.hospital.repository.DiseaseStatsRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DiseaseStatsWebService {
	private DiseaseStatsRepository diseaseStatsRepository;
	private DiseaseStatsConverter diseaseStatsConverter;
	
	public DiseaseStatsWebService(DiseaseStatsRepository diseaseStatsRepository, DiseaseStatsConverter diseaseStatsConverter) {
		this.diseaseStatsRepository = diseaseStatsRepository;
		this.diseaseStatsConverter = diseaseStatsConverter;
	}
	
	public List<DiseaseStatsWebResponse> diseaseStatsData() {
		List<DiseaseStats> entities = diseaseStatsRepository.findAll();
		
		List<DiseaseStatsWebResponse> responses  = diseaseStatsConverter.convertToDtos(entities);
		
		return responses;
	}
}
