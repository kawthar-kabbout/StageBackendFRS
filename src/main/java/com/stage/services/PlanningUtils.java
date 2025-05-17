package com.stage.services;

import com.stage.dto.PublicHolidaysDTO;
import com.stage.persistans.DailyWorkTime;
import com.stage.persistans.DatePlanningResult;
import com.stage.persistans.WorkTime;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public class PlanningUtils {

    // Calcule la date de début et de fin effective en fonction de la durée, jours fériés et heures de travail
    public static DatePlanningResult calculerDateDebutEtFinEffective(
            LocalDateTime startDate,
            int dureeHeures,
            List<PublicHolidaysDTO> holidays,
            WorkTime workTime) {

        // Corriger la date de début si elle tombe hors horaire ou jour férié
        LocalDateTime startEffective = trouverProchaineDateValide(startDate, holidays, workTime);

        LocalDateTime current = startEffective;
        int heuresTravaillees = 0;

        while (heuresTravaillees < dureeHeures) {
            if (!estDansJourFerie(current, holidays) && estDansHoraireTravail(current, workTime)) {
                heuresTravaillees++;
            }

            current = current.plusHours(1);
        }

        return new DatePlanningResult(startEffective, current); // current est la fin effective
    }

    // Corrige une date de départ invalide pour la faire commencer à la prochaine heure valide
    private static LocalDateTime trouverProchaineDateValide(
            LocalDateTime startDate,
            List<PublicHolidaysDTO> holidays,
            WorkTime workTime) {

        LocalDateTime current = startDate;

        while (estDansJourFerie(current, holidays) || !estDansHoraireTravail(current, workTime)) {
            current = current.plusHours(1);
        }

        return current;
    }

    // Vérifie si la date se trouve dans une plage de jours fériés
    private static boolean estDansJourFerie(LocalDateTime date, List<PublicHolidaysDTO> holidays) {
        return holidays.stream().anyMatch(ferie ->
                !date.isBefore(ferie.getStartDateHolidays()) &&
                        !date.isAfter(ferie.getEndDateHolidays())
        );
    }

    // Vérifie si la date/heure se trouve dans une plage horaire de travail
    private static boolean estDansHoraireTravail(LocalDateTime dateTime, WorkTime workTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();

        for (DailyWorkTime dwt : workTime.getDailyWorkTimes()) {
            if (dwt.getDay() == day) {
                if (!time.isBefore(dwt.getMorningStart()) && time.isBefore(dwt.getMorningEnd())) {
                    return true;
                }
                if (!time.isBefore(dwt.getAfternoonStart()) && time.isBefore(dwt.getAfternoonEnd())) {
                    return true;
                }
            }
        }

        return false;
    }
}


