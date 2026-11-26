package com.example.examensprojekt32.config;

import com.example.examensprojekt32.model.WorkingDay;
import com.example.examensprojekt32.repository.WorkingDayRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final WorkingDayRepository workingDayRepository;

    public DataInitializer(WorkingDayRepository workingDayRepository) {
        this.workingDayRepository = workingDayRepository;
    }

    @Override
    public void run(String... args) {

        for (DayOfWeek day : DayOfWeek.values()) {

            if (workingDayRepository.findByDayOfWeek(day).isEmpty()) {

                boolean active = day == DayOfWeek.MONDAY
                        || day == DayOfWeek.WEDNESDAY
                        || day == DayOfWeek.FRIDAY;

                WorkingDay workingDay = new WorkingDay(
                        day,
                        LocalTime.of(8, 0),
                        LocalTime.of(15, 0),
                        active
                );

                workingDayRepository.save(workingDay);
            }
        }
    }
}