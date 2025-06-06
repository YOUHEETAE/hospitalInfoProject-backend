package com.hospital.service;

import com.hospital.repository.HospitalMainRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HospitalCodeFetcherImpl implements HospitalCodeFetcher {

    private final HospitalMainRepository hospitalMainRepository;

    public HospitalCodeFetcherImpl(HospitalMainRepository hospitalMainRepository) {
        this.hospitalMainRepository = hospitalMainRepository;
    }

    @Override
    public List<String> getAllHospitalCodes() {
        return hospitalMainRepository.findAllHospitalCodes();
        
        
    }
}
