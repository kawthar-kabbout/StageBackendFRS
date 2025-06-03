package com.stage.services;

import com.stage.dto.EmployerDTo;
import com.stage.dto.MachineDTO;
import com.stage.dto.PublicHolidaysDTO;
import com.stage.persistans.*;
import lombok.RequiredArgsConstructor;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Service
public class ChocosolverService {
    private final WorkTimeService workTimeService;
    private final EmployerService employerService;
    private final ActivityService activityService;
    private final MachineService machineService;
    private final DependanceActivityService dependanceActivityService;
    private final VacationService publicHolidaysService;

    // Vérifie si une date est un jour férié
    private static boolean estJourFerie(LocalDate date, List<PublicHolidaysDTO> holidays) {
        return holidays.stream().anyMatch(h -> {
            LocalDate start = h.getStartDateHolidays().toLocalDate();
            LocalDate end = h.getEndDateHolidays().toLocalDate();
            return !date.isBefore(start) && !date.isAfter(end);
        });
    }

    // Vérifie si une heure est dans l'horaire de travail du jour
    private static boolean estDansHoraireTravail(LocalTime time, DayOfWeek day, WorkTime workTime) {
        return workTime.getDailyWorkTimes().stream()
                .filter(dwt -> dwt.getDay() == day)
                .anyMatch(dwt ->
                        (!time.isBefore(dwt.getMorningStart()) && time.isBefore(dwt.getMorningEnd())) ||
                                (!time.isBefore(dwt.getAfternoonStart()) && time.isBefore(dwt.getAfternoonEnd()))
                );
    }

    public List<Activity> chocosolver(List<Project> projects, LocalDateTime startPlanning) {
        System.out.println("startPlanning: " + startPlanning);

        // Récupération des données
        WorkTime workTime = workTimeService.getWorkTimeById(1L);
        List<PublicHolidaysDTO> holidays = publicHolidaysService.findAllPublicHolidaysDTO();
        List<EmployerDTo> employers = employerService.getALLEmployerDTO();
        List<MachineDTO> machines = machineService.getALlMachineDTO();
        List<Activity> activities = new ArrayList<>();
        List<DependanceActivity> deps = new ArrayList<>();

        projects.forEach(project -> {
            activities.addAll(activityService.getActivitiesByProjectId(project.getId()));
            deps.addAll(dependanceActivityService.getDependenceActivitiesByProjectId(project.getId()));
        });

        // Initialisation du modèle Choco Solver
        Model model = new Model("Activity Assignment Problem with Employers and Machines");

        // Configuration de l'horizon temporel (90 jours)
        int horizonHours = 24 * 90;
        LocalDateTime baseDate = startPlanning.truncatedTo(ChronoUnit.HOURS);

        // Précalcul des heures valides avec leurs timestamps réels
        List<Integer> heuresValides = new ArrayList<>();
        Map<Integer, LocalDateTime> indexToDateTime = new HashMap<>();

        for (int h = 0; h <= horizonHours; h++) {
            LocalDateTime current = baseDate.plusHours(h);
            if (!estJourFerie(current.toLocalDate(), holidays) &&
                    estDansHoraireTravail(current.toLocalTime(), current.getDayOfWeek(), workTime)) {
                heuresValides.add(h);
                indexToDateTime.put(h, current);
            }
        }

        if (heuresValides.isEmpty()) {
            System.out.println("Aucune heure valide trouvée dans l'horizon donné.");
            return Collections.emptyList();
        }

        // Variables de début (indices dans heuresValides)
        IntVar[] startIndex = new IntVar[activities.size()];
        IntVar[] endIndex = new IntVar[activities.size()];
        IntVar[] durations = new IntVar[activities.size()];

        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);
            int duration = activity.getDuration();

            if (duration > heuresValides.size()) {
                System.out.println("Durée trop longue pour l'horizon pour l'activité " + activity.getName());
                return Collections.emptyList();
            }

            int maxStartIndex = heuresValides.size() - duration;
            startIndex[i] = model.intVar("startIdx_" + activity.getName(), 0, maxStartIndex);
            endIndex[i] = model.intVar("endIdx_" + activity.getName(), duration, heuresValides.size());
            durations[i] = model.intVar(duration); // Durée fixe

