package com.stage.services;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Project;
import com.stage.persistans.enums.DependencyType;
import com.stage.persistans.DependanceActivity;
import com.stage.persistans.Activity;
import com.stage.repositories.DependanceActivityRepository;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DependanceActivityService {
    private final DependanceActivityRepository dependanceActivityRepository;
    private final ActivityRepository activityRepository;
    private final ActivityService activityService;


    public Optional<DependanceActivity> findById(Long id) {
        return dependanceActivityRepository.findById(id);
    }

    public List<DependanceActivity> findAll() {
        return dependanceActivityRepository.findAll();
    }



    public List<DependencyType> getdependencyTyp(){

        return Arrays.asList(DependencyType.values());
    }


public DependanceActivity findByTargetAndProdesesseur( Activity targetActivity, Activity predecessorActivity) {

        DependanceActivity result =dependanceActivityRepository.findByTargetActivityAndPredecessorActivity(targetActivity,predecessorActivity);

            return result;

}

    public DependanceActivity save(DependanceActivity modelDependanceActivity) {
        if (modelDependanceActivity.getTargetActivity().getId() != modelDependanceActivity.getPredecessorActivity().getId()
                && !existsDependance(modelDependanceActivity.getPredecessorActivity().getId() ,modelDependanceActivity.getTargetActivity().getId())) {
            // Vérifier si targetActivity existe
            if (modelDependanceActivity.getTargetActivity() != null) {
                Optional<Activity> targetActivity = activityRepository.findById(modelDependanceActivity.getTargetActivity().getId());
                if (targetActivity.isEmpty()) {
                    throw new IllegalArgumentException("L'activité cible n'existe pas.");
                }
            }

            // Vérifier si predecessorActivity existe
            if (modelDependanceActivity.getPredecessorActivity() != null) {
                Optional<Activity> predecessorActivity = activityRepository.findById(modelDependanceActivity.getPredecessorActivity().getId());
                if (predecessorActivity.isEmpty()) {
                    throw new IllegalArgumentException("L'activité prédécesseur n'existe pas.");
                }
            }

            // Sauvegarder la dépendance
            return dependanceActivityRepository.save(modelDependanceActivity);
        } else {
            throw new IllegalArgumentException("error meme activity ou bien dependency exist");
        }
    }



    public DependanceActivity update(DependanceActivity modelDependanceActivity) {

        return dependanceActivityRepository.save(modelDependanceActivity);
    }
    public void deleteById(Long id) {
        dependanceActivityRepository.deleteById(id);
    }



    public List<DependanceActivity> getDependenceActivitiesByProjectId(Long projectId) {

        List<Activity>activities=activityService.getActivitiesByProjectId(projectId);
        List<DependanceActivity> dependenceActivities=new ArrayList<>();
        for (Activity activity:activities) {
            List<DependanceActivity>dependanceActivities= dependanceActivityRepository.findByTargetActivity(activity);
            dependenceActivities.addAll(dependanceActivities);
        }
        return dependenceActivities;

    }


    public boolean existsDependance(Long target, Long predecessor ) {
      if(dependanceActivityRepository.countByTargetAndPredecessorNative(target, predecessor) >=1)

      {  System.out.println("Target Activity ID: " + target);
          System.out.println("Predecessor Activity ID: " + predecessor);return true;}
      return false;
    }


    public List<Activity> cloneDependanceActivityRoot(Project newProject , Project oldProject) {
        /// find new roots activities
        List<Activity>rootActivitres=this.activityRepository.findByProjectIdAndParentActivityIsNull(newProject.getId());
        List<Activity> resultActivitres=new ArrayList<>();
        for (Activity rootActivity : rootActivitres) {
            resultActivitres.add(coloneDependanceActivtes(rootActivity ,newProject));
        }
return resultActivitres;
    }

    public Activity coloneDependanceActivtes( Activity newactivity ,Project newproject) {
        Activity oldActivity=this.activityRepository.findById(newactivity.getActivityTemplateId()).get();
            /// old dep list by target oldact

            List<DependanceActivity> dependanceActivities=dependanceActivityRepository.findByTargetActivity(oldActivity);
            for (DependanceActivity oldd : dependanceActivities) {
               Activity newProdecesseur=this.activityService.findByActivityTemplateIdAndProjectId(oldd.getPredecessorActivity().getId(),newproject);
               DependanceActivity newDependanceActivity= DependanceActivity .builder()
                       .dependencyType(oldd.getDependencyType())
                       .targetActivity(newactivity)
                       .predecessorActivity(newProdecesseur)
                       .build();

               dependanceActivityRepository.save(newDependanceActivity);


        }
        List<Activity> childrenActivities = activityRepository.findByParentActivity(newactivity);
        for (Activity child : childrenActivities) {
            coloneDependanceActivtes(child ,newproject);
        }
return newactivity;

    }
}
