package com.stage.services;

import com.stage.dto.PublicHolidaysDTO;
import com.stage.persistans.PublicHolidays;
import com.stage.repositories.PublicHolidaysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PublicHolidaysService {
    private final PublicHolidaysRepository publicHolidaysRepository;


    public List<PublicHolidays> findAll() {
        return publicHolidaysRepository.findAll();
    }
   public Optional<PublicHolidays> findById(Long id) {
        return publicHolidaysRepository.findById(id);
   }
    public PublicHolidays save(PublicHolidays publicHolidays) {



        return publicHolidaysRepository.save(publicHolidays);
    }
        public PublicHolidays update(PublicHolidays publicHolidays, Long id) {

            if (publicHolidaysRepository.findById(id).isPresent()) {
                PublicHolidays p = publicHolidaysRepository.findById(id).get();
                p.setStartDatePublicHolidays(publicHolidays.getStartDatePublicHolidays());
                p.setNbdays(publicHolidays.getNbdays());
                p.setName(publicHolidays.getName());
                return publicHolidaysRepository.save(p);

            }
            return null;
        }


    public List<PublicHolidaysDTO> findAllPublicHolidaysDTO() {
        List<PublicHolidays> publicHolidays = publicHolidaysRepository.findAll();
        List<PublicHolidaysDTO> publicHolidaysDTOs = new ArrayList<>();

        for (PublicHolidays publicHoliday : publicHolidays) {
            PublicHolidaysDTO dto = new PublicHolidaysDTO();

            dto.setId(publicHoliday.getId());
            dto.setName(publicHoliday.getName());
            dto.setStartDatePublicHolidays(publicHoliday.getStartDatePublicHolidays());

            // Calcul de la date de fin :
            // Ajout des jours fériés + Soustrait 1 minute pour couvrir l'entièreté du dernier jour
            LocalDateTime endDate = publicHoliday.getStartDatePublicHolidays()
                    .plusDays(publicHoliday.getNbdays())  // Ajout du nombre de jours
                    .minusMinutes(1);                     // Soustrait 1 minute au lieu de 1 seconde
            dto.setEndDatePublicHolidays(endDate);

            publicHolidaysDTOs.add(dto);
            System.out.println("holidaysDTO: " + dto.getStartDatePublicHolidays() + ", " + dto.getEndDatePublicHolidays());
        }

        return publicHolidaysDTOs;
    }

        }
