package com.stage.services;

import com.stage.dto.ActivityDTO;
import com.stage.dto.DependanceActivityDTO;
import com.stage.persistans.enums.DependencyType;
import com.stage.persistans.DependanceActivity;
import com.stage.persistans.Activity;
import com.stage.repositories.DependanceActivityRepository;
import com.stage.repositories.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DependanceActivityService {
    private final DependanceActivityRepository dependanceActivityRepository;
    private final ActivityRepository activityRepository;

    private DependanceActivityService dependanceActivityService;

    public Optional<DependanceActivity> findById(Long id) {
        return dependanceActivityRepository.findById(id);
    }

    public List<DependanceActivity> findAll() {
        return dependanceActivityRepository.findAll();
    }



    public List<DependencyType> getdependencyTyp(){

        return Arrays.asList(DependencyType.values());
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
        // Vérifier si targetActivity exist
        if (modelDependanceActivity.getTargetActivity() != null &&
                activityRepository.findByName(modelDependanceActivity.getTargetActivity().getName()).isEmpty()) {
            throw new IllegalArgumentException("L'activité cible n'existe pas.");
        }

        // Vérifier si predecessorActivity exist
        if (modelDependanceActivity.getPredecessorActivity() != null &&
                activityRepository.findByName(modelDependanceActivity.getPredecessorActivity().getName()).isEmpty()) {
            throw new IllegalArgumentException("L'activité prédécesseur n'existe pas.");
        }
        return dependanceActivityRepository.save(modelDependanceActivity);
    }
    public void deleteById(Long id) {
        dependanceActivityRepository.deleteById(id);
    }


    public boolean existsDependance(Long target, Long predecessor ) {
      if(dependanceActivityRepository.countByTargetAndPredecessorNative(target, predecessor) >=1)

      {  System.out.println("Target Activity ID: " + target);
          System.out.println("Predecessor Activity ID: " + predecessor);return true;}
      return false;
    }



    public Map<Long, String> findPredecessorActivities(Long id) {
        Map<Long, String> predecessorMap = new HashMap<>();

        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            List<DependanceActivity> predecessorActivities = dependanceActivityRepository.findByTargetActivity(activity);

            for (DependanceActivity p : predecessorActivities) {
                predecessorMap.put(
                        p.getPredecessorActivity().getId(),
                        p.getDependencyType()
                );
            }
        }

        return predecessorMap;
    }


    public Map<Long, String> findSuccessorActivities(Long id) {
        Map<Long, String> successorMap = new HashMap<>();
        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            List<DependanceActivity> successorActivities=dependanceActivityRepository.findByPredecessorActivity(activity);

            for (DependanceActivity p : successorActivities) {
                successorMap.put(p.getTargetActivity().getId(), p.getDependencyType());
            }
        }

        return successorMap;
    }

    public ActivityDTO convertToActivityDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();

        if (activity != null) {
            dto.setId(activity.getId());
            dto.setNom(activity.getName());
            dto.setStatutActivity(activity.getStatut());
            dto.setTypeActivity(activity.getTypeActivity());


            dto.setParentActivityId(activity.getParentActivity().getId());

            dto.setProjectId(activity.getProject().getId());

            dto.setPlannedStartDate(activity.getPlannedStartDate());
            dto.setEffectiveStartDate(activity.getEffectiveStartDate());
            dto.setPlannedEndDate(activity.getPlannedEndDate());
            dto.setEffectiveEndDate(activity.getEffectiveEndDate());


            dto.setPredecessorActivity(findPredecessorActivities(activity.getId()));
            dto.setSuccessorActivity(findSuccessorActivities(activity.getId()));


            dto.setChildActivities(new ArrayList<>());
        }

        return dto;
    }


    public void coloneDependanceActivtes(Map<Long, String> structurActivities) {


    }
}
