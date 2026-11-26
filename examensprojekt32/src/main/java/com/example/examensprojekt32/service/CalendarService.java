package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.WorkingDay;
import com.example.examensprojekt32.model.ScheduleException;
import com.example.examensprojekt32.repository.WorkingDayRepository;
import com.example.examensprojekt32.repository.ScheduleExceptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class CalendarService {

    private final WorkingDayRepository workingDayRepository;
    private final ScheduleExceptionRepository scheduleExceptionRepository;

    public CalendarService(
            WorkingDayRepository workingDayRepository,
            ScheduleExceptionRepository scheduleExceptionRepository) {

        this.workingDayRepository = workingDayRepository;
        this.scheduleExceptionRepository = scheduleExceptionRepository;
    }

    // Hent alle faste arbejdsdage

    public List<WorkingDay> getAllWorkingDays() {
        return workingDayRepository.findAll();
    }

    // Hent alle registrerede undtagelser

    public List<ScheduleException> getAllScheduleExceptions() {
        return scheduleExceptionRepository.findAll();
    }

    // Kontrollér om en bestemt dato er en arbejdsdag

    public boolean isWorkingDay(LocalDate date) {

        return scheduleExceptionRepository.findByDate(date)
                .map(ScheduleException::isWorkingDay)
                .orElseGet(() ->
                        workingDayRepository.findByDayOfWeek(date.getDayOfWeek())
                                .map(WorkingDay::isActive)
                                .orElse(false));
    }

    // Hent gældende starttid for en bestemt dato (US03)

    public LocalTime getStartTime(LocalDate date) {

        ScheduleException exception =
                scheduleExceptionRepository.findByDate(date).orElse(null);

        if (exception != null) {
            return exception.isWorkingDay()
                    ? exception.getStartTime()
                    : null;
        }

        return workingDayRepository.findByDayOfWeek(date.getDayOfWeek())
                .filter(WorkingDay::isActive)
                .map(WorkingDay::getStartTime)
                .orElse(null);
    }

    // Hent gældende sluttid for en bestemt dato (US03)

    public LocalTime getEndTime(LocalDate date) {

        ScheduleException exception =
                scheduleExceptionRepository.findByDate(date).orElse(null);

        if (exception != null) {
            return exception.isWorkingDay()
                    ? exception.getEndTime()
                    : null;
        }

        return workingDayRepository.findByDayOfWeek(date.getDayOfWeek())
                .filter(WorkingDay::isActive)
                .map(WorkingDay::getEndTime)
                .orElse(null);
    }

    // Opdatér en fast arbejdsdag

    public WorkingDay updateWorkingDay(
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            boolean active) {

        validateWorkingHours(startTime, endTime);

        WorkingDay workingDay = workingDayRepository
                .findByDayOfWeek(dayOfWeek)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Arbejdsdagen findes ikke."));

        workingDay.setStartTime(startTime);
        workingDay.setEndTime(endTime);
        workingDay.setActive(active);

        return workingDayRepository.save(workingDay);
    }

    // Tilføj en ekstra arbejdsdag

    public ScheduleException addExtraWorkingDay(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        validateDate(date);
        validateWorkingHours(startTime, endTime);

        ScheduleException exception = scheduleExceptionRepository
                .findByDate(date)
                .orElseGet(ScheduleException::new);

        exception.setDate(date);
        exception.setStartTime(startTime);
        exception.setEndTime(endTime);
        exception.setWorkingDay(true);
        exception.setReason("Ekstra arbejdsdag");

        return scheduleExceptionRepository.save(exception);
    }

    // Registrér ferie, sygdom eller anden fridag

    public ScheduleException removeWorkingDay(
            LocalDate date,
            String reason) {

        validateDate(date);

        ScheduleException exception = scheduleExceptionRepository
                .findByDate(date)
                .orElseGet(ScheduleException::new);

        exception.setDate(date);
        exception.setStartTime(null);
        exception.setEndTime(null);
        exception.setWorkingDay(false);
        exception.setReason(reason);

        return scheduleExceptionRepository.save(exception);
    }

    // Ændr arbejdstiden på en bestemt dato

    public ScheduleException updateWorkingHours(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        validateDate(date);
        validateWorkingHours(startTime, endTime);

        if (!isWorkingDay(date)) {
            throw new IllegalArgumentException(
                    "Datoen er ikke en arbejdsdag.");
        }

        ScheduleException exception = scheduleExceptionRepository
                .findByDate(date)
                .orElseGet(ScheduleException::new);

        exception.setDate(date);
        exception.setStartTime(startTime);
        exception.setEndTime(endTime);
        exception.setWorkingDay(true);
        exception.setReason("Ændret arbejdstid");

        return scheduleExceptionRepository.save(exception);
    }

    // Fjern en registreret undtagelse

    public void deleteScheduleException(Long id) {

        ScheduleException exception = scheduleExceptionRepository
                .findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Undtagelsen findes ikke."));

        validateDate(exception.getDate());

        scheduleExceptionRepository.delete(exception);
    }

    // Validering af arbejdstider

    private void validateWorkingHours(
            LocalTime startTime,
            LocalTime endTime) {

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException(
                    "Starttid og sluttid skal angives.");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException(
                    "Starttid skal være før sluttid.");
        }
    }

    // Validering af dato

    private void validateDate(LocalDate date) {

        if (date == null) {
            throw new IllegalArgumentException(
                    "Du skal angive en dato.");
        }

        if (date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Du kan ikke ændre en dato i fortiden.");
        }
    }
}