package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.Treatment;
import com.example.examensprojekt32.repository.TreatmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreatmentService implements TreatmentProvider {

    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    @Override
    public List<Treatment> getAllTreatments() {
        return treatmentRepository.findAll();
    }

    @Override
    public List<Treatment> getSelectedTreatments(List<Long> ids) {
        return treatmentRepository.findAllById(ids);
    }
}