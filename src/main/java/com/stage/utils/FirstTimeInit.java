package com.stage.utils;

import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class FirstTimeInit implements CommandLineRunner {

    private final ActivityRepository modeleActivityRepository;
    private final ProjectRepository projectRepository;

    @Override
    public void run(String... args) throws Exception {
            createModelActivities();
    }

    private void createModelActivities() {
        Activity activity1 = null;
        Activity activity2 = null;

        Project p1 = null;
        Project p2 = null;

        if (projectRepository.count() == 0) {
            p1 = new Project("P1");
            p2 = new Project("P2");

           projectRepository.save(p1);
           projectRepository.save(p2);

        }


        if (modeleActivityRepository.count() == 0) {
            activity1 = new Activity(
                    "Activité 1",
                    StatutActivity.Cancel,
                    ActivityType.EXTERNE,
                    p1,
                    LocalDateTime.of(2025, 3, 1, 9, 0), // Date de début prévue
                    LocalDateTime.of(2025, 3, 1, 17, 0) // Date de fin prévue
            );

            activity2 = new Activity(
                    "Activité 2",
                    StatutActivity.Pending,
                    ActivityType.EXTERNE,
p2,
                    LocalDateTime.of(2025, 3, 2, 9, 0), // Date de début prévue
                    LocalDateTime.of(2025, 3, 2, 17, 0) // Date de fin prévue
            );


            // Sauvegarde des activités en base de données
            modeleActivityRepository.save(activity1);
            modeleActivityRepository.save(activity2);
        }



    }
}