            // Relation entre start et end
            model.arithm(endIndex[i], "=", startIndex[i], "+", durations[i]).post();
        }

        // Contraintes de dépendances (FS, SS, FF, SF)
        for (DependanceActivity dep : deps) {
            int targetIdx = activities.indexOf(dep.getTargetActivity());
            int predIdx = activities.indexOf(dep.getPredecessorActivity());
            int delay = dep.getDelay();

            switch (dep.getDependencyType()) {
                case FS:
                    model.arithm(startIndex[targetIdx], ">=", endIndex[predIdx], "+", delay).post();
                    break;
                case SS:
                    model.arithm(startIndex[targetIdx], ">=", startIndex[predIdx], "+", delay).post();
                    break;
                case FF:
                    model.arithm(endIndex[targetIdx], ">=", endIndex[predIdx], "+", delay).post();
                    break;
                case SF:
                    model.arithm(endIndex[targetIdx], ">=", startIndex[predIdx], "+", delay).post();
                    break;
                default:
                    throw new IllegalArgumentException("Type de dépendance inconnu : " + dep.getDependencyType());
            }
        }

        // Variables d'affectation
        IntVar[][] employerAssignment = new IntVar[activities.size()][employers.size()];
        IntVar[][] machineAssignment = new IntVar[activities.size()][machines.size()];

        for (int i = 0; i < activities.size(); i++) {
            for (int j = 0; j < employers.size(); j++) {
                employerAssignment[i][j] = model.intVar("E" + i + "_" + j, 0, 1);
            }
            for (int k = 0; k < machines.size(); k++) {
                machineAssignment[i][k] = model.intVar("M" + i + "_" + k, 0, 1);
            }
        }

        // Calcul des disponibilités initiales en indices dans heuresValides
        int[] maxEndEmployers = new int[employers.size()];
        int[] maxEndMachines = new int[machines.size()];

        for (int j = 0; j < employers.size(); j++) {
            final int idx = j;
            maxEndEmployers[j] = employers.get(j).getActivitiesNotFinish().stream()
                    .mapToInt(a -> {
                        long hoursSinceStart = Duration.between(startPlanning, a.getPlannedEndDate()).toHours();
                        for (int h = 0; h < heuresValides.size(); h++) {
                            if (heuresValides.get(h) >= hoursSinceStart) {
                                return h;
                            }
                        }
                        return -1;
                    })
                    .max().orElse(-1);
        }

        for (int k = 0; k < machines.size(); k++) {
            maxEndMachines[k] = machines.get(k).getActivitiesNotFinish().stream()
                    .mapToInt(a -> {
                        long hoursSinceStart = Duration.between(startPlanning, a.getPlannedEndDate()).toHours();
                        for (int h = 0; h < heuresValides.size(); h++) {
                            if (heuresValides.get(h) >= hoursSinceStart) {
                                return h;
                            }
                        }
                        return -1;
                    })
                    .max().orElse(-1);
        }

        // Contraintes principales par activité
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);

            if (activity.getSkill() == null) {
                for (int j = 0; j < employers.size(); j++) {
                    model.arithm(employerAssignment[i][j], "=", 0).post();
                }
                for (int k = 0; k < machines.size(); k++) {
                    model.arithm(machineAssignment[i][k], "=", 0).post();
                }
                continue;
            }

            // Affectation des employés
            model.sum(employerAssignment[i], "=", activity.getEmployersNumber()).post();
            boolean hasEmployer = false;

            for (int j = 0; j < employers.size(); j++) {
                if (!employers.get(j).getSkills().stream()
                        .anyMatch(s -> s.getId().equals(activity.getSkill().getId()))) {
                    model.arithm(employerAssignment[i][j], "=", 0).post();
                } else {
                    hasEmployer = true;
                    if (maxEndEmployers[j] >= 0) {
                        model.ifThen(
                                model.arithm(employerAssignment[i][j], "=", 1),
                                model.arithm(startIndex[i], ">=", maxEndEmployers[j])
                        );
                    }
                }
            }

            if (!hasEmployer) {
                for (int j = 0; j < employers.size(); j++) {
                    model.arithm(employerAssignment[i][j], "=", 0).post();
                }
            }

            // Affectation des machines
            if (activity.getCapabilityMachine() != null) {
                model.sum(machineAssignment[i], "=", 1).post();
                boolean hasMachine = false;

                for (int k = 0; k < machines.size(); k++) {
                    if (!machines.get(k).getCapabilityMachine().stream()
                            .anyMatch(c -> c.getId().equals(activity.getCapabilityMachine().getId()))) {
                        model.arithm(machineAssignment[i][k], "=", 0).post();
                    } else {
                        hasMachine = true;
                        if (maxEndMachines[k] >= 0) {
                            model.ifThen(
                                    model.arithm(machineAssignment[i][k], "=", 1),
                                    model.arithm(startIndex[i], ">=", maxEndMachines[k])
                            );
                        }
                    }
                }

                if (!hasMachine) {
                    for (int k = 0; k < machines.size(); k++) {
                        model.arithm(machineAssignment[i][k], "=", 0).post();
                    }
                }
            } else {
                for (int k = 0; k < machines.size(); k++) {
                    model.arithm(machineAssignment[i][k], "=", 0).post();
                }
            }
        }

        // Contraintes de non-chevauchement
        for (int j = 0; j < employers.size(); j++) {
            for (int i1 = 0; i1 < activities.size(); i1++) {
                for (int i2 = i1 + 1; i2 < activities.size(); i2++) {
                    model.ifThen(
                            model.and(
                                    model.arithm(employerAssignment[i1][j], "=", 1),
                                    model.arithm(employerAssignment[i2][j], "=", 1)
                            ),
                            model.or(
                                    model.arithm(endIndex[i1], "<=", startIndex[i2]),
                                    model.arithm(endIndex[i2], "<=", startIndex[i1])
                            )
                    );
                }
            }
        }

        for (int k = 0; k < machines.size(); k++) {
            for (int i1 = 0; i1 < activities.size(); i1++) {
                for (int i2 = i1 + 1; i2 < activities.size(); i2++) {
                    model.ifThen(
                            model.and(
                                    model.arithm(machineAssignment[i1][k], "=", 1),
                                    model.arithm(machineAssignment[i2][k], "=", 1)
                            ),
                            model.or(
                                    model.arithm(endIndex[i1], "<=", startIndex[i2]),
                                    model.arithm(endIndex[i2], "<=", startIndex[i1])
                            )
                    );
                }
            }
        }

        // Objectif : minimiser le makespan
        IntVar makespan = model.intVar("makespan", 0, 2160);
        model.max(makespan, endIndex).post();
        model.setObjective(Model.MINIMIZE, makespan);

        // Résolution
        System.out.println("Recherche de la meilleure solution...");
        Solver solver = model.getSolver();
        solver.limitSolution(999999);
        List<Activity> results = new ArrayList<>();
        int bestMakespan = Integer.MAX_VALUE;

        while (solver.solve()) {
            if (makespan.getValue() >= bestMakespan) continue;

            bestMakespan = makespan.getValue();
            results.clear();

            for (int i = 0; i < activities.size(); i++) {
                Activity activity = activities.get(i);
                activity.setEmployees(new ArrayList<>());
                AtomicBoolean hasEmployers = new AtomicBoolean(false);
                AtomicBoolean hasMachine = new AtomicBoolean(false);

                // Employés assignés
                for (int j = 0; j < employers.size(); j++) {
                    if (employerAssignment[i][j].getValue() == 1) {
                        employerService.findById(employers.get(j).getId())
                                .ifPresent(emp -> {
                                    activity.getEmployees().add(emp);
                                    hasEmployers.set(true);
                                });
                    }
                }

                // Machine assignée
                for (int k = 0; k < machines.size(); k++) {
                    if (machineAssignment[i][k].getValue() == 1) {
                        machineService.findById(machines.get(k).getId())
                                .ifPresent(machine -> {
                                    activity.setMachine(machine);
                                    hasMachine.set(true);
                                });
                    }
                }

                // Calcul des dates planifiées
                int startIdx = startIndex[i].getValue();
                int endIdx = startIdx + activity.getDuration();

                LocalDateTime startDate = indexToDateTime.get(heuresValides.get(startIdx));
                LocalDateTime endDate = indexToDateTime.get(heuresValides.get(endIdx));

                activity.setPlannedStartDate(startDate);
                activity.setPlannedEndDate(endDate);
                results.add(activity);
                activityService.updateActivity(activity);
            }
        }

        if (bestMakespan == Integer.MAX_VALUE) {
            System.out.println("Aucune solution trouvée.");
        }

        return results;
    }
}