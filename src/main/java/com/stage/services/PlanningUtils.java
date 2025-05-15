package com.stage.services;

import com.stage.dto.PublicHolidaysDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PlanningUtils {
    public static LocalDateTime calculerDateFinEffective(LocalDateTime startDate, int dureeHeures, List<PublicHolidaysDTO> joursFeries) {
        LocalDateTime current = startDate;
        int heuresTravaillees = 0;

        while (heuresTravaillees < dureeHeures) {
            boolean estJourFerie = false;

            for (PublicHolidaysDTO ferié : joursFeries) {
                if (!current.isBefore(ferié.getStartDateHolidays()) &&
                        !current.isAfter(ferié.getEndDateHolidays())) {
                    estJourFerie = true;
                    break;
                }
            }

            if (!estJourFerie) {
                heuresTravaillees++;
            }

            current = current.plusHours(1);
        }

        return current;
    }
}
