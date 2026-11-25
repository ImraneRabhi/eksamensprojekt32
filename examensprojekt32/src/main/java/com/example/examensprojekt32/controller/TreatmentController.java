package com.example.examensprojekt32.controller;

import com.example.examensprojekt32.service.TreatmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TreatmentController {
    private final TreatmentService treatmentService;

    public TreatmentController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }

    @GetMapping("/treatments")
    public String showTreatments(Model model) {
        model.addAttribute("treatments", treatmentService.getAllTreatments());
        return "treatments";
    }
}
