package com.hospital.domainLogic;

import com.hospital.entity.HospitalMain;
import com.hospital.entity.HospitalDetail;
import com.hospital.entity.MedicalSubject;
import com.hospital.entity.ProDoc;

import java.util.List;
import java.util.Objects; 


public class HospitalTagFilter {

   
    public static boolean matchesAllTags(HospitalMain hospital, List<String> tags) {
        // 태그 목록이 null이거나 비어있으면 필터링 없이 true 반환
        if (Objects.isNull(tags) || tags.isEmpty()) {
            return true;
        }

        // 각 태그에 대해 순회하며 조건을 확인
        for (String tag : tags) {
            switch (tag) {
                case "응급실":
                    // HospitalDetail이 없거나 응급실 진료가 불가능하면 false
                    if (Objects.isNull(hospital.getHospitalDetail()) || !hospital.getHospitalDetail().hasEmergencyService()) {
                        return false;
                    }
                    break;
                case "주차가능":
                    // HospitalDetail이 없거나 주차 공간이 없으면 false
                    if (Objects.isNull(hospital.getHospitalDetail()) || !hospital.getHospitalDetail().hasParkingSpace()) {
                        return false;
                    }
                    break;
                case "전문의":
                    // ProDoc 리스트가 없거나, 리스트 내에 전문의가 한 명도 없으면 false
                    if (Objects.isNull(hospital.getProDocs()) || hospital.getProDocs().isEmpty() ||
                        hospital.getProDocs().stream().noneMatch(ProDoc::hasSpecialist)) {
                        return false;
                    }
                    break;
                // 기타 진료과목 태그 처리 (ex: "내과", "외과", "치과" 등)
                default:
                    // MedicalSubject 리스트가 없거나, 해당 진료과목을 가진 MedicalSubject가 없으면 false
                    if (Objects.isNull(hospital.getMedicalSubjects()) || hospital.getMedicalSubjects().isEmpty() ||
                        hospital.getMedicalSubjects().stream().noneMatch(ms -> ms.getSubjectName().equals(tag))) {
                        return false;
                    }
                    break;
            }
        }
        return true; // 모든 태그 조건을 만족하면 true
    }
}