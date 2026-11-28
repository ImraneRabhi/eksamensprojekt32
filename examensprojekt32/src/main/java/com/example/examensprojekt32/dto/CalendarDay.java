package com.example.examensprojekt32.dto;

import java.time.LocalDate;

public record CalendarDay(LocalDate date, boolean available, boolean selected) {
}