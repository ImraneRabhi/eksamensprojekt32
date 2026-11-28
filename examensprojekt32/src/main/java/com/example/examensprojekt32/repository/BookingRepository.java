package com.example.examensprojekt32.repository;

import com.example.examensprojekt32.model.Booking;
import com.example.examensprojekt32.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByStartTimeLessThanAndEndTimeGreaterThanAndStatusIn(
            LocalDateTime periodEnd,
            LocalDateTime periodStart,
            Collection<BookingStatus> statuses
    );
}