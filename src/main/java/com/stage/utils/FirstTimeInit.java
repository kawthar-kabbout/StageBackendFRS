package com.stage.utils;

import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import com.stage.persistans.Skill;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.ProjectRepository;
import com.stage.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FirstTimeInit implements CommandLineRunner {

    private final ActivityRepository modeleActivityRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;

    @Override
    public void run(String... args) throws Exception {
            createModelActivities();
    }

    private void createModelActivities() {

        Skill s1 = null;
        Skill s2 = null;


        if (skillRepository.count() == 0) {
            s1 = new Skill("skill 1", "description");
            s2 = new Skill("skill 2", "description");
            skillRepository.save(s1);
            skillRepository.save(s2);
        }

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
            if (p1 != null && p2 != null && s1 != null && s2 != null) {
                activity1 = new Activity("Activité 1", ActivityType.EXTERNE, p1, List.of(s1));
                activity2 = new Activity("Activité 2", ActivityType.EXTERNE, p2, List.of(s2));


                modeleActivityRepository.save(activity1);
                modeleActivityRepository.save(activity2);
            }
        }
    }

}
