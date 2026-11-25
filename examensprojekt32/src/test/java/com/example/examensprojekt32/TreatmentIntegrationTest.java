package com.example.examensprojekt32;

import com.example.examensprojekt32.model.Treatment;
import com.example.examensprojekt32.repository.TreatmentRepository;
import com.example.examensprojekt32.service.TreatmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest
@ActiveProfiles("test")
class TreatmentIntegrationTest {

        @Autowired
        private TreatmentRepository treatmentRepository;

        @Autowired
        private TreatmentService treatmentService;

        @Test
        void shouldSaveAndRetrieveHijamaAndMassageWithAllDetails() {
            // Arrange: Opret de behandlinger, testen skal bruge.
            Treatment hijama = new Treatment(
                    "Hijama",
                    "Behandling med hijama",
                    new BigDecimal("699.00"),
                    60
            );

            Treatment massage = new Treatment(
                    "Massage",
                    "Afslappende massage",
                    new BigDecimal("499.00"),
                    45
            );

            treatmentRepository.saveAndFlush(hijama);
            treatmentRepository.saveAndFlush(massage);

            // Act: Hent behandlingerne gennem service-klassen.
            List<Treatment> treatments = treatmentService.getAllTreatments();

            // Assert: Find begge behandlinger og kontrollér deres oplysninger.
            Treatment savedHijama = treatments.stream()
                    .filter(treatment -> treatment.getId().equals(hijama.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Hijama blev ikke fundet"));

            Treatment savedMassage = treatments.stream()
                    .filter(treatment -> treatment.getId().equals(massage.getId()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Massage blev ikke fundet"));

            assertAll("Hijama",
                    () -> assertEquals("Hijama", savedHijama.getName()),
                    () -> assertEquals("Behandling med hijama", savedHijama.getDescription()),
                    () -> assertEquals(0, new BigDecimal("699.00").compareTo(savedHijama.getPrice())),
                    () -> assertEquals(60, savedHijama.getDurationMinutes())
            );

            assertAll("Massage",
                    () -> assertEquals("Massage", savedMassage.getName()),
                    () -> assertEquals("Afslappende massage", savedMassage.getDescription()),
                    () -> assertEquals(0, new BigDecimal("499.00").compareTo(savedMassage.getPrice())),
                    () -> assertEquals(45, savedMassage.getDurationMinutes())
            );
        }
    }
