package com.stage.services;

import com.stage.persistans.PublicHolidays;
import com.stage.repositories.PublicHolidaysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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


       /* // Vérifie si une plage de dates identique existe
        if (publicHolidaysRepository
                .findByStartDatePublicHolidaysAndEndDatePublicHolidays(
                        publicHolidays.getStartDatePublicHolidays(),
                        publicHolidays.getNbdays())
                .isPresent()) {
            throw new RuntimeException("Un jour férié avec la même plage de dates existe déjà.");
        }
*/
        return publicHolidaysRepository.save(publicHolidays);
    }
        public PublicHolidays update(PublicHolidays publicHolidays, Long id) {

        if (publicHolidaysRepository.findById(id).isPresent()) {
        PublicHolidays p=    publicHolidaysRepository.findById(id).get();
        p.setStartDatePublicHolidays(publicHolidays.getStartDatePublicHolidays());
        p.setNbdays(publicHolidays.getNbdays());
        p.setName(publicHolidays.getName());
        return publicHolidaysRepository.save(p);

        }
        return null;

        }


}
