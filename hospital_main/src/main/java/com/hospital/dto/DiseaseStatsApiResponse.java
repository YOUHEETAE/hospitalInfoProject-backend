package com.hospital.dto;

import java.util.List;

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
public class DiseaseStatsApiResponse {

	@JsonProperty("response")
	private Response response;

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Response {

		@JsonProperty("header")
		private Header header;

		@JsonProperty("body")
		private Body body;
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Header {

		@JsonProperty("resultCode")
		private String resultCode;

		@JsonProperty("resultMsg")
		private String resultMsg;
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
		@JsonProperty("item")
		private List<DiseaseStatsApiItem> item;
	}

}
