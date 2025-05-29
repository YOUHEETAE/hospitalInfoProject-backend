package com.hospital.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hospital.entity.Hospital;
import com.hospital.entity.HospitalApiEntity;
import com.hospital.entity.HospitalEntity;
import com.hospital.repository.HospitalMainRepository;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



@Service
public class HospitalMainServiceImpl implements HospitalMainService{

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final HospitalMainRepository hospitalMainRepository;

    // @Autowired 어노테이션은 생성자가 하나일 경우 생략 가능하지만 명시적으로 두어도 무방합니다.
    public HospitalMainServiceImpl(HospitalMainRepository hospitalMainRepository) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(15000); // 연결 타임아웃 10초 (10000ms)
        factory.setReadTimeout(60000);    // 읽기 타임아웃 30초 (30000ms)
        this.restTemplate = new RestTemplate(factory); // RestTemplate 객체를 생성자 내에서 직접 생성

        this.objectMapper = new ObjectMapper(); // ObjectMapper 객체를 생성자 내에서 직접 생성
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.hospitalMainRepository = hospitalMainRepository;
    }

    // 서비스 키와 기본 URL은 필드 초기화로 처리
    private final String baseUrl = "https://apis.data.go.kr/B551182/hospInfoServicev2/getHospBasisList";
    // 현재 예시로 사용할 시군구 코드 리스트 (서울 종로구, 중구, 용산구)
    private final List<String> sigunguCodes = Arrays.asList("310401", "310402", "310403");
    
    private final String serviceKey = "6IeDxLyk3cFOR8Fpgdyar0bwLjz07UptNyvbUC3KT3SRjcKdjyHG8Rt+DJ90JVPGwgH+GalAJveVPKnlYSKIfg==";



   
    
    public int fetchParseAndSaveHospitals() {
        System.out.println("Starting to fetch, parse, and save hospitals to DB...");
        int totalSavedOrUpdatedCount = 0;
        List<Hospital> allHospitalsToSave = new ArrayList<>(); // 모든 시군구의 병원을 담을 리스트

        // 테이블 생성 (이미 있으면 스킵). ykiho UNIQUE 제약조건 추가 로직 포함되어야 함.
        hospitalMainRepository.createHospitalTable();

        for (String sgguCd : sigunguCodes) {
            System.out.println("Processing sigunguCode: " + sgguCd);
            int pageNo = 1;
            int numOfRows = 1000; // 한 페이지당 가져올 데이터 수 (API가 허용하는 최대치에 가깝게 설정하여 호출 횟수 줄임)
            boolean hasMorePages = true;
            int currentPageFetchedCount = 0; // 현재 페이지에서 가져온 데이터 수 (페이징 종료 조건 확인용)

            while (hasMorePages) {
                String encodedServiceKey;
                String encodedSgguCd;
                try {
                    encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
                    encodedSgguCd = URLEncoder.encode(sgguCd, StandardCharsets.UTF_8.toString());
                } catch (Exception e) { // UnsupportedEncodingException 대신 더 일반적인 Exception으로 처리
                    System.err.println("서비스 키 또는 시군구 코드 인코딩 실패: " + e.getMessage());
                    break; // 현재 시군구 코드 처리 중단
                }

                String apiUrl = String.format("%s?serviceKey=%s&pageNo=%d&numOfRows=%d&sgguCd=%s&_type=json",
                                               baseUrl, encodedServiceKey, pageNo, numOfRows, encodedSgguCd);

                try {
                    URI uri = new URI(apiUrl); // URI 객체 생성

                    // API 호출
                    String responseJson = restTemplate.getForObject(uri, String.class);

                    JsonNode rootNode = objectMapper.readTree(responseJson);
                    JsonNode headerNode = rootNode.path("response").path("header");
                    String resultCode = headerNode.path("resultCode").asText();
                    String resultMsg = headerNode.path("resultMsg").asText();

                    if (!"00".equals(resultCode)) {
                        System.err.println("API 호출 실패: " + resultCode + " - " + resultMsg + " (sigunguCode: " + sgguCd + ", page: " + pageNo + ")");
                        hasMorePages = false; // API 에러 시 현재 시군구 코드 처리 중단
                        continue; // 다음 페이지로 넘어가지 않고 다음 sigunguCode 처리
                    }

                    JsonNode bodyNode = rootNode.path("response").path("body");
                    JsonNode itemsNode = bodyNode.path("items").path("item");
                    int totalCount = bodyNode.path("totalCount").asInt(0); // 총 데이터 수

                    List<Hospital> currentBatch = new ArrayList<>();

                    if (itemsNode.isArray()) {
                        for (JsonNode itemNode : itemsNode) {
                            Hospital hospital = objectMapper.treeToValue(itemNode, Hospital.class);
                            currentBatch.add(hospital);
                        }
                    } else if (itemsNode.isObject() && !itemsNode.isMissingNode()) {
                        // 결과가 단일 객체일 경우 (item이 1개일 때)
                        Hospital hospital = objectMapper.treeToValue(itemsNode, Hospital.class);
                        currentBatch.add(hospital);
                    }
                    // else if (itemsNode.isMissingNode()) { // items 노드가 없는 경우 (데이터 없음)
                    //     hasMorePages = false;
                    //     System.out.println("Sigungu " + sgguCd + ", Page " + pageNo + ": 'item' node is missing or empty.");
                    // }

                    if (!currentBatch.isEmpty()) {
                        allHospitalsToSave.addAll(currentBatch);
                        currentPageFetchedCount += currentBatch.size();
                        System.out.println("Sigungu " + sgguCd + ", Page " + pageNo + ": Fetched " + currentBatch.size() + " hospitals. Total fetched for sigungu: " + currentPageFetchedCount);

                        // 다음 페이지를 계속 가져올지 판단
                        // (현재까지 가져온 수가 총 수보다 작고, 현재 배치 사이즈가 numOfRows와 같으면 다음 페이지가 있을 가능성)
                        if (currentPageFetchedCount < totalCount && currentBatch.size() == numOfRows) {
                            pageNo++; // 다음 페이지로
                        } else {
                            hasMorePages = false; // 더 이상 가져올 데이터가 없거나 마지막 페이지
                        }
                        Thread.sleep(500);

                    } else {
                 
                        hasMorePages = false;
                        System.out.println("Sigungu " + sgguCd + ", Page " + pageNo + ": No more data found.");
                    }

                    // 짧은 지연 시간 (API 과부하 방지, 필요 시 활성화)
                    // Thread.sleep(500); // 0.5초 대기

                } catch (URISyntaxException e) {
                    System.err.println("URL 생성 오류 (URISyntaxException) for sigunguCode " + sgguCd + ", page " + pageNo + ": " + e.getMessage());
                    hasMorePages = false; // URL 오류 시 현재 시군구 처리 중단
                } catch (JsonProcessingException e) {
                    System.err.println("JSON 파싱 오류 (JsonProcessingException) for sigunguCode " + sgguCd + ", page " + pageNo + ": " + e.getMessage());
                    hasMorePages = false; // JSON 파싱 오류 시 현재 시군구 처리 중단
                } catch (Exception e) { // 나머지 모든 예외 처리
                    System.err.println("API 호출 또는 처리 중 알 수 없는 오류 발생 for sigunguCode " + sgguCd + ", page " + pageNo + ": " + e.getMessage());
                    e.printStackTrace(); // 자세한 스택 트레이스 출력
                    hasMorePages = false; // 오류 발생 시 현재 시군구 처리 중단
                }
            }
        }

        // 모든 시군구 코드를 순회하며 모든 페이지의 데이터를 모은 후, 한 번에 DB에 저장/업데이트
        if (!allHospitalsToSave.isEmpty()) {
            try {
             
                int[] savedRows = hospitalMainRepository.insertHospitals(allHospitalsToSave);
                totalSavedOrUpdatedCount = Arrays.stream(savedRows).sum(); // 실제로 삽입/업데이트된 레코드 수
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