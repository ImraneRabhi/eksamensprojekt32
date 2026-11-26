package com.example.examensprojekt32.controller;

import com.example.examensprojekt32.service.CalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/admin/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping
    public String showCalendar(Model model) {

        model.addAttribute("workingDays",
                calendarService.getAllWorkingDays());

        model.addAttribute("scheduleExceptions",
                calendarService.getAllScheduleExceptions());

        return "calendar";
    }

    @PostMapping("/working-day/update")
    public String updateWorkingDay(
            @RequestParam DayOfWeek dayOfWeek,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam(defaultValue = "false") boolean active,
            RedirectAttributes redirectAttributes) {

        try {
            calendarService.updateWorkingDay(
                    dayOfWeek, startTime, endTime, active);

            redirectAttributes.addFlashAttribute(
                    "success", "Arbejdsdagen er opdateret.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/calendar";
    }

    @PostMapping("/extra-day")
    public String addExtraWorkingDay(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            RedirectAttributes redirectAttributes) {

        try {
            calendarService.addExtraWorkingDay(
                    date, startTime, endTime);

            redirectAttributes.addFlashAttribute(
                    "success", "Ekstra arbejdsdag tilføjet.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/calendar";
    }

    @PostMapping("/day-off")
    public String removeWorkingDay(
            @RequestParam LocalDate date,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {

        try {
            calendarService.removeWorkingDay(date, reason);

            redirectAttributes.addFlashAttribute(
                    "success", "Fridagen er registreret.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/calendar";
    }

    @PostMapping("/hours/update")
    public String updateWorkingHours(
            @RequestParam LocalDate date,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            RedirectAttributes redirectAttributes) {

        try {
            calendarService.updateWorkingHours(
                    date, startTime, endTime);

            redirectAttributes.addFlashAttribute(
                    "success", "Arbejdstiden er opdateret.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/calendar";
    }

    @PostMapping("/exception/delete")
    public String deleteScheduleException(
            @RequestParam Long id,
            RedirectAttributes redirectAttributes) {

        try {
            calendarService.deleteScheduleException(id);

            redirectAttributes.addFlashAttribute(
                    "success", "Undtagelsen er fjernet.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());
        }

        return "redirect:/admin/calendar";
    }
}