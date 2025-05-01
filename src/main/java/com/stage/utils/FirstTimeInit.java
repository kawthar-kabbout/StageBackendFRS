package com.stage.utils;

import com.stage.persistans.*;
import com.stage.persistans.enums.DependencyType;
import com.stage.persistans.enums.MachineType;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import com.stage.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirstTimeInit implements CommandLineRunner {

    private final ActivityRepository modeleActivityRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final DependanceActivityRepository dependanceActivityRepository;
    private final EmployerRepository employerRepository;
    private final MachineRepository machineRepository;
    private final  CapabilityMachineRepository capabilityMachineRepository;
    private final WorkTimeRepository workTimeRepository;

    @Override
    public void run(String... args) throws Exception {
            createModelActivities();
    }

    private void createModelActivities() {





        WorkTime workTime = new WorkTime();

        // Configuration des horaires de travail en semaine
        workTime.setMorningSessionStart(LocalTime.of(8, 0));   // Matin : 08:00
        workTime.setMorningSessionEnd(LocalTime.of(12, 0));     // Matin : 12:00
        workTime.setAfternoonSessionStart(LocalTime.of(13, 0)); // Après-midi : 13:00
        workTime.setAfternoonSessionEnd(LocalTime.of(17, 0));   // Après-midi : 17:00

        // Configuration d'un jour de week-end travaillé (exemple : samedi)
        workTime.setWeekendDay("Samedi");
        workTime.setWeekendWorkTimeStart(LocalTime.of(9, 0));    // Samedi : 09:00
        workTime.setWeekendWorkTimeEnd(LocalTime.of(13, 0));     // Samedi : 13:00

        // Liste des jours travaillés (ex: Lundi à Vendredi)
        workTime.setWorkingDays(Arrays.asList("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi"));

        // Liste des jours non travaillés (ex: Dimanche + Samedi si pas de travail le week-end)
        workTime.setNoWorkingDays(Arrays.asList("Dimanche"));
        workTimeRepository.save(workTime);



        Skill s1 = null;
        Skill s2 = null;
        CapabilityMachine c1 = null;
        CapabilityMachine c2 = null;

        // Création des compétences (skills) si elles n'existent pas déjà
        if (skillRepository.count() == 0) {
            s1 = new Skill("skill 1", "description");
            s2 = new Skill("skill 2", "description");
            skillRepository.save(s1);
            skillRepository.save(s2);

            // Création des capacités machines si elles n'existent pas déjà
            if (capabilityMachineRepository.count() == 0) {
                c1 = new CapabilityMachine();
                c2 = new CapabilityMachine();

                c1.setName("capability machine1");
                c2.setName("capability machine2");

                capabilityMachineRepository.save(c1);
                capabilityMachineRepository.save(c2);
            }

            // Création des employeurs (employers)
            Employer employer1 = null;
            Employer employer2 = null;
            Employer employer3 = null;
            Employer employer4 = null;

            if (employerRepository.count() == 0 && s1 != null && s2 != null) {
                employer1 = new Employer();
                employer1.setFirstName("employer1");
                employer1.setLastName("employer1");
                employer1.setPhone("1234555567890");
                employer1.setAddress("123 Main St");
                employer1.setGrade("Senior");
                employer1.setSkills(List.of(s1));
                employer1.setCapabilityMachine(List.of(c1));

                employer2 = new Employer();
                employer2.setFirstName("employer2");
                employer2.setLastName("employer2");
                employer2.setPhone("09876543244441");
                employer2.setAddress("456 Elm St");
                employer2.setGrade("Junior");
                employer2.setSkills(List.of(s2,s1));
                employer2.setCapabilityMachine(List.of(c2,c1));


                employer3 = new Employer();
                employer3.setFirstName("employer3");
                employer3.setLastName("employer3");
                employer3.setPhone("0987654321");
                employer3.setAddress("456 Elm St");
                employer3.setGrade("Junior");
                employer3.setSkills(List.of(s2));
                employer3.setCapabilityMachine(List.of(c2));

                employer4 = new Employer();
                employer4.setFirstName("employer4");
                employer4.setLastName("employer4");
                employer4.setPhone("0987654326584981");
                employer4.setAddress("456 Elm St");
                employer4.setGrade("Junior");
                employer4.setSkills(List.of(s2,s1));
                employer4.setCapabilityMachine(List.of(c2,c1));
                employerRepository.save(employer1);
                employerRepository.save(employer2);
                employerRepository.save(employer3);
                employerRepository.save(employer4);
            }

            // Création des machines
            Machine machine1 = null;
            Machine machine2 = null;
            Machine machine11 = null;

            if (machineRepository.count() == 0 && s1 != null && s2 != null) {
                machine1 = new Machine();
                machine1.setSerialNumber("MACHINE-001");
                machine1.setName("Machine 1");
                machine1.setMachineType(MachineType.Automatique);
                machine1.setMarque("Brand X");
                machine1.setStatus(true);
                machine1.setCapabilityMachines(List.of(c1));


                machine11 = new Machine();
                machine11.setSerialNumber("MACHINE-001555");
                machine11.setName("Machine 11");
                machine11.setMachineType(MachineType.Automatique);
                machine11.setMarque("Brand X");
                machine11.setStatus(true);
                machine11.setCapabilityMachines(List.of(c1));

                machine2 = new Machine();
                machine2.setSerialNumber("MACHINE-002");
                machine2.setName("Machine 2");
                machine2.setMachineType(MachineType.Automatique);
                machine2.setMarque("Brand Y");
                machine2.setStatus(false);
                machine2.setCapabilityMachines(List.of(c2));

                machineRepository.save(machine1);
                machineRepository.save(machine2);
                machineRepository.save(machine11);
            }
        }

        // Création des projets (projects)
        Project p1 = null;
        Project p2 = null;

        if (projectRepository.count() == 0) {
            p1 = new Project("P1");
            p2 = new Project("P2");
            projectRepository.save(p1);
            projectRepository.save(p2);
        }

        // Création des activités (activities)
        Activity activity1 = null;
        Activity activity2 = null;
        Activity activity3 = null;
        Activity activity4 = null;
        Activity activity5 = null;
        Activity activity6 = null;
        Activity activity7 = null;

        if (modeleActivityRepository.count() == 0) {
            if (p1 != null && p2 != null && s1 != null && s2 != null) {
                activity1 = new Activity("Activité 1", ActivityType.EXTERNE, p1, c1, s1,90,1);
                activity2 = new Activity("Activité 2", ActivityType.EXTERNE, p2, c1, s1,50,3);
                activity3 = new Activity("Activité 3", ActivityType.EXTERNE, p2, c2, s2,30,1);
                activity4 = new Activity("Activité 4", ActivityType.EXTERNE, p2, null,s2,40,1);
                activity5 = new Activity("Activité 5", ActivityType.SOUS_TRAITANCE, p2, null,null,10,0);
                activity6 = new Activity("Activité 6", ActivityType.SOUS_TRAITANCE, p2, null,null,6,0);
                activity7 = new Activity("Activité 7", ActivityType.SOUS_TRAITANCE, p2, null,null,5,0);

                modeleActivityRepository.save(activity1);
                modeleActivityRepository.save(activity2);
                modeleActivityRepository.save(activity3);
                modeleActivityRepository.save(activity4);
                modeleActivityRepository.save(activity5);
                modeleActivityRepository.save(activity6);
                modeleActivityRepository.save(activity7);
            }
        }


       if (activity1 != null && activity2 != null && activity3 != null && activity4 != null && activity5 != null) {

            if (activity3.getId() != null && activity2.getId() != null) {
                DependanceActivity d1 = new DependanceActivity(activity3, activity2, DependencyType.FS,2);
                dependanceActivityRepository.save(d1);
            }

            if (activity3.getId() != null && activity4.getId() != null) {
                DependanceActivity d2 = new DependanceActivity(activity4,activity3, DependencyType.FS,1);
                dependanceActivityRepository.save(d2);
            }

            if (activity3.getId() != null && activity5.getId() != null) {
                DependanceActivity d3 = new DependanceActivity(activity5,activity3, DependencyType.SS,1);
                dependanceActivityRepository.save(d3);
            }
           if (activity6.getId() != null && activity5.getId() != null) {
               DependanceActivity d3 = new DependanceActivity(activity6,activity5, DependencyType.SS,1);
               dependanceActivityRepository.save(d3);
           }

        }
    }

    }





