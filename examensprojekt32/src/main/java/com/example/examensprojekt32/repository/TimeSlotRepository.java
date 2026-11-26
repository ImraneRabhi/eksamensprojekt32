package com.example.examensprojekt32.repository;

import com.example.examensprojekt32.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByDateOrderByStartTimeAsc(LocalDate date);
}