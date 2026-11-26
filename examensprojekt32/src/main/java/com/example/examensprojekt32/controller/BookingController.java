package com.example.examensprojekt32.controller;

import com.example.examensprojekt32.dto.CalendarDay;
import com.example.examensprojekt32.service.AvailabilityService;
import com.example.examensprojekt32.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private static final int DAYS_AHEAD = 30;

    private final BookingService bookingService;
    private final AvailabilityService availabilityService;

    public BookingController(BookingService bookingService,
                             AvailabilityService availabilityService) {
        this.bookingService = bookingService;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public String showBooking(Model model) {

        model.addAttribute("treatments",
                bookingService.getAllTreatments());

        return "booking";
    }

    @PostMapping
    public String selectTreatments(
            @RequestParam(name = "treatmentIds", required = false)
            List<Long> treatmentIds,
            Model model,
            HttpSession session) {

        model.addAttribute("treatments",
                bookingService.getAllTreatments());

        session.removeAttribute("selectedTreatmentIds");

        try {
            model.addAttribute("selectedTreatments",
                    bookingService.getSelectedTreatments(treatmentIds));

            model.addAttribute("totalPrice",
                    bookingService.calculateTotalPrice(treatmentIds));

            model.addAttribute("totalDuration",
                    bookingService.calculateDuration(treatmentIds));

            session.setAttribute("selectedTreatmentIds",
                    List.copyOf(treatmentIds));

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "booking";
    }

    @GetMapping("/times")
    public String showAvailableTimes(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            HttpSession session,
            Model model) {

        @SuppressWarnings("unchecked")
        List<Long> treatmentIds =
                (List<Long>) session.getAttribute("selectedTreatmentIds");

        if (treatmentIds == null || treatmentIds.isEmpty()) {
            return "redirect:/booking";
        }

        int duration = bookingService.calculateDuration(treatmentIds);

        List<LocalDate> availableDates =
                availabilityService.getAvailableDates(DAYS_AHEAD, duration);

        model.addAttribute("totalDuration", duration);
        model.addAttribute("availableDates", availableDates);
        model.addAttribute("calendarWeeks", buildCalendarWeeks(availableDates, date));

        if (date != null) {
            model.addAttribute("selectedDate", date);
            try {
                model.addAttribute("availableTimes",
                        availabilityService.getAvailableTimes(date, duration));
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        return "times";
    }

    private List<List<CalendarDay>> buildCalendarWeeks(List<LocalDate> availableDates,
                                                       LocalDate selectedDate) {

        Set<LocalDate> available = new HashSet<>(availableDates);

        LocalDate today = LocalDate.now();
        LocalDate lastDay = today.plusDays(DAYS_AHEAD - 1);

        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end = lastDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<List<CalendarDay>> weeks = new ArrayList<>();
        List<CalendarDay> week = new ArrayList<>();

        for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
            week.add(new CalendarDay(day, available.contains(day), day.equals(selectedDate)));

            if (week.size() == 7) {
                weeks.add(week);
                week = new ArrayList<>();
            }
        }
        return weeks;
    }
}