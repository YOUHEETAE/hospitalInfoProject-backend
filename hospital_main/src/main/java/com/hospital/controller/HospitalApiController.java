package com.hospital.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.hospital.client.HospitalDetailApiCaller;
import com.hospital.entity.Hospital;
import com.hospital.entity.HospitalDetail;
import com.hospital.service.HospitalDetailApiService;
import com.hospital.service.HospitalMainService;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalApiController {

	private final HospitalMainService hospitalMainService;
	private final HospitalDetailApiService hospitalDetailApiService; // HospitalDetailApiService 주입을 위해 추가
	private final HospitalDetailApiCaller hospitalDetailApiCaller;

	public HospitalApiController(HospitalMainService hospitalMainService,
			HospitalDetailApiService hospitalDetailApiService, HospitalDetailApiCaller hospitalDetailApiCaller) {
		this.hospitalMainService = hospitalMainService;
		this.hospitalDetailApiService = hospitalDetailApiService;
		this.hospitalDetailApiCaller = hospitalDetailApiCaller;
	}

	@PostMapping(value = "/save", produces = MediaType.TEXT_PLAIN_VALUE)
	public String saveHospitalsToDb() {
		int savedCount = 0;
		try {
			System.out.println("Starting to fetch, parse, and save hospitals to DB...");
			savedCount = hospitalMainService.fetchParseAndSaveHospitals();
			return "병원 정보 " + savedCount + "개 DB 저장 완료!";
		} catch (Exception e) {
			System.err.println("Error occurred during DB save: " + e.getMessage());
			return "병원 정보 DB 저장 중 오류 발생: " + e.getMessage();
		}
	}

	@GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Hospital> getAllHospitals() {
		System.out.println("Fetching all hospitals from DB...");
		return hospitalMainService.getAllHospitals();
	}

	@GetMapping("/details")
	public ResponseEntity<String> fetchHospitalDetails() {
		List<String> allDetails = hospitalDetailApiCaller.fetchAllHospitalDetails(1, 10);
		String combinedJson = "[" + String.join(",", allDetails) + "]";
		return ResponseEntity.ok().header("Content-Type", "application/json; charset=UTF-8").body(combinedJson);
	}

	@PostMapping("/update-details")
	public ResponseEntity<String> updateHospitalDetails() {
		try {
			// 페이지 번호, 한 페이지당 데이터 수 등 필요시 조절 가능
			hospitalDetailApiService.updateAllHospitalDetails(1, 10);
			return ResponseEntity.ok("병원 상세 정보 업데이트 성공");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("병원 상세 정보 업데이트 실패: " + e.getMessage());
		}
	}
}