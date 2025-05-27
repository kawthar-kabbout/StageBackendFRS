package com.stage.services;

import com.stage.dto.PublicHolidaysDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.DailyWorkTime;
import com.stage.persistans.DependanceActivity;
import com.stage.persistans.WorkTime;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanningUtilsChoco {
    private static boolean estJourFerie(LocalDate date, List<PublicHolidaysDTO> holidays) {
        for (PublicHolidaysDTO h : holidays) {
            LocalDate start = h.getStartDateHolidays().toLocalDate();
            LocalDate end = h.getEndDateHolidays().toLocalDate();
            if (!date.isBefore(start) && !date.isAfter(end)) {
                return true;
            }
        }
        return false;
    }

    private static boolean estDansHoraireTravail(LocalTime time, DayOfWeek day, WorkTime workTime) {
        for (DailyWorkTime dwt : workTime.getDailyWorkTimes()) {
            if (dwt.getDay() == day) {
                if ((!time.isBefore(dwt.getMorningStart()) && time.isBefore(dwt.getMorningEnd())) ||
                        (!time.isBefore(dwt.getAfternoonStart()) && time.isBefore(dwt.getAfternoonEnd()))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void planifierAvecChoco(
            List<Activity> activities,
            List<DependanceActivity> dependencies,
            List<PublicHolidaysDTO> holidays,
            WorkTime workTime
    ) {
        Model model = new Model("Planning");

        int horizon = 24 * 90; // 90 jours en heures
        LocalDateTime dateReference = LocalDate.now().atStartOfDay();

        // Construction de la liste des heures valides (indexées)
        List<Integer> heuresValides = new ArrayList<>();
        Map<Integer, LocalDateTime> indexToDateTime = new HashMap<>();
        for (int h = 0; h <= horizon; h++) {
            LocalDateTime dt = dateReference.plusHours(h);
            if (!estJourFerie(dt.toLocalDate(), holidays) &&
                    estDansHoraireTravail(dt.toLocalTime(), dt.getDayOfWeek(), workTime)) {
                heuresValides.add(h);
                indexToDateTime.put(h, dt);
            }
        }

        // Variables de début d'activités (index sur heuresValides)
        IntVar[] startIndex = new IntVar[activities.size()];

        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            int duration = activity.getDuration();

            // Création des variables de début d'activité dans l'index des heures valides
            startIndex[i] = model.intVar("startIdx_" + activity.getName(), 0, heuresValides.size() - duration);

            // S'assurer que les "duration" créneaux consécutifs sont utilisés
            for (int d = 0; d < duration - 1; d++) {
                model.arithm(startIndex[i], "<", heuresValides.size() - d - 1).post();
            }
        }

        // Contraintes de dépendances
        for (DependanceActivity dep : dependencies) {
            int targetIdx = activities.indexOf(dep.getTargetActivity());
            int predIdx = activities.indexOf(dep.getPredecessorActivity());
            int delay = dep.getDelay();
            switch (dep.getDependencyType()) {
                case FS: // Fin prédécesseur -> Début successeur
                    model.arithm(startIndex[targetIdx], ">=", model.intOffsetView(startIndex[predIdx], delay)).post();
                    break;
                case SS:
                    model.arithm(startIndex[targetIdx], ">=", model.intOffsetView(startIndex[predIdx], delay)).post();
                    break;
                case FF:
                case SF:
                    // Ces cas sont complexes dans l'indexation indirecte, à adapter si nécessaire
                    break;
            }
        }

        // Résolution
        if (model.getSolver().solve()) {
            for (int i = 0; i < activities.size(); i++) {
                int startIdx = startIndex[i].getValue();
                int duration = activities.get(i).getDuration();
                int realStartHour = heuresValides.get(startIdx);
                int realEndHour = heuresValides.get(startIdx + duration - 1) + 1;

                LocalDateTime startDate = dateReference.plusHours(realStartHour);
                LocalDateTime endDate = dateReference.plusHours(realEndHour);

                activities.get(i).setPlannedStartDate(startDate);
                activities.get(i).setPlannedEndDate(endDate);

                System.out.println(activities.get(i).getName() + " : start = " + startDate + ", end = " + endDate);
            }
        } else {
            System.out.println("Aucune solution trouvée.");
        }
    }
}