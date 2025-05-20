package com.stage.utils;

import com.stage.persistans.*;
import com.stage.persistans.DailyWorkTime;
import com.stage.persistans.enums.*;
import com.stage.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

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
    private final DailyWorkTimeRepository dailyWorkTimeRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
            createModelActivities();


            if(userRepository.count() == 0) {


            User user =   new User();
            user.setEmail("kawthar@test.tn");
            user.setPassword(passwordEncoder.encode("password"));
            user.setIsEnabled(true);
            user.setRole(Role.ADMIN);
            userRepository.save(user);}
    }

    private void createModelActivities() {



        // Création de la liste des horaires par jour
        List<DailyWorkTime> dailyList = new ArrayList<>();

// Jours du lundi au vendredi : 08:00–12:00 et 13:00–17:00
        for (DayOfWeek day : List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            DailyWorkTime dwt = new DailyWorkTime();
            dwt.setDay(day);
            dwt.setMorningStart(LocalTime.of(8, 0));
            dwt.setMorningEnd(LocalTime.of(12, 0));
            dwt.setAfternoonStart(LocalTime.of(13, 0));
            dwt.setAfternoonEnd(LocalTime.of(17, 0));
            dailyList.add(dwt);
        }

// Samedi : 08:00–12:00 et 13:00–15:00
            DailyWorkTime saturday = new DailyWorkTime();
            saturday.setDay(DayOfWeek.SATURDAY);
            saturday.setMorningStart(LocalTime.of(8, 0));
            saturday.setMorningEnd(LocalTime.of(12, 0));
            saturday.setAfternoonStart(LocalTime.of(13, 0));
            saturday.setAfternoonEnd(LocalTime.of(15, 0));
            dailyList.add(saturday);

// Étape 1 : Sauvegarder chaque DailyWorkTime individuellement
        List<DailyWorkTime> savedDailyWorkTimes = dailyWorkTimeRepository.saveAll(dailyList);

// Étape 2 : Créer le WorkTime et lui attacher les horaires sauvegardés
        WorkTime workTime = new WorkTime();
        workTime.setDailyWorkTimes(savedDailyWorkTimes); // maintenant avec ID valide

// Étape 3 : Sauvegarder WorkTime
        workTime = workTimeRepository.save(workTime);

        Skill s1 = null;
        Skill s2 = null;
        Skill s3 = null;
        CapabilityMachine c1 = null;
        CapabilityMachine c2 = null;

        // Création des compétences (skills) si elles n'existent pas déjà
        if (skillRepository.count() == 0) {
            s1 = new Skill("skill 1", "description");
            s2 = new Skill("skill 2", "description");
            s3 = new Skill("skill 3", "description");
            skillRepository.save(s1);
            skillRepository.save(s2);
            skillRepository.save(s3);

            // Création des capacités machines si elles n'existent pas déjà
            if (capabilityMachineRepository.count() == 0) {
                c1 = new CapabilityMachine("capability machine1","description");
                c2 = new CapabilityMachine("capability machine2","description");


                capabilityMachineRepository.save(c1);
                capabilityMachineRepository.save(c2);
            }

            // Création des employeurs (employers)
            Employer employer1 = null;
            Employer employer2 = null;
            Employer employer3 = null;
            Employer employer4 = null;
            Employer employer5 = null;

            if (employerRepository.count() == 0 && s1 != null && s2 != null) {
                employer1 = new Employer();
                employer1.setFirstName("employer1");
                employer1.setLastName("employer1");
                employer1.setPhone("1234555567890");
                employer1.setAddress("123 Main St");
                employer1.setGrade("Senior");
                employer1.setSkills(List.of(s1));
                employer1.setWorkTime(workTime);
                employer1.setCapabilityMachine(List.of(c1));
                employer1.setMatricule(100);

                employer2 = new Employer();
                employer2.setFirstName("employer2");
                employer2.setLastName("employer2");
                employer2.setPhone("09876543244441");
                employer2.setAddress("456 Elm St");
                employer2.setGrade("Junior");
                employer2.setSkills(List.of(s2,s1));
                employer2.setWorkTime(workTime);
                employer2.setCapabilityMachine(List.of(c2,c1));
                employer2.setMatricule(101);

                employer3 = new Employer();
                employer3.setFirstName("employer3");
                employer3.setLastName("employer3");
                employer3.setPhone("0987654321");
                employer3.setAddress("456 Elm St");
                employer3.setGrade("Junior");
                employer3.setSkills(List.of(s2));
                employer3.setWorkTime(workTime);
                employer3.setCapabilityMachine(List.of(c2));
                employer3.setMatricule(102);

                
                employer4 = new Employer();
                employer4.setFirstName("employer4");
                employer4.setLastName("employer4");
                employer4.setPhone("0987654326584981");
                employer4.setAddress("456 Elm St");
                employer4.setGrade("Junior");
                employer4.setSkills(List.of(s2,s1));
                employer4.setWorkTime(workTime);
                employer4.setCapabilityMachine(List.of(c2,c1));
                employer4.setMatricule(103);

                employer5= new Employer();
                employer5.setFirstName("employer5");
                employer5.setLastName("employer5");
                employer5.setPhone("098765432658");
                employer5.setAddress("456 Elm St");
                employer5.setGrade("Junior");
                employer5.setSkills(List.of(s3));
                employer5.setWorkTime(workTime);
                employer5.setMatricule(104);

                employerRepository.save(employer1);
                employerRepository.save(employer2);
                employerRepository.save(employer3);
                employerRepository.save(employer4);
                employerRepository.save(employer5);
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
            p1 = new Project("Cuisine");
            p2 = new Project("Dressing");
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
        Activity activity8 =null;
        Activity activity9 =null;
        Activity activity10 = null;
        Activity activity11 = null;
        Activity activity12 = null;

        if (modeleActivityRepository.count() == 0) {
            if (p1 != null && p2 != null && s1 != null && s2 != null) {

                activity1 = new Activity("Activité 1", ActivityType.EXTERNE, p1, c1, s1,90,2);

                activity8 =new Activity("Production", ActivityType.INTERNE,p2,0);
                activity2 = new Activity( "Découpe des Panneaux et Pièces", Statut.Pending,  ActivityType.INTERNE, activity8, p2, s1, c1, 2, 2  );
                activity3 = new Activity( "Usinage", Statut.Pending,  ActivityType.INTERNE, activity8, p2, s2,c2, 5, 2   );
                activity4 = new Activity( "Assemblage des Composants", Statut.Pending,  ActivityType.INTERNE, activity8, p2, s2,null, 3, 1   );
                activity5 = new Activity( "Penturation", Statut.Pending,  ActivityType.SOUS_TRAITANCE, activity8, p2, null,null, 48, 0 );
                activity6 =new Activity( "Finition", Statut.Pending,  ActivityType.INTERNE, activity8, p2, s2,null, 2, 2   );
                activity7 = new Activity( "Contrôle Qualité des Produits Finis", Statut.Pending,  ActivityType.INTERNE, activity8, p2, s2,null, 1, 2   );

                activity9 =new Activity("Installation", ActivityType.EXTERNE,p2,0);

                activity10 = new Activity( "Préparation du Site d'Installation", Statut.Pending, ActivityType.EXTERNE,  activity9, p2, s3, null, 3, 1 );
                activity11= new Activity( "Montage sur Place", Statut.Pending, ActivityType.EXTERNE,  activity9, p2, s3,null, 3, 1   );
                activity12 = new Activity( "Ajustements et Vérifications Finales", Statut.Pending, ActivityType.EXTERNE,  activity9, p2, s3,null, 1, 1  );


                modeleActivityRepository.save(activity8);
                modeleActivityRepository.save(activity1);
                modeleActivityRepository.save(activity2);
                modeleActivityRepository.save(activity3);
                modeleActivityRepository.save(activity4);
                modeleActivityRepository.save(activity5);
                modeleActivityRepository.save(activity6);
                modeleActivityRepository.save(activity7);
                modeleActivityRepository.save(activity9);
                modeleActivityRepository.save(activity10);
                modeleActivityRepository.save(activity11);
                modeleActivityRepository.save(activity12);

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

            if (activity4.getId() != null && activity5.getId() != null) {
                DependanceActivity d3 = new DependanceActivity(activity5,activity4, DependencyType.SS,1);
                dependanceActivityRepository.save(d3);
            }

           if (activity6.getId() != null && activity5.getId() != null) {
               DependanceActivity d3 = new DependanceActivity(activity6,activity5, DependencyType.SS,1);
               dependanceActivityRepository.save(d3);
           }

           if (activity7.getId() != null && activity6.getId() != null) {
               DependanceActivity d4 = new DependanceActivity(activity7,activity6, DependencyType.SS,1);
               dependanceActivityRepository.save(d4);
           }

           if (activity10.getId() != null && activity7.getId() != null) {
               DependanceActivity d5 = new DependanceActivity(activity10,activity7, DependencyType.FS,1);
               dependanceActivityRepository.save(d5);
           }
           if (activity11.getId() != null && activity10.getId() != null) {
               DependanceActivity d6 = new DependanceActivity(activity11,activity10, DependencyType.FS,1);
               dependanceActivityRepository.save(d6);
           }
           if (activity12.getId() != null && activity11.getId() != null) {
               DependanceActivity d7 = new DependanceActivity(activity12,activity11, DependencyType.FS,1);
               dependanceActivityRepository.save(d7);
           }
        }
    }

    }





