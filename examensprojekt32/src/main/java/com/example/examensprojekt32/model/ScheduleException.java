package com.example.examensprojekt32.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_exception")
public class ScheduleException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    @Column(nullable = false)
    private boolean workingDay;

    private String reason;

    public ScheduleException() {
    }

    public ScheduleException(LocalDate date,
                             LocalTime startTime,
                             LocalTime endTime,
                             boolean workingDay,
                             String reason) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workingDay = workingDay;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isWorkingDay() {
        return workingDay;
    }

    public void setWorkingDay(boolean workingDay) {
        this.workingDay = workingDay;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}