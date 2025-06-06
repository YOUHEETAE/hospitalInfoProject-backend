package com.hospital.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hospital.dto.api.HospitalDetailApiResponse;
import com.hospital.dto.api.HospitalDetailApiItem;
import com.hospital.entity.HospitalDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class HospitalDetailApiParser {

    private final ObjectMapper objectMapper;

    public HospitalDetailApiParser() {
        this.objectMapper = new ObjectMapper();
    }

    // JSON 파싱 중 오류 발생 시 예외를 호출자에게 던짐 -> 즉시 중단 가능
    public HospitalDetailApiResponse parseResponse(String json) throws Exception {
        return objectMapper.readValue(json, HospitalDetailApiResponse.class);
    }

    // 예외를 잡아서 로그 출력 후, 필요에 따라 RuntimeException으로 다시 던져서 호출자도 중단하도록 함
    public List<HospitalDetailApiItem> parseItems(String json) {
        try {
            HospitalDetailApiResponse response = parseResponse(json);
            if (response != null &&
                response.getResponse() != null &&
                response.getResponse().getBody() != null &&
                response.getResponse().getBody().getItems() != null) {
                return response.getResponse().getBody().getItems().getItem();
            }
        } catch (Exception e) {
            // 오류 내용 확인 가능하게 출력
            System.err.println("HospitalDetailApiParser parseItems 오류:");
            e.printStackTrace();

            // 필요시 여기서 바로 중단시키기 위해 RuntimeException으로 던질 수 있음
            throw new RuntimeException("HospitalDetailApiParser에서 JSON 파싱 오류 발생", e);
        }
        return Collections.emptyList();
    }

    /**
     * JSON 응답에서 아이템들을 파싱하고,
     * 각 아이템과 병원 코드를 받아 엔티티 리스트로 변환해서 반환
     */
    public List<HospitalDetail> parseToEntities(String json, String hospitalCode) {
        List<HospitalDetailApiItem> items = parseItems(json);
        List<HospitalDetail> entities = new ArrayList<>();

        for (HospitalDetailApiItem item : items) {
            HospitalDetail entity = convertDtoToEntity(item, hospitalCode);
            entities.add(entity);
        }
        return entities;
    }

    /**
     * DTO -> Entity 변환 메서드 (코드 재사용용)
     */
    private HospitalDetail convertDtoToEntity(HospitalDetailApiItem dto, String hospitalCode) {
        return HospitalDetail.builder()
                .hospitalCode(hospitalCode)
                .emyDayYn(dto.getEmyDayYn())
                .emyNightYn(dto.getEmyNgtYn())
                .parkQty(dto.getParkQty() != null ? Integer.valueOf(dto.getParkQty()) : null)
                .lunchWeek(dto.getLunchWeek())
                .rcvWeek(dto.getRcvWeek())
                .rcvSat(dto.getRcvSat())
                .trmtMonStart(dto.getTrmtMonStart())
                .trmtMonEnd(dto.getTrmtMonEnd())
                .trmtTueStart(dto.getTrmtTueStart())
                .trmtTueEnd(dto.getTrmtTueEnd())
                .trmtWedStart(dto.getTrmtWedStart())
                .trmtWedEnd(dto.getTrmtWedEnd())
                .trmtThurStart(dto.getTrmtThuStart())
                .trmtThurEnd(dto.getTrmtThuEnd())
                .trmtFriStart(dto.getTrmtFriStart())
                .trmtFriEnd(dto.getTrmtFriEnd())
                .build();
    }
}
