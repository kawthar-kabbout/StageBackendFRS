package com.stage.utils;

import com.stage.persistans.Activity;
import com.stage.persistans.DependanceActivity;
import com.stage.persistans.Project;
import com.stage.persistans.Skill;
import com.stage.persistans.enums.DependencyType;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.DependanceActivityRepository;
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
    private final DependanceActivityRepository dependanceActivityRepository;


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
        Activity activity3 = null;
        Activity activity4 = null;
        Activity activity5 = null;
        Activity activity6 = null;
        Activity activity7 = null;
        Activity activity8 = null;
        Activity activity9 = null;

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
                activity3 = new Activity("Activité 3", ActivityType.EXTERNE, p2, List.of(s2));
                activity4 = new Activity("Activité 4", ActivityType.EXTERNE, p2, List.of(s2));

                activity5 = new Activity("Activité 5", ActivityType.EXTERNE, p2, List.of(s2));
                activity6 = new Activity("Activité 6", ActivityType.EXTERNE, p2, List.of(s2));
                activity7 = new Activity("Activité 7", ActivityType.EXTERNE, p2, List.of(s2));
                modeleActivityRepository.save(activity1);
                modeleActivityRepository.save(activity2);
                modeleActivityRepository.save(activity3);
                modeleActivityRepository.save(activity4);
                modeleActivityRepository.save(activity5);
                modeleActivityRepository.save(activity6);
                modeleActivityRepository.save(activity7);

               /* DependanceActivity d1=new DependanceActivity(activity3,activity2, DependencyType.FF);
                DependanceActivity d2=new DependanceActivity(activity3,activity4, DependencyType.FS);
                DependanceActivity d3=new DependanceActivity(activity3,activity5, DependencyType.SS);
                dependanceActivityRepository.save(d1);
                dependanceActivityRepository.save(d2);
                dependanceActivityRepository.save(d3);*/
            }
        }
    }

}
