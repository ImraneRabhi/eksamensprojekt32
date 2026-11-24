package com.example.examensprojekt32.service;

import com.example.examensprojekt32.repository.TreatmentRepository;
import org.springframework.stereotype.Service;
import com.example.examensprojekt32.model.Treatment;
import java.util.List;

@Service
public class TreatmentService {
    private final TreatmentRepository treatmentRepository;

    public TreatmentService(TreatmentRepository treatmentRepository) {
        this.treatmentRepository = treatmentRepository;
    }

    public List<Treatment> getAllTreatments() {
        return treatmentRepository.findAll();
    }


}
