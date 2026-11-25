package com.example.examensprojekt32.controller;

import com.example.examensprojekt32.service.BookingService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public  BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

}
