package com.stage.services;

import com.stage.dto.PublicHolidaysDTO;
import com.stage.persistans.Vacation;
import com.stage.repositories.VacationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VacationService {
    private final VacationRepository publicHolidaysRepository;


    public List<Vacation> findAll() {
        List<Vacation> vacations = new ArrayList<>();
        List<Vacation> publicHolidays = publicHolidaysRepository.findAll();
        for (Vacation vacation : publicHolidays) {
            if (vacation.getArchived()==0)
                vacations.add(vacation);
        }

        return vacations;
    }
   public Optional<Vacation> findById(Long id) {

        Vacation vacation = publicHolidaysRepository.findById(id).orElse(null);
        if (vacation!=null && vacation.getArchived()==0){
            return Optional.of(vacation);

        }
        return Optional.ofNullable(vacation);


   }
    public Vacation save(Vacation publicHolidays) {



        return publicHolidaysRepository.save(publicHolidays);
    }
        public Vacation update(Vacation publicHolidays, Long id) {

            if (publicHolidaysRepository.findById(id).isPresent()) {
                Vacation p = publicHolidaysRepository.findById(id).get();
                p.setStartDate(publicHolidays.getStartDate());
                p.setNbdays(publicHolidays.getNbdays());
                p.setName(publicHolidays.getName());
                return publicHolidaysRepository.save(p);

            }
            return null;
        }


    public List<PublicHolidaysDTO> findAllPublicHolidaysDTO() {
        List<Vacation> publicHolidays = publicHolidaysRepository.findAll();
        List<PublicHolidaysDTO> publicHolidaysDTOs = new ArrayList<>();

        for (Vacation publicHoliday : publicHolidays) {
            PublicHolidaysDTO dto = new PublicHolidaysDTO();

            dto.setId(publicHoliday.getId());
            dto.setName(publicHoliday.getName());
            dto.setStartDateHolidays(publicHoliday.getStartDate());

            // Calcul de la date de fin :
            // Ajout des jours fériés + Soustrait 1 minute pour couvrir l'entièreté du dernier jour
            LocalDateTime endDate = publicHoliday.getStartDate()
                    .plusDays(publicHoliday.getNbdays())  // Ajout du nombre de jours
                    .minusMinutes(1);                     // Soustrait 1 minute au lieu de 1 seconde
            dto.setEndDateHolidays(endDate);

            publicHolidaysDTOs.add(dto);
            System.out.println("holidaysDTO: " + dto.getStartDateHolidays() + ", " + dto.getEndDateHolidays());
        }

        return publicHolidaysDTOs;
    }

        }
