package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.ScheduleException;
import com.example.examensprojekt32.model.WorkingDay;
import com.example.examensprojekt32.repository.ScheduleExceptionRepository;
import com.example.examensprojekt32.repository.WorkingDayRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private WorkingDayRepository workingDayRepository;

    @Mock
    private ScheduleExceptionRepository scheduleExceptionRepository;

    @InjectMocks
    private CalendarService calendarService;

    private final LocalTime startTime = LocalTime.of(8, 0);
    private final LocalTime endTime = LocalTime.of(15, 0);

    @Test
    void canSeeNormalWorkingDay() {

        LocalDate date = LocalDate.of(2026, 11, 30);

        WorkingDay workingDay = new WorkingDay(
                DayOfWeek.MONDAY,
                startTime,
                endTime,
                true
        );

        when(scheduleExceptionRepository.findByDate(date))
                .thenReturn(Optional.empty());

        when(workingDayRepository.findByDayOfWeek(DayOfWeek.MONDAY))
                .thenReturn(Optional.of(workingDay));

        assertTrue(calendarService.isWorkingDay(date));
        assertEquals(startTime, calendarService.getStartTime(date));
        assertEquals(endTime, calendarService.getEndTime(date));
    }

    @Test
    void cannotBookOnClosedWorkingDay() {

        LocalDate date = LocalDate.of(2026, 12, 1);

        WorkingDay workingDay = new WorkingDay(
                DayOfWeek.TUESDAY,
                startTime,
                endTime,
                false
        );

        when(scheduleExceptionRepository.findByDate(date))
                .thenReturn(Optional.empty());

        when(workingDayRepository.findByDayOfWeek(DayOfWeek.TUESDAY))
                .thenReturn(Optional.of(workingDay));

        assertFalse(calendarService.isWorkingDay(date));
        assertNull(calendarService.getStartTime(date));
        assertNull(calendarService.getEndTime(date));
    }

    @Test
    void sicknessOverridesNormalWorkingDay() {

        LocalDate date = LocalDate.of(2026, 11, 30);

        ScheduleException exception = new ScheduleException();

        exception.setDate(date);
        exception.setWorkingDay(false);
        exception.setReason("Sygdom");

        when(scheduleExceptionRepository.findByDate(date))
                .thenReturn(Optional.of(exception));

        assertFalse(calendarService.isWorkingDay(date));
        assertNull(calendarService.getStartTime(date));
        assertNull(calendarService.getEndTime(date));

        verifyNoInteractions(workingDayRepository);
    }

    @Test
    void extraWorkingDayOverridesClosedDay() {

        LocalDate date = LocalDate.of(2026, 11, 28);

        ScheduleException exception = new ScheduleException();

        exception.setDate(date);
        exception.setWorkingDay(true);
        exception.setStartTime(startTime);
        exception.setEndTime(endTime);

        when(scheduleExceptionRepository.findByDate(date))
                .thenReturn(Optional.of(exception));

        assertTrue(calendarService.isWorkingDay(date));
        assertEquals(startTime, calendarService.getStartTime(date));
        assertEquals(endTime, calendarService.getEndTime(date));

        verifyNoInteractions(workingDayRepository);
    }

    @Test
    void canSeeChangedWorkingHours() {

        LocalDate date = LocalDate.of(2026, 11, 30);

        ScheduleException exception = new ScheduleException();

        exception.setDate(date);
        exception.setWorkingDay(true);
        exception.setStartTime(LocalTime.of(10, 0));
        exception.setEndTime(LocalTime.of(14, 0));

        when(scheduleExceptionRepository.findByDate(date))
                .thenReturn(Optional.of(exception));

        assertEquals(
                LocalTime.of(10, 0),
                calendarService.getStartTime(date)
        );

        assertEquals(
                LocalTime.of(14, 0),
                calendarService.getEndTime(date)
        );

        verifyNoInteractions(workingDayRepository);
    }

    @Test
    void cannotSaveInvalidWorkingHours() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calendarService.updateWorkingDay(
                        DayOfWeek.MONDAY,
                        LocalTime.of(15, 0),
                        LocalTime.of(8, 0),
                        true
                )
        );

        assertEquals(
                "Starttid skal være før sluttid.",
                exception.getMessage()
        );

        verifyNoInteractions(workingDayRepository);
    }

    @Test
    void cannotAddWorkingDayInThePast() {

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> calendarService.addExtraWorkingDay(
                        LocalDate.now().minusDays(1),
                        startTime,
                        endTime
                )
        );

        assertEquals(
                "Du kan ikke ændre en dato i fortiden.",
                exception.getMessage()
        );

        verifyNoInteractions(scheduleExceptionRepository);
    }

    @Test
    void canDeleteScheduleException() {

        ScheduleException exception = new ScheduleException();

        exception.setDate(LocalDate.now().plusDays(1));

        when(scheduleExceptionRepository.findById(1L))
                .thenReturn(Optional.of(exception));

        calendarService.deleteScheduleException(1L);

        verify(scheduleExceptionRepository).delete(exception);
    }
}