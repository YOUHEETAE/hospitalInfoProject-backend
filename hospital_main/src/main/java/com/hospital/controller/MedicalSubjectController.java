package com.hospital.controller;

import com.hospital.service.MedicalSubjectApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subject") // ✔️ 모든 진료과목 API는 이 prefix를 사용함 (/api/subject/...)
public class MedicalSubjectController {

    private final MedicalSubjectApiService medicalSubjectService;

    @Autowired
    public MedicalSubjectController(MedicalSubjectApiService medicalSubjectService) {
        this.medicalSubjectService = medicalSubjectService;
    }

    /**
     * 진료과목 데이터 저장 시작 (비동기)
     * - 모든 병원의 병원코드를 기준으로 공공 API에서 데이터를 받아옴
     * - 비동기로 각 병원에 대해 데이터를 파싱 → DB에 저장
     * - 진행상황은 /status API로 확인
     */
    @GetMapping(value = "/save", produces = MediaType.TEXT_PLAIN_VALUE)
    public String saveSubjects() {
        int total = medicalSubjectService.fetchParseAndSaveMedicalSubjects(); // 전체 병원 수 반환
        return String.format("진료과목 저장 시작됨! 전체 병원 수: %d개\n(진행상황은 로그 또는 /status API로 확인)\n", total);
    }

    /**
     * 저장 작업 진행상황 확인용 API
     * - 완료된 병원 수와 실패한 병원 수를 확인 가능
     * - 프론트나 로그 없이도 서버 상태 확인에 유용
     */
    @GetMapping(value = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getStatus() {
        int done = medicalSubjectService.getCompletedCount(); // 완료된 병원 수
        int fail = medicalSubjectService.getFailedCount();    // 실패한 병원 수
        return String.format("진료과목 진행상황: 완료 %d건, 실패 %d건\n", done, fail);
    }
}
