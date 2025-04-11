package com.stage.services;

import com.stage.dto.ActiviteFrontDTO;
import com.stage.persistans.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@RequiredArgsConstructor
@Service
public class ChocosolverService {

private  final  EmployerService employerService;
private final ActivityService activityService;
private final MachineService machineService;
private final DependanceActivityService dependanceActivityService;

    public List <Activity>chocosolver(List<Project> projects) {
        // Récupération des données initiales
        List<Employer> employers = employerService.findAll();
       // List<Activity> activities = activityService.getActivitiesByProjectId(projectId);
       // List<DependanceActivity> deps = dependanceActivityService.getDependenceActivitiesByProjectId(projectId);
        List<Machine> machines = machineService.findAll();

    List<Activity>activities = new ArrayList<>();
    List<DependanceActivity>deps =new ArrayList<>();
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
                // Sinon, appliquer la contrainte habituelle
                model.arithm(endDates[i], ">=", startDates[i], "+", activities.get(i).getDuration()).post();
            }
        }

        // Contraintes principales
        for (int i = 0; i < activities.size(); i++) {
            Activity activity = activities.get(i);

            // Condition 1 : Si l'activité n'a pas de compétence (Skill == null)
            if (activity.getSkill() == null) {
                // Aucun employeur ni machine ne doit être affecté
                System.out.println("skill" +activity.getSkill());


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
                    Employer employer = employers.get(j);
                    if (employer.getSkills().stream().noneMatch(skill -> skill.getId().equals(requiredSkill.getId()))) {
                        // L'employeur n'a pas la compétence requise
                        model.arithm(employerAssignment[i][j], "=", 0).post();
                    } else {
                        canAssignEmployers = true;
                    }
                }

                // Contrainte sur les machines
                if (requiredCapability != null) {
                    boolean canAssignMachines = false;
                    model.sum(machineAssignment[i], "=", 1).post(); // Une seule machine peut être affectée
                    for (int k = 0; k < machines.size(); k++) {
                        Machine machine = machines.get(k);
                        if (machine.getCapabilityMachines().stream().noneMatch(cap -> cap.getId().equals(requiredCapability.getId()))) {
                            // La machine n'a pas la capacité requise
                            model.arithm(machineAssignment[i][k], "=", 0).post();
                        } else {
                            canAssignMachines = true;
                        }
                    }

                    // Si aucune machine compatible n'est trouvée, désactiver l'affectation
                    if (!canAssignMachines) {
                        for (int k = 0; k < machines.size(); k++) {
                            model.arithm(machineAssignment[i][k], "=", 0).post();
                        }
                    }
                } else {
                    // Si l'activité n'a pas de CapabilityWithMachine, aucune machine ne doit être affectée
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


        // Contraintes de dépendance des tâches
        for (int i = 0; i < deps.size(); i++) {
            if (deps.get(i).getDependencyType().equals("FS")) {
                int targetIndex = activities.indexOf(deps.get(i).getTargetActivity());
                int predecessorIndex = activities.indexOf(deps.get(i).getPredecessorActivity());
                model.arithm(startDates[targetIndex], ">=", endDates[predecessorIndex], "+", deps.get(i).getDelay()).post();
            } else if (deps.get(i).getDependencyType().equals("SS")) {
                int targetIndex = activities.indexOf(deps.get(i).getTargetActivity());
                int predecessorIndex = activities.indexOf(deps.get(i).getPredecessorActivity());
                model.arithm(startDates[targetIndex], ">=", startDates[predecessorIndex], "+", deps.get(i).getDelay()).post();
            } else if (deps.get(i).getDependencyType().equals("FF")) {
                int targetIndex = activities.indexOf(deps.get(i).getTargetActivity());
                int predecessorIndex = activities.indexOf(deps.get(i).getPredecessorActivity());
                model.arithm(endDates[targetIndex], ">=", endDates[predecessorIndex], "+", deps.get(i).getDelay()).post();
            } else if (deps.get(i).getDependencyType().equals("SF")) {
                int targetIndex = activities.indexOf(deps.get(i).getTargetActivity());
                int predecessorIndex = activities.indexOf(deps.get(i).getPredecessorActivity());
                model.arithm(endDates[predecessorIndex], ">=", startDates[targetIndex], "+", deps.get(i).getDelay()).post();
            }
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
         List<Activity>results = new ArrayList<Activity>();
        while (solver.solve()) {
            solutionCount++;
            System.out.println("Solution " + solutionCount + " trouvée :");
            for (int i = 0; i < activities.size(); i++) {


                Activity activity = activities.get(i);
                System.out.print(activity.getName() + " -> ");

                // Affichage des employeurs affectés
                boolean hasEmployers = false;
                for (int j = 0; j < employers.size(); j++) {
                    if (employerAssignment[i][j].getValue() == 1) {
                        System.out.print("Employer: " + employers.get(j).getFirstName() + ", ");
                        hasEmployers = true;
                        activity.setEmployees((List.of(employers.get(j))));
                    }
                }

                // Affichage des machines affectées
                boolean hasMachines = false;
                for (int k = 0; k < machines.size(); k++) {
                    if (machineAssignment[i][k].getValue() == 1) {
                        System.out.print("Machine: " + machines.get(k).getName() + ", ");
                        hasMachines = true;
                        activity.setMachine(machines.get(k));
                    }
                }

                // Si aucun employeur ni machine n'est affecté
                if (!hasEmployers && !hasMachines) {
                    System.out.print("Pas d'employeurs et machines attribués, ");
                }

                // Dates de début et de fin
                System.out.println("[Start: " + startDates[i].getValue() + ", End: " + endDates[i].getValue() + "]");

                activity.setPlannedStartDate( new Date (startDates[i].getValue()).toInstant().atZone(ZoneId.systemDefault())
                  .toLocalDateTime());
                    activity.setPlannedEndDate( new Date (endDates[i].getValue()).toInstant().atZone(ZoneId.systemDefault())
                         .toLocalDateTime());
                    results.add(activity);
            }

            // Durée totale d'exécution
            System.out.println("Durée totale d'exécution : " + totalDuration.getValue() + " heures");
            System.out.println(results);

        }

        if (solutionCount == 0) {
            System.out.println("Aucune solution trouvée. Vérifiez les contraintes et les données.");
            return results;
        }

       return results;
    }
}









