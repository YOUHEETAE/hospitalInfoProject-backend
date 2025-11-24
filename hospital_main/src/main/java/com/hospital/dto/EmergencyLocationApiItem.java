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
public class EmergencyLocationApiItem {
	
	@JsonProperty("hpid")
	private String hpid;
	
	@JsonProperty("wgs84Lat")
	private String coordinateY;
	
	@JsonProperty("wgs84Lon")
	private String coordinateX;
	
	@JsonProperty("dutyAddr")
	private String emergencyAddress;

}
