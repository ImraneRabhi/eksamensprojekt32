package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.Booking;
import com.example.examensprojekt32.model.BookingStatus;
import com.example.examensprojekt32.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AvailabilityService {

    private static final int SLOT_INTERVAL_MINUTES = 30;

    private static final List<BookingStatus> ACTIVE_STATUSES =
            List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED);

    private final CalendarService calendarService;
    private final BookingRepository bookingRepository;

    public AvailabilityService(CalendarService calendarService,
                               BookingRepository bookingRepository) {
        this.calendarService = calendarService;
        this.bookingRepository = bookingRepository;
    }

    public List<LocalTime> getAvailableTimes(LocalDate date, int durationMinutes) {
        return getAvailableTimes(date, durationMinutes, LocalDateTime.now());
    }

    List<LocalTime> getAvailableTimes(LocalDate date, int durationMinutes, LocalDateTime now) {
        validateDuration(durationMinutes);

        if (date == null) {
            throw new IllegalArgumentException("Du skal vælge en dato.");
        }
        if (date.isBefore(now.toLocalDate())) {
            throw new IllegalArgumentException("Du kan ikke vælge en dato i fortiden.");
        }

        List<Booking> bookings = findActiveBookings(
                date.atStartOfDay(),
                date.plusDays(1).atStartOfDay());

        return calculateTimes(date, durationMinutes, bookings, now);
    }

    public List<LocalDate> getAvailableDates(int daysAhead, int durationMinutes) {
        return getAvailableDates(daysAhead, durationMinutes, LocalDateTime.now());
    }

    List<LocalDate> getAvailableDates(int daysAhead, int durationMinutes, LocalDateTime now) {
        validateDuration(durationMinutes);

        LocalDate today = now.toLocalDate();

        List<Booking> bookings = findActiveBookings(
                today.atStartOfDay(),
                today.plusDays(daysAhead).atStartOfDay());

        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < daysAhead; i++) {
            LocalDate date = today.plusDays(i);
            if (!calculateTimes(date, durationMinutes, bookings, now).isEmpty()) {
                dates.add(date);
            }
        }
        return dates;
    }

    private List<LocalTime> calculateTimes(LocalDate date,
                                           int durationMinutes,
                                           List<Booking> bookings,
                                           LocalDateTime now) {

        LocalTime openingTime = calendarService.getStartTime(date);
        LocalTime closingTime = calendarService.getEndTime(date);

        if (openingTime == null || closingTime == null) {
            return List.of();
        }

        LocalDateTime candidateStart = date.atTime(openingTime);
        LocalDateTime closing = date.atTime(closingTime);
        List<LocalTime> times = new ArrayList<>();

        while (!candidateStart.plusMinutes(durationMinutes).isAfter(closing)) {
            LocalDateTime candidateEnd = candidateStart.plusMinutes(durationMinutes);

            boolean inFuture = candidateStart.isAfter(now);
            boolean free = !overlapsAnyBooking(candidateStart, candidateEnd, bookings);

            if (inFuture && free) {
                times.add(candidateStart.toLocalTime());
            }
            candidateStart = candidateStart.plusMinutes(SLOT_INTERVAL_MINUTES);
        }
        return times;
    }

    private boolean overlapsAnyBooking(LocalDateTime start,
                                       LocalDateTime end,
                                       List<Booking> bookings) {
        return bookings.stream().anyMatch(booking ->
                start.isBefore(booking.getEndTime())
                        && end.isAfter(booking.getStartTime()));
    }

    private List<Booking> findActiveBookings(LocalDateTime from, LocalDateTime to) {
        return bookingRepository.findByStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
                to, from, ACTIVE_STATUSES);
    }

    private void validateDuration(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Varigheden skal være større end 0.");
        }
    }
}