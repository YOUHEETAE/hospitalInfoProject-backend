package com.hospital.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "response")
public class EmergencyLocationApiResponse {

	@JsonProperty("header")
	private Header header;

	@JsonProperty("body")
	private Body body;

	// 직접 header, body에 접근 가능하도록 반환
	public EmergencyLocationApiResponse getResponse() {
		return this;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Header {

		@JsonProperty("resultCode")
		private String ResultCode;

		@JsonProperty("resultMsg")
		private String ResultMsg;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Body {

		@JsonProperty("items")
		private ApiItemsWrapper items;


		@JsonProperty("pageNo")
		private int pageNo;

		@JsonProperty("numOfRows")
		private int numOfRows;

		@JsonProperty("totalCount")
		private int totalCount;

	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class ApiItemsWrapper {

		@JacksonXmlElementWrapper(useWrapping = false)
		@JacksonXmlProperty(localName = "item")
		private List<EmergencyLocationApiItem> item;
	}

}
