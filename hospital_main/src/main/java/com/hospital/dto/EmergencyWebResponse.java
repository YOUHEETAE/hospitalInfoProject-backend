package com.hospital.dto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmergencyWebResponse {

	// === 필수 기본 정보 ===
	@JsonProperty("dutyName")
	private String dutyName; // 기관명

	@JsonProperty("dutyTel3")
	private String dutyTel3; // 응급실 전화번호

	@JsonProperty("hpid")
	private String hpid; // 기관 코드

	@JsonProperty("hvidate")
	private String hvidate; // 입력일시


	@JsonProperty("hvamyn")
	@JsonDeserialize(using = YNToBooleanDeserializer.class)
	private Boolean hvamyn; // 구급차 가용 여부

	// === 좌표 정보 (DB에서 추가) ===
	private Double coordinateX; // x 좌표 (위도)
	private Double coordinateY; // y 좌표 (경도)

	private String emergencyAddress;
	
	// === 장비 가용 정보 리스트 ===
	public List<String> availableEquipment;
	
	
	// === 핵심 병상 현황 ===
	public Map<String, Integer> availableBeds;
	
	public static EmergencyWebResponse from(EmergencyApiResponse api) {
	   List<String> equipmentData = availableEquipment(api);
	   Map<String, Integer> BedsData = availableBeds(api);

	    return EmergencyWebResponse.builder()
	        .dutyName(api.getDutyName())
	        .dutyTel3(api.getDutyTel3())
	        .hpid(api.getHpid())
	        .hvidate(convertToIsoUtc(api.getLastUpdatedDate()))
	        .availableBeds(BedsData)
	        .availableEquipment(equipmentData)
	        .hvamyn(convertYNToBoolean(api.getAmbulanceAvailability()))
	        .coordinateX(api.getCoordinateX())
	        .coordinateY(api.getCoordinateY())
	        .emergencyAddress(api.getEmergencyAddress())
	        .build();
	}
	

	/**
	 * Y/N 문자열을 Boolean으로 변환
	 */
	private static Boolean convertYNToBoolean(String value) {
	    if (value == null) {
	        return null;
	    }
	    return "Y".equalsIgnoreCase(value.trim());
	}

	private static String convertToIsoUtc(String dateString) {
	    if (dateString == null || dateString.length() != 14) {
	        return null;
	    }

	    try {
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	        LocalDateTime localDateTime = LocalDateTime.parse(dateString, inputFormatter);
	        
	        // UTC 시간으로 먼저 설정
	        ZonedDateTime utcDateTime = localDateTime.atZone(ZoneId.of("UTC"));
	        
	        // UTC를 KST(한국 시간)로 변환
	        ZonedDateTime kstDateTime = utcDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"));

	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM월 dd일 HH시 mm분 ss초");
	        return kstDateTime.format(outputFormatter);

	    } catch (Exception e) {
	        return dateString; // 변환 실패 시 원본 반환
	    }
	}

	// 장비 추출 로직 분리 (Y/N 문자열 처리)
	private static List<String> availableEquipment(EmergencyApiResponse api) {
	    Map<String, String> equipmentMap = new LinkedHashMap<>();
	    equipmentMap.put("인공호흡기", api.getVentilatorAvailability());
	    equipmentMap.put("CT", api.getCtAvailability());
	    equipmentMap.put("MRI", api.getMriAvailability());
	    equipmentMap.put("CRRT", api.getCrrtAvailability());

	    return equipmentMap.entrySet().stream()
	        .filter(entry -> "Y".equalsIgnoreCase(entry.getValue()))
	        .map(Map.Entry::getKey)
	        .collect(Collectors.toList());
	}
	
	private static Map<String, Integer> availableBeds(EmergencyApiResponse api){
		Map <String, Integer> bedsMap = new LinkedHashMap<>();
		bedsMap.put("응급실 일반 병상", api.getEmergencyBeds());
		bedsMap.put("수술실 병상", api.getOperatingBeds());
		bedsMap.put("일반 입원실 병상", api.getGeneralWardBeds());
		
		return bedsMap;
	}

	/**
	 * Y/N 문자열을 Boolean으로 변환하는 커스텀 Deserializer
	 */
	public static class YNToBooleanDeserializer extends JsonDeserializer<Boolean> {
		@Override
		public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
			String value = p.getText();
			if (value == null || value.trim().isEmpty()) {
				return null;
			}
			return "Y".equalsIgnoreCase(value.trim());
		}
	}

}
