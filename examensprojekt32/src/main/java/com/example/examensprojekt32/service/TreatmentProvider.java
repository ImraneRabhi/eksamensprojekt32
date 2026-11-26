package com.example.examensprojekt32.service;

import java.util.List;

public interface TreatmentProvider {

    List<? extends TreatmentData> getAllTreatments();

    List<? extends TreatmentData> getSelectedTreatments(List<Long> ids);
}