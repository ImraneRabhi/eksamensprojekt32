package com.example.examensprojekt32.service;

import java.math.BigDecimal;

public interface TreatmentData {

    Long getId();

    String getName();

    BigDecimal getPrice();

    int getDurationMinutes();
}