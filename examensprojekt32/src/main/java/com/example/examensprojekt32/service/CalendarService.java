package com.example.examensprojekt32.service;

import com.example.examensprojekt32.model.WorkingDay;
import com.example.examensprojekt32.model.ScheduleException;
import com.example.examensprojekt32.model.TimeSlot;
import com.example.examensprojekt32.model.TimeSlotStatus;
import com.example.examensprojekt32.repository.WorkingDayRepository;
import com.example.examensprojekt32.repository.ScheduleExceptionRepository;
import com.example.examensprojekt32.repository.TimeSlotRepository;
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
    private final TimeSlotRepository timeSlotRepository;

    public CalendarService(
            WorkingDayRepository workingDayRepository,
            ScheduleExceptionRepository scheduleExceptionRepository,
            TimeSlotRepository timeSlotRepository) {

        this.workingDayRepository = workingDayRepository;
        this.scheduleExceptionRepository = scheduleExceptionRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    // Hent alle faste arbejdsdage
    public List<WorkingDay> getAllWorkingDays() {
        return workingDayRepository.findAll();
    }

    // Kontroller om en dato er en arbejdsdag
    public boolean isWorkingDay(LocalDate date) {
        return scheduleExceptionRepository.findByDate(date)
                .map(ScheduleException::isWorkingDay)
                .orElseGet(() -> workingDayRepository
                        .findByDayOfWeek(date.getDayOfWeek())
                        .map(WorkingDay::isActive)
                        .orElse(false));
    }

    // Opdater en fast arbejdsdag
    public WorkingDay updateWorkingDay(
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            boolean active) {

        validateWorkingHours(startTime, endTime);

        WorkingDay workingDay = workingDayRepository
                .findByDayOfWeek(dayOfWeek)
                .orElseThrow(() -> new IllegalArgumentException(
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

    // Registrer en fridag
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

    // Hent registrerede tidsintervaller på en dato
    public List<TimeSlot> getTimeSlots(LocalDate date) {
        return timeSlotRepository
                .findByDateOrderByStartTimeAsc(date);
    }

    // Bloker et tidsinterval
    public TimeSlot blockTimeSlot(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        validateDate(date);
        validateWorkingHours(startTime, endTime);

        if (!isWorkingDay(date)) {
            throw new IllegalArgumentException(
                    "Datoen er ikke en arbejdsdag.");
        }

        validateWithinWorkingHours(date, startTime, endTime);

        boolean overlapping = getTimeSlots(date)
                .stream()
                .filter(slot ->
                        slot.getStatus() == TimeSlotStatus.BLOCKED)
                .anyMatch(slot ->
                        startTime.isBefore(slot.getEndTime())
                                && endTime.isAfter(slot.getStartTime()));

        if (overlapping) {
            throw new IllegalArgumentException(
                    "Tidsintervallet overlapper en blokeret tid.");
        }

        TimeSlot timeSlot = new TimeSlot(
                date,
                startTime,
                endTime,
                TimeSlotStatus.BLOCKED
        );

        return timeSlotRepository.save(timeSlot);
    }

    // Frigiv et blokeret tidsinterval
    public void unblockTimeSlot(Long id) {

        TimeSlot timeSlot = timeSlotRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tidsintervallet findes ikke."));

        validateDate(timeSlot.getDate());

        if (timeSlot.getStatus() != TimeSlotStatus.BLOCKED) {
            throw new IllegalArgumentException(
                    "Tidsintervallet er ikke blokeret.");
        }

        timeSlotRepository.delete(timeSlot);
    }

    // Valider start- og sluttid
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

    // Valider dato
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

    // Kontroller om tiden ligger inden for arbejdstiden
    private void validateWithinWorkingHours(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        ScheduleException exception = scheduleExceptionRepository
                .findByDate(date)
                .orElse(null);

        LocalTime workingStart;
        LocalTime workingEnd;

        if (exception != null) {

            workingStart = exception.getStartTime();
            workingEnd = exception.getEndTime();

        } else {

            WorkingDay workingDay = workingDayRepository
                    .findByDayOfWeek(date.getDayOfWeek())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Arbejdsdagen findes ikke."));

            workingStart = workingDay.getStartTime();
            workingEnd = workingDay.getEndTime();
        }

        if (workingStart == null || workingEnd == null
                || startTime.isBefore(workingStart)
                || endTime.isAfter(workingEnd)) {

            throw new IllegalArgumentException(
                    "Tiden ligger uden for arbejdstiden.");
        }
    }
}