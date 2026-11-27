package com.example.examensprojekt32.controller;
import org.springframework.ui.Model;
import com.example.examensprojekt32.service.TreatmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final TreatmentService treatmentService;

    public HomeController(TreatmentService treatmentService) {
        this.treatmentService = treatmentService;
    }



    @GetMapping("/")
    public String showHome(Model model) {
        model.addAttribute("treatments", treatmentService.getAllTreatments());
        return "home";
}
}
