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
        List<DependanceActivity> deps = dependanceActivityRepository.findAll();
        List<DependanceActivity> result = new ArrayList<>();
        for (DependanceActivity dep : deps) {
            if (dep.getArchived()==0)
                result.add(dep);
        }

        return result;
    }



    public List<DependencyType> getdependencyTyp(){

        return Arrays.asList(DependencyType.values());
    }


public DependanceActivity findByTargetAndProdesesseur( Activity targetActivity, Activity predecessorActivity) {

        DependanceActivity result =dependanceActivityRepository.findByTargetActivityAndPredecessorActivity(targetActivity,predecessorActivity);
        if (result.getArchived()==1)
           return  null;

        return result;

}

    public DependanceActivity save(DependanceActivity modelDependanceActivity) {
        if (modelDependanceActivity.getTargetActivity().getId() != modelDependanceActivity.getPredecessorActivity().getId()
                && !existsDependance(
                        modelDependanceActivity.getPredecessorActivity().getId()
                ,modelDependanceActivity.getTargetActivity().getId())  /// inverce
        && !existsDependance( modelDependanceActivity.getTargetActivity().getId() ,
                modelDependanceActivity.getPredecessorActivity().getId()
                ))
        {
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
            return null;
        }
    }



    public DependanceActivity update(Long id, DependanceActivity dependanceActivity) {
        Long predecessorId = dependanceActivity.getPredecessorActivity().getId();
        Long targetId = dependanceActivity.getTargetActivity().getId();

        // Vérifie si une dépendance identique existe déjà (même prédécesseur + même cible)
    DependanceActivity existingSameDepOpt =
                dependanceActivityRepository.
                        findByTargetActivityAndPredecessorActivity(dependanceActivity.getPredecessorActivity()
                                , dependanceActivity.getTargetActivity());

        if (existingSameDepOpt !=null && !existingSameDepOpt.getId().equals(id)) {
            // Une autre dépendance (différente de celle qu'on veut modifier) existe déjà
            return null;
        }

        // Recherche de la dépendance à modifier
        Optional<DependanceActivity> optionalDep = dependanceActivityRepository.findById(id);
        if (optionalDep.isPresent()) {
            DependanceActivity existingDep = optionalDep.get();
            existingDep.setTargetActivity(dependanceActivity.getTargetActivity());
            existingDep.setPredecessorActivity(dependanceActivity.getPredecessorActivity());
            existingDep.setDelay(dependanceActivity.getDelay());
            existingDep.setDependencyType(dependanceActivity.getDependencyType());
            existingDep.setArchived(dependanceActivity.getArchived());

            return dependanceActivityRepository.save(existingDep);
        }

        return null; // Dépendance à modifier non trouvée
    }

    public void deleteByTagetActivity(Activity targetActivity) {
      List<DependanceActivity> deps=  dependanceActivityRepository.findByTargetActivity(targetActivity);
      for (DependanceActivity dep : deps) {
          dep.setArchived(1);
      }
    }



    public List<DependanceActivity> getDependenceActivitiesByProjectId(Long projectId) {
        List<Activity> activities = activityService.getActivitiesByProjectId(projectId);
        List<DependanceActivity> dependenceActivities = new ArrayList<>();

        for (Activity activity : activities) {
            List<DependanceActivity> depOfActivity = dependanceActivityRepository.findByTargetActivity(activity);

            // Ajouter seulement les dépendances non archivées (archive == 0)
            for (DependanceActivity dep : depOfActivity) {
                if (dep.getArchived() == 0 && dep.getPredecessorActivity().getArchived()==0) {
                    dependenceActivities.add(dep);
                }
            }


        }

        return dependenceActivities;
    }


    public boolean existsDependance(Long target, Long predecessor ) {
      if(dependanceActivityRepository.countByTargetAndPredecessorNative(target, predecessor) >=1)

      {  System.out.println("");
          System.out.println("");
          System.out.println("Target Activity ID: " + target);
          System.out.println("Predecessor Activity ID: " + predecessor);
          return true;
      }
      return false;
    }


    public List<Activity> cloneDependanceActivityRoot(Project newProject , Project oldProject) {
        /// find new roots activities
        /// /activity non archiver
        List<Activity>rootActivitres=this.activityService.findByProjectIdAndParentActivityIsNull(newProject.getId());
        List<Activity> resultActivitres=new ArrayList<>();
        for (Activity rootActivity : rootActivitres) {
            if (rootActivity.getArchived()==0){
                resultActivitres.add(coloneDependanceActivtes(rootActivity ,newProject));
            }
        }
return resultActivitres;
    }

    public Activity coloneDependanceActivtes( Activity newactivity ,Project newproject) {
        Activity oldActivity=this.activityRepository.findById(newactivity.getActivityTemplateId()).get();
            /// old dep list by target oldact

            List<DependanceActivity> dependanceActivities=this.findByTargetActivity(oldActivity);
            for (DependanceActivity oldd : dependanceActivities) {
               Activity newProdecesseur=this.activityService.findByActivityTemplateIdAndProjectId(oldd.getPredecessorActivity().getId(),newproject);
               DependanceActivity newDependanceActivity= DependanceActivity .builder()
                       .dependencyType(oldd.getDependencyType())
                       .targetActivity(newactivity)
                       .predecessorActivity(newProdecesseur)
                       .delay(oldd.getDelay())
                       .build();

               dependanceActivityRepository.save(newDependanceActivity);


        }
        List<Activity> childrenActivities = activityRepository.findByParentActivity(newactivity);
        for (Activity child : childrenActivities) {
           if (child.getArchived()==0){
               coloneDependanceActivtes(child ,newproject);
           }
        }
return newactivity;

    }

    public List<DependanceActivity> findByTargetActivity(Activity targetActivity) {
        List<DependanceActivity> deps=  dependanceActivityRepository.findByTargetActivity(targetActivity);

        List<DependanceActivity> result = new ArrayList<>();
        for (DependanceActivity dep : deps) {
            if (dep.getArchived()==0)
                result.add(dep);
        }
        return result;
    }

    public Boolean deleteById(DependanceActivity dep) {
        DependanceActivity dependance = dependanceActivityRepository.findById(dep.getId()).get();
        if (dependance != null) {
            dependance.setArchived(1);
            dependanceActivityRepository.save(dependance);
            return true;
        }
        return false;
    }

    public Boolean deleteByTargetActivity(Activity targetActivity) {
        Activity activity = activityRepository.findById(targetActivity.getId()).get();
        if (activity != null) {
            List<DependanceActivity> deps= this.findByTargetActivity(targetActivity);
            for (DependanceActivity dep : deps) {
                this.deleteById(dep);
            }
        return true;
        } return  false;

    }
}
