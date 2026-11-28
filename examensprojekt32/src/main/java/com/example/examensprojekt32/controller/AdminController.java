package com.example.examensprojekt32.controller;

import com.example.examensprojekt32.model.Admin;
import com.example.examensprojekt32.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String showLogin() {
        return "admin-login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Admin admin = adminService.login(username, password);
            session.setAttribute("adminUsername", admin.getUsername());
            return "redirect:/admin";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "admin-login";
        }
    }

    @GetMapping
    public String showDashboard(HttpSession session, Model model) {
        model.addAttribute("adminUsername",
                session.getAttribute("adminUsername"));
        return "admin";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

}
