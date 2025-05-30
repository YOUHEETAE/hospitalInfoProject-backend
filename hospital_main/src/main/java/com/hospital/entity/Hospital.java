package com.hospital.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {

	private String hospitalCode;

	private String hospitalName;

	private String provinceName;

	private String districtName;

	private String hospitalAddress;

	private String hospitalTel;

	private String hospitalHomepage;

	private int doctorNum;

	private double coordinateX;

	private double coordinateY;

}