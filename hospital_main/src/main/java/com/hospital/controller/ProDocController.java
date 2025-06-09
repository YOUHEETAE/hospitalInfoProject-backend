package com.hospital.controller;

import com.hospital.service.ProDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prodoc") // ✔️ 전문의 관련 API는 이 prefix를 사용함
public class ProDocController {

    private final ProDocService proDocService;

    @Autowired
    public ProDocController(ProDocService proDocService) {
        this.proDocService = proDocService;
    }

    /**
     * 전문의 정보 저장 트리거 API (비동기 방식)
     * - 모든 병원의 병원코드(ykiho)를 기준으로 전문의 데이터 API 호출
     * - 각 병원 단위로 파싱 후 DB에 저장
     * - 실시간 진행상황은 로그 또는 /status API로 확인
     */
    @GetMapping(value = "/save", produces = MediaType.TEXT_PLAIN_VALUE)
    public String syncProDocData() {
        int total = proDocService.fetchParseAndSaveProDocs(); // 전체 병원 수 반환
        return String.format("전문의 정보 저장 시작됨! 전체 병원 수: %d개.\n(실시간 진행상황은 로그에서 확인 가능)\n", total);
    }

    /**
     * 전문의 정보 저장 진행상황 확인 API
     * - 병원 단위로 완료/실패 카운트 반환
     */
    @GetMapping(value = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public String getStatus() {
        int done = proDocService.getCompletedCount(); // 저장 완료된 병원 수
        int fail = proDocService.getFailedCount();    // 실패한 병원 수
        return String.format("현재 진행상황: 완료 %d건, 실패 %d건\n", done, fail);
    }
}
