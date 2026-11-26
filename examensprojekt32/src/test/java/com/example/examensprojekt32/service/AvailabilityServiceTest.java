package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.Booking;
import com.example.examensprojekt32.model.BookingStatus;
import com.example.examensprojekt32.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {

    @Mock
    private CalendarService calendarService;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private AvailabilityService availabilityService;

    private final LocalDate date = LocalDate.of(2026, 12, 1);
    private final LocalDateTime earlyMorning = date.atTime(8, 0);

    @BeforeEach
    void noBookingsByDefault() {
        lenient().when(bookingRepository.findByStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                any(), any(), any())).thenReturn(List.of());
    }

    private void openFromTenToTwelve() {
        when(calendarService.getStartTime(date)).thenReturn(LocalTime.of(10, 0));
        when(calendarService.getEndTime(date)).thenReturn(LocalTime.of(12, 0));
    }

    @Test
    void closedDayHasNoAvailableTimes() {
        when(calendarService.getStartTime(date)).thenReturn(null);
        when(calendarService.getEndTime(date)).thenReturn(null);

        List<LocalTime> times = availabilityService.getAvailableTimes(date, 30, earlyMorning);

        assertTrue(times.isEmpty());
    }

    @Test
    void dayWithoutBookingsHasAllTimes() {
        openFromTenToTwelve();

        List<LocalTime> times = availabilityService.getAvailableTimes(date, 30, earlyMorning);

        assertEquals(List.of(
                LocalTime.of(10, 0), LocalTime.of(10, 30),
                LocalTime.of(11, 0), LocalTime.of(11, 30)), times);
    }

    @Test
    void bookedTimeIsNotAvailable() {
        openFromTenToTwelve();
        Booking booking = new Booking(date.atTime(10, 30), date.atTime(11, 0), BookingStatus.CONFIRMED);
        when(bookingRepository.findByStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                any(), any(), any())).thenReturn(List.of(booking));

        List<LocalTime> times = availabilityService.getAvailableTimes(date, 30, earlyMorning);

        assertEquals(List.of(
                LocalTime.of(10, 0), LocalTime.of(11, 0), LocalTime.of(11, 30)), times);
    }

    @Test
    void longTreatmentMustEndBeforeClosing() {
        openFromTenToTwelve();

        List<LocalTime> times = availabilityService.getAvailableTimes(date, 60, earlyMorning);

        assertEquals(List.of(
                LocalTime.of(10, 0), LocalTime.of(10, 30), LocalTime.of(11, 0)), times);
    }

    @Test
    void passedTimesTodayAreNotAvailable() {
        openFromTenToTwelve();
        LocalDateTime quarterPastTen = date.atTime(10, 15);

        List<LocalTime> times = availabilityService.getAvailableTimes(date, 30, quarterPastTen);

        assertEquals(List.of(
                LocalTime.of(10, 30), LocalTime.of(11, 0), LocalTime.of(11, 30)), times);
    }

    @Test
    void onlyActiveBookingsAreConsidered() {
        openFromTenToTwelve();

        availabilityService.getAvailableTimes(date, 30, earlyMorning);

        verify(bookingRepository).findByStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                any(), any(), eq(List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)));
    }

    @Test
    void dateInThePastIsRejected() {
        LocalDate yesterday = date.minusDays(1);

        assertThrows(IllegalArgumentException.class,
                () -> availabilityService.getAvailableTimes(yesterday, 30, earlyMorning));
    }

    @Test
    void closedDayIsNotAnAvailableDate() {
        LocalDate tomorrow = date.plusDays(1);
        when(calendarService.getStartTime(date)).thenReturn(null);
        when(calendarService.getEndTime(date)).thenReturn(null);
        when(calendarService.getStartTime(tomorrow)).thenReturn(LocalTime.of(10, 0));
        when(calendarService.getEndTime(tomorrow)).thenReturn(LocalTime.of(12, 0));

        List<LocalDate> dates = availabilityService.getAvailableDates(2, 30, earlyMorning);

        assertEquals(List.of(tomorrow), dates);
    }
}