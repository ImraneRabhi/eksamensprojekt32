package com.example.examensprojekt32.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookingServiceTest {

    private TreatmentProvider treatmentProvider;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        treatmentProvider = mock(TreatmentProvider.class);
        bookingService = new BookingService(treatmentProvider);
    }

    @Test
    void calculateTotalPriceForHijama() {
        TreatmentData hijama = mock(TreatmentData.class);

        when(hijama.getPrice())
                .thenReturn(new BigDecimal("400.00"));

        when(treatmentProvider.getSelectedTreatments(List.of(1L)))
                .thenReturn(List.of(hijama));

        BigDecimal result = bookingService.calculateTotalPrice(List.of(1L));

        assertEquals(new BigDecimal("400.00"), result);
    }

    @Test
    void calculateTotalPriceForBothTreatments() {

        TreatmentData hijama = mock(TreatmentData.class);
        TreatmentData massage = mock(TreatmentData.class);

        when(hijama.getPrice())
                .thenReturn(new BigDecimal("400.00"));

        when(massage.getPrice())
                .thenReturn(new BigDecimal("400.00"));

        when(treatmentProvider.getSelectedTreatments(List.of(1L, 2L)))
                .thenReturn(List.of(hijama, massage));

        BigDecimal result =
                bookingService.calculateTotalPrice(List.of(1L, 2L));

        assertEquals(new BigDecimal("700.00"), result);
    }
}
