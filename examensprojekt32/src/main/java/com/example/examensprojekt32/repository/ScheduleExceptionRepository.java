package com.example.examensprojekt32.repository;

import com.example.examensprojekt32.model.ScheduleException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleExceptionRepository
        extends JpaRepository<ScheduleException, Long> {

    Optional<ScheduleException> findByDate(LocalDate date);
}