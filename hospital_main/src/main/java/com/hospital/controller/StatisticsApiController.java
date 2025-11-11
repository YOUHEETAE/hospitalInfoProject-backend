package com.hospital.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.caller.DiseaseStatsApiCaller;
import com.hospital.dto.DiseaseStatsApiResponse;

@RestController
@RequestMapping("/api/stats")
public class StatisticsApiController {
	
	private DiseaseStatsApiCaller diseaseStatsApiCaller;
	
	public StatisticsApiController(DiseaseStatsApiCaller diseaseStatsApiCaller) {
		this.diseaseStatsApiCaller = diseaseStatsApiCaller;
	}
	
	@GetMapping(value = "/diseaseStatsData", produces = MediaType.APPLICATION_JSON_VALUE)
	public DiseaseStatsApiResponse diseaseStatsData(){
		DiseaseStatsApiResponse responseData = diseaseStatsApiCaller.callApi(2025, 2025, 1);
		return responseData;
	}
	

}
