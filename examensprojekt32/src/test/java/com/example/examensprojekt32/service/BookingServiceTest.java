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
                .thenReturn(new BigDecimal("300.00"));

        when(treatmentProvider.getSelectedTreatments(List.of(1L, 2L)))
                .thenReturn(List.of(hijama, massage));

        BigDecimal result =
                bookingService.calculateTotalPrice(List.of(1L, 2L));

        assertEquals(new BigDecimal("700.00"), result);
    }

    @Test
    void calculateTotalDurationForBothTreatments() {

        TreatmentData hijama = mock(TreatmentData.class);
        TreatmentData massage = mock(TreatmentData.class);

        when(hijama.getDurationMinutes()).thenReturn(60);
        when(massage.getDurationMinutes()).thenReturn(30);

        when(treatmentProvider.getSelectedTreatments(List.of(1L, 2L)))
                .thenReturn(List.of(hijama, massage));

        int result = bookingService.calculateDuration(List.of(1L, 2L));

        assertEquals(90, result);
    }

    @Test
    void canSelectMassage() {

        TreatmentData massage = mock(TreatmentData.class);

        when(massage.getPrice())
                .thenReturn(new BigDecimal("300.00"));

        when(massage.getDurationMinutes()).thenReturn(30);

        when(treatmentProvider.getSelectedTreatments(List.of(2L)))
                .thenReturn(List.of(massage));

        assertEquals(List.of(massage),
                bookingService.getSelectedTreatments(List.of(2L)));

        assertEquals(new BigDecimal("300.00"),
                bookingService.calculateTotalPrice(List.of(2L)));

        assertEquals(30,
                bookingService.calculateDuration(List.of(2L)));
    }

    @Test
    void cannotSelectEmptyTreatments() {

        assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getSelectedTreatments(List.of());
        });

        verifyNoInteractions(treatmentProvider);
    }
}
