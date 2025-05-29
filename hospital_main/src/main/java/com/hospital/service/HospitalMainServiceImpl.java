package com.hospital.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hospital.entity.Hospital;
import com.hospital.repository.HospitalMainRepository;
import com.hospital.client.HospitalMainInfoApiCaller; 

import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import com.hospital.parser.HospitalMainInfoApiParser;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class HospitalMainServiceImpl implements HospitalMainService {

   
    
    private final HospitalMainRepository hospitalMainRepository;
    private final HospitalMainInfoApiCaller hospitalMainInfoApiCaller; // 여기에 `final` 키워드를 추가합니다.
    private final HospitalMainInfoApiParser  hospitalMainInfoApiParser;

    @Autowired
    public HospitalMainServiceImpl(HospitalMainRepository hospitalMainRepository, HospitalMainInfoApiCaller hospitalMainInfoApiCaller, HospitalMainInfoApiParser hospitalMainInfoApiParser) { // HospitalMainInfoApiCaller 주입
        this.hospitalMainRepository = hospitalMainRepository;
        this.hospitalMainInfoApiCaller = hospitalMainInfoApiCaller;
        this.hospitalMainInfoApiParser = hospitalMainInfoApiParser;
    }

   

    private final List<String> sigunguCodes = Arrays.asList("310401", "310402", "310403");

    public int fetchParseAndSaveHospitals() {
        System.out.println("Starting to fetch, parse, and save hospitals to DB...");
        int totalSavedOrUpdatedCount = 0;
        List<Hospital> allHospitalsToSave = new ArrayList<>();

        hospitalMainRepository.createHospitalTable();

        for (String sgguCd : sigunguCodes) {
            System.out.println("Processing sigunguCode: " + sgguCd);
            int pageNo = 1;
            int numOfRows = 1000;
            boolean hasMorePages = true;
            int currentPageFetchedCount = 0;

            while (hasMorePages) {
                String encodedSgguCd;
                try {
                    encodedSgguCd = URLEncoder.encode(sgguCd, StandardCharsets.UTF_8.toString());
                } catch (Exception e) {
                    System.err.println("시군구 코드 인코딩 실패: " + e.getMessage());
                    break;
                }

                // API 호출 로직을 HospitalMainInfoApiCaller에게 위임합니다.
                String apiPath = "hospInfoServicev2/getHospBasisList";
                String queryParams = String.format("pageNo=%d&numOfRows=%d&sgguCd=%s",
                                                  pageNo, numOfRows, encodedSgguCd);

                try {
                	JsonNode rootNode = hospitalMainInfoApiCaller.callApi(apiPath, queryParams);
                	
                	List<Hospital> currentBatch = hospitalMainInfoApiParser.parseHospitals(rootNode);

                    int totalCount = rootNode.path("response").path("body").path("totalCount").asInt(0);

                    if (!currentBatch.isEmpty()) {
                        allHospitalsToSave.addAll(currentBatch);
                        currentPageFetchedCount += currentBatch.size();
                        System.out.println("Sigungu " + sgguCd + ", Page " + pageNo + ": Fetched " + currentBatch.size() + " hospitals. Total fetched for sigungu: " + currentPageFetchedCount);

                        if (currentPageFetchedCount < totalCount && currentBatch.size() == numOfRows) {
                            pageNo++;
                        } else {
                            hasMorePages = false;
                        }
                        Thread.sleep(500);

                    } else {
                        hasMorePages = false;
                        System.out.println("Sigungu " + sgguCd + ", Page " + pageNo + ": No more data found.");
                    }

                } catch (JsonProcessingException e) {
                    System.err.println("JSON 파싱 오류 (JsonProcessingException) for sigunguCode " + sgguCd + ", page " + pageNo + ": " + e.getMessage());
                    hasMorePages = false;
                } catch (RuntimeException e) {
                    System.err.println("API 호출 또는 응답 처리 중 오류 발생 for sigunguCode " + sgguCd + ", page " + pageNo + ": " + e.getMessage());
                    e.printStackTrace();
                    hasMorePages = false;
                } catch (Exception e) {
                    System.err.println("알 수 없는 오류 발생 for sigunguCode " + sgguCd + ", page " + pageNo + ": " + e.getMessage());
                    e.printStackTrace();
                    hasMorePages = false;
                }
            }
        }

        if (!allHospitalsToSave.isEmpty()) {
            try {
                int[] savedRows = hospitalMainRepository.insertHospitals(allHospitalsToSave);
                totalSavedOrUpdatedCount = Arrays.stream(savedRows).sum();
                System.out.println("Successfully saved or updated " + totalSavedOrUpdatedCount + " hospitals into DB.");
            } catch (Exception e) {
                System.err.println("Error saving/updating hospitals to DB: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No hospitals fetched to save or update.");
        }

        System.out.println("Method finished: HospitalApiService.fetchParseAndSaveHospitals(), Result: " + totalSavedOrUpdatedCount);
        return totalSavedOrUpdatedCount;
    }

    public List<Hospital> getAllHospitals() {
        return hospitalMainRepository.findAllHospitals();
    }
}