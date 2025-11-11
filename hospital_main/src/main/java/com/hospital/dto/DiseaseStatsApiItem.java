package com.hospital.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DiseaseStatsApiItem {

	@JsonProperty("period")
	private String period;

	@JsonProperty("icdGroupNm")
	private String icdGroupNm;

	@JsonProperty("icdNm")
	private String icdNm;

	@JsonProperty("resultVal")
	private String resultVal;

}
