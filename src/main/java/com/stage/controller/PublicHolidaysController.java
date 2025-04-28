package com.stage.controller;

import com.stage.dto.HolidaysDTO;
import com.stage.persistans.PublicHolidays;
import com.stage.services.PublicHolidaysService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/publicHolidays")
@RequiredArgsConstructor
public class PublicHolidaysController {
    private final PublicHolidaysService publicHolidaysService;

    @GetMapping
    public ResponseEntity<List<PublicHolidays>>  getAllPublicHolidays() {
        List<PublicHolidays> publicHolidays = publicHolidaysService.findAll();
        return new ResponseEntity<>(publicHolidays, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicHolidays> getPublicHolidaysById(@PathVariable Long id) {
       Optional<PublicHolidays>  publicHolidays = publicHolidaysService.findById(id);
       if (publicHolidays.isPresent()) {
           return new ResponseEntity<>(publicHolidays.get(), HttpStatus.OK);
       }
       return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @PostMapping
    public ResponseEntity<?> save(@RequestBody HolidaysDTO holoholiday) {
        try {


            // Convertir le timestamp en LocalDate (date sans heure)
            Long startTimestampMillis = holoholiday.getStartDatePublicHolidays() * 1000;
            LocalDate startDate = Instant.ofEpochMilli(startTimestampMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();

            // Convertir LocalDate en LocalDateTime avec l'heure fixée à 00:00:00
            LocalDateTime startDateTime = startDate.atStartOfDay();

                PublicHolidays publicHolidays = new PublicHolidays(
                        holoholiday.getId(),
                        holoholiday.getName(),
                        startDateTime,
                        holoholiday.getNbdays()
                );

                System.out.println(publicHolidays);
            PublicHolidays saved = publicHolidaysService.save(publicHolidays);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            return new ResponseEntity<>(Map.of("message", ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }


@PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody HolidaysDTO holoholiday) {
    if (publicHolidaysService.findById(id).isPresent()) {

        Long startTimestampMillis = holoholiday.getStartDatePublicHolidays() * 1000;


        LocalDateTime start = Instant.ofEpochMilli(startTimestampMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();



        PublicHolidays publicHolidays = new PublicHolidays(
                holoholiday.getId(),
                holoholiday.getName(),
                start,
                holoholiday.getNbdays()
        );

        PublicHolidays hol = publicHolidaysService.update(publicHolidays , id);
        if (hol != null) {
            return new ResponseEntity<>(hol, HttpStatus.OK);
        }

    } return new ResponseEntity<>(HttpStatus.NOT_FOUND);
}
}
