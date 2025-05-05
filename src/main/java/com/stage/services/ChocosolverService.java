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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ChocosolverService {

private  final  EmployerService employerService;
private final ActivityService activityService;
private final MachineService machineService;
private final DependanceActivityService dependanceActivityService;
private final PublicHolidaysService publicHolidaysService;
    public List<Activity> chocosolver(List<Project> projects, LocalDateTime startPlanning) {
        System.out.println("startPlanning: " + startPlanning);

        // Récupération des données initiales
        List<PublicHolidaysDTO> holidays = publicHolidaysService.findAllPublicHolidaysDTO();
        List<EmployerDTo> employers = employerService.getALLEmployerDTO();
        List<MachineDTO> machines = machineService.getALlMachineDTO();
        List<Activity> activities = new ArrayList<>();
        List<DependanceActivity> deps = new ArrayList<>();

        for (Project project : projects) {
            activities.addAll(activityService.getActivitiesByProjectId(project.getId()));
            deps.addAll(dependanceActivityService.getDependenceActivitiesByProjectId(project.getId()));
        }

        // Initialisation du modèle Choco Solver
        Model model = new Model("Activity Assignment Problem with Employers and Machines");

        // Variables pour l'affectation des activités aux employeurs
        IntVar[][] employerAssignment = new IntVar[activities.size()][employers.size()];
        for (int i = 0; i < activities.size(); i++) {
            for (int j = 0; j < employers.size(); j++) {
                employerAssignment[i][j] = model.intVar("Activity" + i + "_employer" + j, 0, 1);
            }
        }

        // Variables pour l'affectation des activités aux machines
        IntVar[][] machineAssignment = new IntVar[activities.size()][machines.size()];
        for (int i = 0; i < activities.size(); i++) {
            for (int k = 0; k < machines.size(); k++) {
                machineAssignment[i][k] = model.intVar("Activity" + i + "_machine" + k, 0, 1);
            }
        }

        // Variables pour les dates de début et de fin des activités
        IntVar[] startDates = new IntVar[activities.size()];
        IntVar[] endDates = new IntVar[activities.size()];

        for (int i = 0; i < activities.size(); i++) {
            startDates[i] = model.intVar("start_" + activities.get(i).getName(), 0, 2160);
            endDates[i] = model.intVar("end_" + activities.get(i).getName(), 0, 2160);

            // Condition : Si la durée de l'activité est 0, alors startDate == endDate
            if (activities.get(i).getDuration() == 0) {
                model.arithm(startDates[i], "=", endDates[i]).post();
            } else {
                model.arithm(endDates[i], ">=", startDates[i], "+", activities.get(i).getDuration()).post();
            }
        }

        // Contrainte : Ajuster les dates si elles tombent pendant les vacances publiques
        // Contrainte : Une activité ne doit pas chevaucher un jour férié
        for (int i = 0; i < activities.size(); i++) {
            for (PublicHolidaysDTO holiday : holidays) {
                // Convertir les dates du jour férié en heures depuis startPlanning
                int holidayStartHour = (int) Duration.between(startPlanning, holiday.getStartDatePublicHolidays()).toHours();
                int holidayEndHour = (int) Duration.between(startPlanning, holiday.getEndDatePublicHolidays()).toHours();

                // Contrainte : L'activité doit être entièrement avant ou après le jour férié
                model.or(
                        model.arithm(endDates[i], "<=", holidayStartHour),  // Activité terminée avant le jour férié
                        model.arithm(startDates[i], ">=", holidayEndHour)   // Activité commencée après le jour férié
                ).post();
            }
        }




        // Calcul des dates de fin maximales pour chaque employé
        int[] maxExistingEndsEmployers = new int[employers.size()];
        for (int j = 0; j < employers.size(); j++) {
            EmployerDTo employer = employers.get(j);
            long maxEnd = employer.getActivitiesNotFinish().stream()
                    .map(Activity::getPlannedEndDate)
                    .mapToLong(endDate -> Duration.between(startPlanning, endDate).toHours())
                    .max()
                    .orElse(-1); // -1 si l'employé n'a pas d'activités existantes
            maxExistingEndsEmployers[j] = (int) maxEnd;
        }

        // Calcul des dates de fin maximales pour chaque machine
        int[] maxExistingEndsMachines = new int[machines.size()];
        for (int k = 0; k < machines.size(); k++) {
            MachineDTO machine = machines.get(k);
            long maxEnd = machine.getActivitiesNotFinish().stream()
                    .map(Activity::getPlannedEndDate)
                    .mapToLong(endDate -> Duration.between(startPlanning, endDate).toHours())
                    .max()
                    .orElse(-1); // -1 si la machine n'a pas d'activités existantes
            maxExistingEndsMachines[k] = (int) maxEnd;
        }

        // Contraintes de dépendance des tâches
        for (DependanceActivity dep : deps) {
            int targetIndex = activities.indexOf(dep.getTargetActivity());
            int predecessorIndex = activities.indexOf(dep.getPredecessorActivity());
            switch (dep.getDependencyType()) {
                case FS: // Finish-to-Start
                    model.arithm(startDates[targetIndex], ">=", endDates[predecessorIndex], "+", dep.getDelay()).post();
                    break;
                case SS: // Start-to-Start
                    model.arithm(startDates[targetIndex], ">=", startDates[predecessorIndex], "+", dep.getDelay()).post();
                    break;
                case FF: // Finish-to-Finish
                    model.arithm(endDates[targetIndex], ">=", endDates[predecessorIndex], "+", dep.getDelay()).post();
                    break;
                case SF: // Start-to-Finish
                    model.arithm(endDates[predecessorIndex], ">=", startDates[targetIndex], "+", dep.getDelay()).post();
                    break;
                default:
                    throw new IllegalArgumentException("Type de dépendance inconnu : " + dep.getDependencyType());
            }
        }

        // Contraintes principales
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);

            // Condition 1 : Si l'activité n'a pas de compétence (Skill == null)
            if (activity.getSkill() == null) {
                for (int j = 0; j < employers.size(); j++) {
                    model.arithm(employerAssignment[i][j], "=", 0).post();
                }
                for (int k = 0; k < machines.size(); k++) {
                    model.arithm(machineAssignment[i][k], "=", 0).post();
                }
            } else {
                // Condition 2 : Si l'activité a un Skill
                Skill requiredSkill = activity.getSkill();
                CapabilityMachine requiredCapability = activity.getCapabilityMachine();

                // Contrainte sur les employeurs
                boolean canAssignEmployers = false;
                model.sum(employerAssignment[i], "=", activity.getEmployersNumber()).post(); // Nombre exact d'employeurs requis
                for (int j = 0; j < employers.size(); j++) {
                    EmployerDTo employer = employers.get(j);
                    if (employer.getSkills().stream().noneMatch(skill -> skill.getId().equals(requiredSkill.getId()))) {
                        model.arithm(employerAssignment[i][j], "=", 0).post();
                    } else {
                        canAssignEmployers = true;
                        // Contrainte de disponibilité individuelle
                        if (maxExistingEndsEmployers[j] >= 0) {
                            model.ifThen(
                                    model.arithm(employerAssignment[i][j], "=", 1),
                                    model.arithm(startDates[i], ">=", maxExistingEndsEmployers[j])
                            );
                        }
                    }
                }

                // Contrainte sur les machines
                if (requiredCapability != null) {
                    boolean canAssignMachines = false;
                    model.sum(machineAssignment[i], "=", 1).post(); // Une seule machine peut être affectée
                    for (int k = 0; k < machines.size(); k++) {
                        MachineDTO machine = machines.get(k);
                        if (machine.getCapabilityMachine().stream().noneMatch(cap -> cap.getId().equals(requiredCapability.getId()))) {
                            model.arithm(machineAssignment[i][k], "=", 0).post();
                        } else {
                            canAssignMachines = true;
                            // Contrainte de disponibilité individuelle
                            if (maxExistingEndsMachines[k] >= 0) {
                                model.ifThen(
                                        model.arithm(machineAssignment[i][k], "=", 1),
                                        model.arithm(startDates[i], ">=", maxExistingEndsMachines[k])
                                );
                            }
                        }
                    }
                    // Si aucune machine compatible n'est trouvée, désactiver l'affectation
                    if (!canAssignMachines) {
                        for (int k = 0; k < machines.size(); k++) {
                            model.arithm(machineAssignment[i][k], "=", 0).post();
                        }
                    }
                } else {
                    // Si l'activité n'a pas de capacité requise, aucune machine ne doit être affectée
                    for (int k = 0; k < machines.size(); k++) {
                        model.arithm(machineAssignment[i][k], "=", 0).post();
                    }
                }

                // Si aucun employeur compatible n'est trouvé, désactiver l'affectation
                if (!canAssignEmployers) {
                    for (int j = 0; j < employers.size(); j++) {
                        model.arithm(employerAssignment[i][j], "=", 0).post();
                    }
                }
            }
        }

        // Contrainte : Un employeur ne peut pas travailler sur deux activités simultanément
        for (int j = 0; j < employers.size(); j++) {
            for (int i1 = 0; i1 < activities.size(); i1++) {
                for (int i2 = i1 + 1; i2 < activities.size(); i2++) {
                    model.ifThen(
                            model.and(
                                    model.arithm(employerAssignment[i1][j], "=", 1),
                                    model.arithm(employerAssignment[i2][j], "=", 1)
                            ),
                            model.or(
                                    model.arithm(endDates[i1], "<=", startDates[i2]),
                                    model.arithm(endDates[i2], "<=", startDates[i1])
                            )
                    );
                }
            }
        }

        // Contrainte : Une machine ne peut pas être utilisée sur deux activités simultanément
        for (int k = 0; k < machines.size(); k++) {
            for (int i1 = 0; i1 < activities.size(); i1++) {
                for (int i2 = i1 + 1; i2 < activities.size(); i2++) {
                    model.ifThen(
                            model.and(
                                    model.arithm(machineAssignment[i1][k], "=", 1),
                                    model.arithm(machineAssignment[i2][k], "=", 1)
                            ),
                            model.or(
                                    model.arithm(endDates[i1], "<=", startDates[i2]),
                                    model.arithm(endDates[i2], "<=", startDates[i1])
                            )
                    );
                }
            }
        }

        // Contrainte : startDate >= max(maxExistingEnds[j] pour les employés et machines affectés)
        for (int i = 0; i < activities.size(); i++) {
            IntVar[] assignedEmployers = new IntVar[employers.size()];
            IntVar[] assignedMachines = new IntVar[machines.size()];
            for (int j = 0; j < employers.size(); j++) {
                assignedEmployers[j] = employerAssignment[i][j];
            }
            for (int k = 0; k < machines.size(); k++) {
                assignedMachines[k] = machineAssignment[i][k];
            }

            // Variable pour stocker le maximum des dates de fin existantes des employés affectés
            IntVar maxEndVarEmployers = model.intVar("maxEndEmployers_" + i, 0, 2160);
            for (int j = 0; j < employers.size(); j++) {
                model.ifThen(
                        model.arithm(employerAssignment[i][j], "=", 1), // Si l'employé est affecté
                        model.arithm(maxEndVarEmployers, ">=", maxExistingEndsEmployers[j]) // Alors maxEndVarEmployers >= sa date de fin existante
                );
            }

            // Variable pour stocker le maximum des dates de fin existantes des machines affectées
            IntVar maxEndVarMachines = model.intVar("maxEndMachines_" + i, 0, 2160);
            for (int k = 0; k < machines.size(); k++) {
                model.ifThen(
                        model.arithm(machineAssignment[i][k], "=", 1), // Si la machine est affectée
                        model.arithm(maxEndVarMachines, ">=", maxExistingEndsMachines[k]) // Alors maxEndVarMachines >= sa date de fin existante
                );
            }

            // Variable pour stocker le maximum global (entre les employés et les machines)
            IntVar maxEndVarGlobal = model.intVar("maxEndGlobal_" + i, 0, 2160);
            model.max(maxEndVarGlobal, new IntVar[]{maxEndVarEmployers, maxEndVarMachines}).post();

            // Contrainte : startDate[i] >= maxEndVarGlobal
            model.ifThen(
                    model.and(
                            model.sum(assignedEmployers, "=", activities.get(i).getEmployersNumber()), // Si le nombre requis d'employés est affecté
                            model.sum(assignedMachines, "=", 1) // Si une machine est affectée (ou 0 si pas de machine requise)
                    ),
                    model.arithm(startDates[i], ">=", maxEndVarGlobal) // Alors startDate[i] >= maxEndVarGlobal
            );
        }

        // Variable pour la durée totale d'exécution
        IntVar totalDuration = model.intVar("totalDuration", 0, 2160);
        model.max(totalDuration, endDates).post();

        // Objectif : Minimiser la durée totale d'exécution
        model.setObjective(Model.MINIMIZE, totalDuration);

        // Résolution du modèle
        System.out.println("Recherche de la meilleure solution...");
        Solver solver = model.getSolver();
        solver.limitSolution(100); // Limite à 10 solutions
        int solutionCount = 0;
        List<Activity> results = new ArrayList<>();
        int bestTotalDuration = Integer.MAX_VALUE;

        while (solver.solve()) {
            solutionCount++;
            System.out.println("Solution " + solutionCount + " trouvée :");
            if (solutionCount == 1 || totalDuration.getValue() < bestTotalDuration) {
                results.clear();
                for (int i = 0; i < activities.size(); i++) {
                    Activity activity = activities.get(i);

                    // ✅ Vider la liste existante ou l'initialiser à vide
                    activity.setEmployees(new ArrayList<>());
                    // Mise à jour et affichage des employés affectés
                    boolean hasEmployers = false;
                    for (int j = 0; j < employers.size(); j++) {
                        if (employerAssignment[i][j].getValue() == 1) {
                            Optional<Employer> employerOpt = employerService.findById(employers.get(j).getId());
                            employerOpt.ifPresentOrElse(
                                    emp -> {
                                        activity.getEmployees().add(emp);
                                        System.out.print("Employé: " + emp.getFirstName() + ", ");
                                    },
                                    () -> { throw new RuntimeException("Employé non trouvé"); }
                            );
                            hasEmployers = true;
                        }
                    }

                    // Mise à jour et affichage des machines affectées
                    boolean hasMachines = false;
                    for (int k = 0; k < machines.size(); k++) {
                        if (machineAssignment[i][k].getValue() == 1) {
                            Optional<Machine> machineOpt = machineService.findById(machines.get(k).getId());
                            machineOpt.ifPresentOrElse(
                                    machine -> {
                                        activity.setMachine(machine);
                                        System.out.print("Machine: " + machine.getName() + ", ");
                                    },
                                    () -> { throw new RuntimeException("Machine non trouvée"); }
                            );
                            hasMachines = true;
                        }
                    }

                    // Si aucun employeur ni machine n'est affecté
                    if (!hasEmployers && !hasMachines) {
                        System.out.print("Pas d'employeurs ni de machines attribués, ");
                    }

                    // Mise à jour des dates planifiées
                    activity.setPlannedStartDate(startPlanning.plusHours(startDates[i].getValue()));
                    activity.setPlannedEndDate(startPlanning.plusHours(endDates[i].getValue()));

                    // Affichage des dates de début et de fin
                    System.out.println("[Start: " + startDates[i].getValue() + ", End: " + endDates[i].getValue() + "]");

                    // Ajout de l'activité aux résultats
                    results.add(activity);
                    activityService.updateActivity(activity);
                    System.out.println("["+activity.getName()+" : " +activity.getPlannedStartDate() + ", " + activity.getPlannedEndDate() + "]");
                }

                // Mise à jour de la meilleure durée totale
                bestTotalDuration = totalDuration.getValue();
            }

            // Affichage de la durée totale d'exécution
            System.out.println("Durée totale d'exécution : " + totalDuration.getValue() + " heures");
        }

        if (solutionCount == 0) {
            System.out.println("Aucune solution trouvée. Vérifiez les contraintes et les données.");
        }

        return results;
    }
}