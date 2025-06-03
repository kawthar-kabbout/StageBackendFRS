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
            user.setPassword(passwordEncoder.encode("1234"));
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

        Skill decoupe = null;
        Skill usinage = null;
        Skill assemblage = null;
        Skill finition = null;
        Skill contrôle = null;
        Skill pose = null;
        Skill skill1 = null;
        Skill skill2 = null;

        CapabilityMachine c1 = null;
        CapabilityMachine c2 = null;
        CapabilityMachine c3 = null;
        CapabilityMachine c4 = null;


        if (skillRepository.count() == 0) {
            decoupe = new Skill("Découpe des Panneaux", "Découpe des Panneaux du Bois");
            usinage = new Skill("Usinage", "Usinage (Perçage, Rainurage, etc.)");
            assemblage = new Skill("Assemblage", "Assemblage des caissons");
            finition = new Skill("Finition", "Finition");
            contrôle = new Skill("Contrôle", "Contrôle Qualité des Produits Finis");
            pose = new Skill("Installation", "Installation");
            skill1 = new Skill("Skill 1", "Skill 1");
            skill2 = new Skill("Skill 2", "Skill 2");

            skillRepository.save(decoupe);
            skillRepository.save(usinage);
            skillRepository.save(assemblage);
            skillRepository.save(finition);
            skillRepository.save(contrôle);
            skillRepository.save(pose);
            skillRepository.save(skill1);
            skillRepository.save(skill2);
            // Création des capacités machines si elles n'existent pas déjà
            if (capabilityMachineRepository.count() == 0) {
                c1 = new CapabilityMachine("Découpe des Panneaux","Découpe des Panneaux");
                c2 = new CapabilityMachine("Rainures","Rainures");
                c3 = new CapabilityMachine("Plaqueuse de chant","Plaqueuse de chant");
                c4  =new CapabilityMachine("CapabilityMachine1","CapabilityMachine1");


                capabilityMachineRepository.save(c1);
                capabilityMachineRepository.save(c2);
                capabilityMachineRepository.save(c3);
                capabilityMachineRepository.save(c4);
            }

            // Création des employeurs (employers)
            Employer employer1 = null;
            Employer employer2 = null;
            Employer employer3 = null;
            Employer employer4 = null;
            Employer employer5 = null;

            if (employerRepository.count() == 0 && decoupe != null && usinage != null) {
                employer1 = new Employer();
                employer1.setFirstName("Kabbout");
                employer1.setLastName("Ayman");
                employer1.setPhone("27049997");
                employer1.setAddress("123 Main St");
                employer1.setGrade("Junior");
                employer1.setSkills(List.of(decoupe,usinage,assemblage));
                employer1.setWorkTime(workTime);
                employer1.setCapabilityMachine(List.of(c1,c3));
                employer1.setMatricule("EMP-1020");
                employer1.setEmail("employer1@test.tn");

                employer2 = new Employer();
                employer2.setFirstName("Kabbout");
                employer2.setLastName("Mohamed");
                employer2.setPhone("23416137");
                employer2.setAddress("456 Elm St");
                employer2.setGrade("Junior");
                employer2.setSkills(List.of(decoupe,usinage,assemblage,contrôle,finition));
                employer2.setWorkTime(workTime);
                employer2.setCapabilityMachine(List.of(c2,c1,c3));
                employer2.setMatricule("EMP-1021");
                employer2.setEmail("employer2@test.tn");

                employer3 = new Employer();
                employer3.setFirstName("Kabbout");
                employer3.setLastName("Mohamed");
                employer3.setPhone("96624391");
                employer3.setAddress("456 Elm St");
                employer3.setGrade("Junior");
                employer3.setSkills(List.of(contrôle,finition));
                employer3.setWorkTime(workTime);

                employer3.setMatricule("EMP-1022");
                employer3.setEmail("employer3@test.tn");

                
                employer4 = new Employer();
                employer4.setFirstName("Hmida");
                employer4.setLastName("Jihed");
                employer4.setPhone("23558778");
                employer4.setAddress("456 Elm St");
                employer4.setGrade("Junior");
                employer4.setSkills(List.of(pose));
                employer4.setWorkTime(workTime);

                employer4.setMatricule("EMP-1023");
                employer4.setEmail("employer4@test.tn");

                employer5= new Employer();
                employer5.setFirstName("Chouchen");
                employer5.setLastName("Samih");
                employer5.setPhone("21369258");
                employer5.setAddress("456 Elm St");
                employer5.setGrade("Junior");
                employer5.setSkills(List.of(pose));
                employer5.setWorkTime(workTime);
                employer5.setMatricule("EMP-1024");
                employer5.setEmail("employer5@test.tn");

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

            if (machineRepository.count() == 0 && decoupe != null && pose != null) {
                machine1 = new Machine();

                machine1.setSerialNumber("MACHINE-001");
                machine1.setName("Sia panneau");
                machine1.setMachineType(MachineType.Automatique);
                machine1.setMarque("Brand X");
                machine1.setStatus(true);
                machine1.setCapabilityMachines(List.of(c1,c2));


                machine11 = new Machine();
                machine11.setSerialNumber("MACHINE-001555");
                machine11.setName("Plaqueuse de chant automatique");
                machine11.setMachineType(MachineType.Automatique);
                machine11.setMarque("Brand Y");
                machine11.setStatus(true);
                machine11.setCapabilityMachines(List.of(c3));

                machine2 = new Machine();
                machine2.setSerialNumber("MACHINE-002");
                machine2.setName("CNC");
                machine2.setMachineType(MachineType.Automatique);
                machine2.setMarque("Brand Y");
                machine2.setStatus(true);
                machine2.setCapabilityMachines(List.of(c1,c2));

                machineRepository.save(machine1);
                machineRepository.save(machine2);
                machineRepository.save(machine11);
            }
        }

        // Création des projets (projects)
        Project p1 = null;
        Project p2 = null;
        Project p3 = null;
        Project p4 = null;
        Project p5 = null;

        if (projectRepository.count() == 0) {
            p1 = new Project("Dressing");
            p2 = new Project("Meubles cuisine");
            p3 = new Project("Buffet");
            p4 = new Project("Table à manger");
            p5 = new Project("Meuble TV");

            projectRepository.save(p1);
            projectRepository.save(p2);
            projectRepository.save(p3);
            projectRepository.save(p4);
            projectRepository.save(p5);
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
        Activity activity13 = null;
        Activity activity14 = null;



        Activity activity15 = null;
        Activity activity16 = null;
        Activity activity17 = null;
        Activity activity18 = null;
        Activity activity19 = null;
        Activity activity20 = null;



        if (modeleActivityRepository.count() == 0) {
            if (p1 != null && p2 != null && decoupe != null && pose != null) {

                activity1 = new Activity("Activité 1", ActivityType.EXTERNE, p1, c1, decoupe,4,2);
                activity19 = new Activity("Activité 19", ActivityType.EXTERNE, p1, c3, usinage,3,2);

                activity8 =new Activity("Production", null,ActivityType.INTERNE,p2,0);

                activity2 = new Activity( "Découpe des Panneaux",Statut.Pending,  ActivityType.INTERNE, activity8, p2, decoupe, c1, 2, 2  );

                activity3 =new Activity("Usinage", activity8,ActivityType.INTERNE,p2,0);

                activity4 = new Activity( "Perçage", Statut.Pending,  ActivityType.INTERNE, activity3, p2, usinage,null, 1, 1   );
                activity5 = new Activity( "Couvre chant", Statut.Pending,  ActivityType.INTERNE, activity3, p2, usinage,c3, 1, 1  );
                activity6 = new Activity( "Rainurage", Statut.Pending,  ActivityType.INTERNE, activity3, p2, usinage,c2, 1, 1  );

                activity7 = new Activity( "Assemblage des caissons", Statut.Pending,  ActivityType.INTERNE, activity8, p2, assemblage,null, 3, 2   );
                activity9 = new Activity( "Application de peinture sur les façades", Statut.Pending,  ActivityType.SOUS_TRAITANCE, activity8, p2, null,null, 12, 0 );
                activity10 =new Activity( "Finition", Statut.Pending,  ActivityType.INTERNE, activity8, p2, finition,null, 2, 2   );
                activity11 = new Activity( "Contrôle", Statut.Pending,  ActivityType.INTERNE, activity8, p2, contrôle,null, 1, 2   );

                activity12 =new Activity("Livraison",null, ActivityType.EXTERNE,p2,0);

                activity13 = new Activity( "Pose d'élément haut", Statut.Pending, ActivityType.EXTERNE,  activity12, p2, pose, null, 3, 2 );
                activity14= new Activity( "Pose d'élément bas", Statut.Pending, ActivityType.EXTERNE,  activity12, p2, pose,null, 3, 2   );



                activity15= new Activity( "activity15", Statut.Pending, ActivityType.EXTERNE,  null, p3, skill1,c4, 3, 2   );

                activity16= new Activity( "activity16", Statut.Pending, ActivityType.EXTERNE,  null, p3, skill2,c4, 3, 2   );
                activity17= new Activity( "activity17", Statut.Pending, ActivityType.EXTERNE,  null, p3, skill1,c4, 3, 2   );
                activity18= new Activity( "activity18", Statut.Pending, ActivityType.EXTERNE,  null, p3, skill2,c4, 3, 2   );

                activity20= new Activity( "activity20", Statut.Pending, ActivityType.SOUS_TRAITANCE,  null, p3, null,null, 3, 2   );




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
                modeleActivityRepository.save(activity13);
                modeleActivityRepository.save(activity14);
                modeleActivityRepository.save(activity15);
                modeleActivityRepository.save(activity16);
                modeleActivityRepository.save(activity17);
                modeleActivityRepository.save(activity18);
                modeleActivityRepository.save(activity19);
                modeleActivityRepository.save(activity20);


            }
        }


       if (activity1 != null && activity2 != null && activity3 != null && activity4 != null && activity5 != null) {

            if (activity4.getId() != null && activity2.getId() != null) {
                DependanceActivity d1 = new DependanceActivity(activity4, activity2, DependencyType.FS,2);
                dependanceActivityRepository.save(d1);
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
               DependanceActivity d4 = new DependanceActivity(activity7,activity6, DependencyType.FS,1);
               dependanceActivityRepository.save(d4);
           }

           if (activity9.getId() != null && activity7.getId() != null) {
               DependanceActivity d5 = new DependanceActivity(activity9,activity7, DependencyType.FS,1);
               dependanceActivityRepository.save(d5);
           }

           if (activity10.getId() != null && activity9.getId() != null) {
               DependanceActivity d5 = new DependanceActivity(activity10,activity9, DependencyType.FS,1);
               dependanceActivityRepository.save(d5);
           }


           if (activity10.getId() != null && activity11.getId() != null) {
               DependanceActivity d6 = new DependanceActivity(activity11,activity10, DependencyType.FS,1);
               dependanceActivityRepository.save(d6);
           }

           if (activity13.getId() != null && activity11.getId() != null) {
               DependanceActivity d7 = new DependanceActivity(activity13,activity11, DependencyType.FS,2);
               dependanceActivityRepository.save(d7);
           }


           if (activity14.getId() != null && activity13.getId() != null) {
               DependanceActivity d9 = new DependanceActivity(activity14,activity13, DependencyType.FS,24);
               dependanceActivityRepository.save(d9);
           }

           if (activity19.getId() != null && activity1.getId() != null){
               DependanceActivity d8 = new DependanceActivity(activity19,activity1, DependencyType.FS,2);
               dependanceActivityRepository.save(d8);
           }
        }
    }

    }





