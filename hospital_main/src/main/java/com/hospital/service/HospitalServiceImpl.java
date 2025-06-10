package com.hospital.service;

import com.hospital.entity.HospitalMain;

import com.hospital.repository.HospitalRepository;
import com.hospital.converter.HospitalConverter;
import com.hospital.domainLogic.HospitalTagFilter;

import com.hospital.dto.web.HospitalResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class HospitalServiceImpl implements HospitalService {

	private final HospitalRepository hospitalRepository;
	private final HospitalFilter hospitalFilter;
	private final HospitalConverter hospitalConverter;

	@Autowired
	public HospitalServiceImpl(HospitalRepository HospitalRepository, HospitalFilter hospitalFilter,
			HospitalConverter hospitalConverter) {
		this.hospitalRepository = HospitalRepository;
		this.hospitalFilter = hospitalFilter;
		this.hospitalConverter = hospitalConverter;

	}

	@Override
	public List<HospitalResponseDTO> getHospitals(String sub, double userLat, double userLng, double radius,
			List<String> tags) {

		List<HospitalMain> hospitalEntities = hospitalRepository.findHospitalsBySubject(sub);
		
		List<HospitalResponseDTO> hospitals = hospitalEntities.stream()

				.filter(hospital -> HospitalTagFilter.matchesAllTags(hospital, tags))

				.map(this.hospitalConverter::convertToDTO)

				.filter(hospitalResponseDTO -> hospitalFilter.filterByDistance(hospitalResponseDTO, userLat, userLng,
						radius))

				.collect(Collectors.toList());

		return hospitals;
	}

}