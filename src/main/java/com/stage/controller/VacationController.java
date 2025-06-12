package com.stage.controller;

import com.stage.dto.VacationDTO;
import com.stage.persistans.Vacation;
import com.stage.repositories.VacationRepository;
import com.stage.services.VacationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/publicHolidays")
@RequiredArgsConstructor
public class VacationController {
    private final VacationService publicHolidaysService;
private final VacationRepository publicHolidaysRepository;
    @GetMapping
    public ResponseEntity<List<Vacation>>  getAllPublicHolidays() {
        List<Vacation> publicHolidays = publicHolidaysService.findAll();
        return new ResponseEntity<>(publicHolidays, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vacation> getPublicHolidaysById(@PathVariable Long id) {
       Optional<Vacation>  publicHolidays = publicHolidaysService.findById(id);
       if (publicHolidays.isPresent()) {
           return new ResponseEntity<>(publicHolidays.get(), HttpStatus.OK);
       }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }



    @PostMapping
    public ResponseEntity<?> save(@RequestBody VacationDTO holidayDto) {
        try {
            // Validation des données d'entrée
            if (holidayDto.getStartDate() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "La date de début est requise."));
            }

            // Conversion timestamp -> LocalDateTime
            Long startTimestampMillis = holidayDto.getStartDate() * 1000;
            LocalDate startDate = Instant.ofEpochMilli(startTimestampMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            LocalDateTime startDateTime = startDate.atStartOfDay();

            // Vérification de doublon
            Optional<Vacation> existingHoliday = publicHolidaysRepository.findByStartDate(startDateTime);
            if (existingHoliday.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Un jour férié existe déjà pour cette date: " + startDate));
            }

            // Création et sauvegarde
            Vacation holiday = new Vacation(
                    holidayDto.getId(),
                    holidayDto.getName(),
                    startDateTime,
                    holidayDto.getNbdays()
            );

            Vacation savedHoliday = publicHolidaysService.save(holiday);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedHoliday);

        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Erreur lors de l'ajout du jour férié: " + ex.getMessage()));
        }
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody VacationDTO holoholiday) {
        Optional<Vacation> existing = publicHolidaysService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Conversion timestamp en LocalDateTime
        Long startTimestampMillis = holoholiday.getStartDate() * 1000;
        LocalDateTime start = Instant.ofEpochMilli(startTimestampMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        // Vérifier s'il existe un autre jour férié avec la même date (différent de celui qu’on modifie)
        Optional<Vacation> holidayWithSameDate = publicHolidaysRepository.findByStartDate(start);
        if (holidayWithSameDate.isPresent() && !holidayWithSameDate.get().getId().equals(id)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Un autre jour férié existe déjà à cette date.");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }

        Vacation toUpdate = new Vacation(
                id,
                holoholiday.getName(),
                start,
                holoholiday.getNbdays()
        );

        Vacation updated = publicHolidaysService.update(toUpdate, id);

        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
