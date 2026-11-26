package com.example.examensprojekt32.controller;

import com.example.examensprojekt32.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
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
}