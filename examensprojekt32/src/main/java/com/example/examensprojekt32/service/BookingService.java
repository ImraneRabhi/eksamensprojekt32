package com.example.examensprojekt32.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookingService {

    private final TreatmentProvider treatmentProvider;

    public BookingService(TreatmentProvider treatmentProvider) {
        this.treatmentProvider = treatmentProvider;
    }

    public List<TreatmentData> getAllTreatments() {
        return List.copyOf(treatmentProvider.getAllTreatments());
    }

    public List<TreatmentData> getSelectedTreatments(List<Long> ids) {

        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(
                    "Du skal vælge mindst én behandling.");
        }

        List<TreatmentData> treatments =
                List.copyOf(treatmentProvider.getSelectedTreatments(ids));

        if (treatments.size() != ids.size()) {
            throw new IllegalArgumentException(
                    "Ugyldigt behandlingsvalg.");
        }

        return treatments;
    }

    public BigDecimal calculateTotalPrice(List<Long> ids) {
        return getSelectedTreatments(ids).stream()
                .map(TreatmentData::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int calculateDuration(List<Long> ids) {
        return getSelectedTreatments(ids).stream()
                .mapToInt(TreatmentData::getDurationMinutes)
                .sum();
    }
}