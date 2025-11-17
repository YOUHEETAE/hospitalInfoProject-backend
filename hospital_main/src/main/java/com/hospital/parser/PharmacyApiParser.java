package com.hospital.parser;



import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hospital.dto.PharmacyApiItem;
import com.hospital.dto.PharmacyApiResponse;
import com.hospital.entity.Pharmacy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PharmacyApiParser {

    public List<Pharmacy> parsePharmacies(PharmacyApiResponse apiResponseDto) {
        log.debug("약국 데이터 파싱 시작");
        
        // 1. 응답 검증
        validateApiResponse(apiResponseDto);
        
        // 2. 아이템 추출 및 변환
        List<Pharmacy> pharmacies = extractAndConvertItems(apiResponseDto);
        
        log.debug("약국 데이터 파싱 완료: {}건", pharmacies.size());
        return pharmacies;
    }

    /**
     * API 응답 검증
     */
    private void validateApiResponse(PharmacyApiResponse response) {
        if (response == null || response.getHeader() == null) {
            throw new RuntimeException("API 응답이 올바르지 않습니다");
        }
        
        String resultCode = response.getHeader().getResultCode();
        String resultMsg = response.getHeader().getResultMsg();
        
        if (!"00".equals(resultCode)) {
            throw new RuntimeException("API 응답 오류: " + resultCode + " - " + resultMsg);
        }
    }

    /**
     * 아이템 추출 및 변환 
     */
    private List<Pharmacy> extractAndConvertItems(PharmacyApiResponse response) {
        return Optional.ofNullable(response)
                .map(PharmacyApiResponse::getBody)
                .map(PharmacyApiResponse.Body::getItems)
                .orElseGet(ArrayList::new)
                .stream()
                .map(this::convertToPharmacy)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 단일 아이템을 Pharmacy 엔티티로 변환
     */
    private Pharmacy convertToPharmacy(PharmacyApiItem itemDto) {
        if (itemDto == null || itemDto.getHpid() == null || itemDto.getHpid().trim().isEmpty()) {
            log.warn("유효하지 않은 약국 데이터: {}", itemDto);
            return null;
        }

        return Pharmacy.builder()
                // 기본 정보
                .name(itemDto.getDutyName())
                .address(itemDto.getDutyAddr())
                .phone(itemDto.getDutyTel1())
                .fax(itemDto.getDutyFax())
                .etc(itemDto.getDutyEtc())
                .mapInfo(itemDto.getDutyMapimg())
                .postCode1(itemDto.getPostCdn1())
                .postCode2(itemDto.getPostCdn2())
                .latitude(itemDto.getWgs84Lat())
                .longitude(itemDto.getWgs84Lon())
                .ykiho(itemDto.getHpid())
                // 운영 시간 (월요일)
                .mondayOpen(itemDto.getDutyTime1s())
                .mondayClose(itemDto.getDutyTime1c())
                // 운영 시간 (화요일)
                .tuesdayOpen(itemDto.getDutyTime2s())
                .tuesdayClose(itemDto.getDutyTime2c())
                // 운영 시간 (수요일)
                .wednesdayOpen(itemDto.getDutyTime3s())
                .wednesdayClose(itemDto.getDutyTime3c())
                // 운영 시간 (목요일)
                .thursdayOpen(itemDto.getDutyTime4s())
                .thursdayClose(itemDto.getDutyTime4c())
                // 운영 시간 (금요일)
                .fridayOpen(itemDto.getDutyTime5s())
                .fridayClose(itemDto.getDutyTime5c())
                // 운영 시간 (토요일)
                .saturdayOpen(itemDto.getDutyTime6s())
                .saturdayClose(itemDto.getDutyTime6c())
                // 운영 시간 (일요일)
                .sundayOpen(itemDto.getDutyTime7s())
                .sundayClose(itemDto.getDutyTime7c())
                // 운영 시간 (공휴일)
                .holidayOpen(itemDto.getDutyTime8s())
                .holidayClose(itemDto.getDutyTime8c())
                .build();
    }

    /**
     * 기존 메서드 - 하위 호환성을 위해 유지
     */
    public List<Pharmacy> parseToEntities(List<PharmacyApiItem> apiItems) {
        if (apiItems == null || apiItems.isEmpty()) {
            return List.of();
        }

        return apiItems.stream()
                .map(this::convertToPharmacy)
                .filter(pharmacy -> pharmacy != null && pharmacy.isValid())
                .collect(Collectors.toList());
    }
}