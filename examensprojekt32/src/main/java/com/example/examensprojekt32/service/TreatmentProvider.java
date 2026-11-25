package com.example.examensprojekt32.service;

import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TreatmentProvider {

    List<TreatmentData> getAllTreatments();

    List<TreatmentData> getSelectedTreatments(List<Long> ids);
}