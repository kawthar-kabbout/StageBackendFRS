package com.stage.services;


import com.stage.dto.EmployerDTo;
import com.stage.dto.MachineDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.CapabilityMachine;
import com.stage.persistans.Project;
import com.stage.persistans.Skill;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EmployeeMachineValidator {
    private  final  EmployerService employerService;
    private final ActivityService activityService;
    private final MachineService machineService;

    public List<String> employeeMachineValidator(List<Project> projects) {
        List<String> errors = new ArrayList<>();

        List<EmployerDTo> employers = employerService.getALLEmployerDTO();
        List<MachineDTO> machines = machineService.getALlMachineDTO();
        List<Activity> activities = new ArrayList<>();

        for (Project project : projects) {
            activities.addAll(activityService.getActivitiesByProjectId(project.getId()));
        }

        for (Activity activity : activities) {
            Skill requiredSkill = activity.getSkill();
            CapabilityMachine requiredCapabilityMachine = activity.getCapabilityMachine();
            int requiredCount = activity.getEmployersNumber();

            List<EmployerDTo> skilledEmployers = new ArrayList<>();
            if (requiredSkill != null  && requiredCount > 0 && requiredCapabilityMachine == null ) {
                // Trouver les employés qui ont la compétence requise
                for (EmployerDTo emp : employers) {
                    if (emp.getSkills() != null && emp.getSkills().contains(requiredSkill)) {
                        skilledEmployers.add(emp);
                    }
                }
                if (skilledEmployers.size() < requiredCount) {

                    if (skilledEmployers==null || skilledEmployers.isEmpty()) {
                        errors.add(String.format(
                                "Projet '%s' : Activité '%s' nécessite %d employés avec la compétence '%s', mais aucun employer a cette compétence.",
                                activity.getProject().getName(),           // Projet
                                activity.getName(),                        // Activité
                                requiredCount,                             // %d
                                requiredSkill.getName(),                   // Compétence
                                skilledEmployers.size()                    // %d
                        ));

                    }else{
                    errors.add(String.format(
                            "Projet '%s' : Activité '%s' nécessite %d employés avec la compétence '%s',  mais seulement %d disponibles.",
                            activity.getProject().getName(),           // Projet
                            activity.getName(),                        // Activité
                            requiredCount,                             // %d
                            requiredSkill.getName(),                   // Compétence
                            skilledEmployers.size()                    // %d
                    ));

                }
                }



            }


              if (requiredSkill != null && requiredCapabilityMachine != null && requiredCount>0) {

                  // Trouver les employés qui ont la compétence requise
                  for (EmployerDTo emp : employers) {
                      if (emp.getSkills() != null && emp.getSkills().contains(requiredSkill)) {
                          skilledEmployers.add(emp);
                      }
                  }


                // Vérifier que ces employés ont aussi la bonne CapabilityMachine
                int matchingMachineCount = 0;
                for (EmployerDTo emp : skilledEmployers) {
                    if (emp.getCapabilityMachine() != null && emp.getCapabilityMachine().contains(requiredCapabilityMachine)) {
                        matchingMachineCount++;
                    }
                }

                if (matchingMachineCount < requiredCount) {
                    errors.add(String.format(
                            "Projet '%s' : Activité '%s' nécessite %d employés avec la compétence '%s' ET la machine '%s', mais seulement %d en disposent.",
                            activity.getProject().getName(),           // Projet
                            activity.getName(),                        // Activité
                            requiredCount,                             // %d
                            requiredSkill.getName(),                   // Compétence
                            requiredCapabilityMachine != null ? requiredCapabilityMachine.getName() : "null", // Machine
                            matchingMachineCount                       // %d
                    ));
                }

                // Vérifier qu'une machine est bien disponible si la CapabilityMachine est requise
                if (requiredCapabilityMachine != null) {
                    boolean machineFound = false;
                    for (MachineDTO machine : machines) {
                        if (machine.getCapabilityMachine() != null &&
                                machine.getCapabilityMachine().contains(requiredCapabilityMachine)) {
                            machineFound = true;
                            break;
                        }
                    }

                    if (!machineFound) {
                        errors.add(String.format(
                                "Projet '%s' : Aucune machine disponible avec la capacité '%s' pour l’activité '%s'.",
                                activity.getProject().getName(),        // Projet
                                requiredCapabilityMachine.getName(),              // Capacité machine
                                activity.getName()                      // Activité
                        ));
                    }

                }

            }
        }

        return errors;
    }


}
