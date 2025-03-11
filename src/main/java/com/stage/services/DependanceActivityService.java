package com.stage.services;

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



    public Map<Long, String> findPredecessorActivities(Activity activity) {
        Map<Long, String> predecessorMap = new HashMap<>();
        List<DependanceActivity> predecessorActivities=dependanceActivityRepository.findByTargetActivity(activity);

        for (DependanceActivity p : predecessorActivities) {
            predecessorMap.put(p.getPredecessorActivity().getId(), p.getDependencyType());
        }
        return predecessorMap;
    }

    public Map<Long, String> findSuccessorActivities(Activity activity) {
        Map<Long, String>  successorMap = new HashMap<>();
        List<DependanceActivity> successorActivities=dependanceActivityRepository.findByPredecessorActivity(activity);

        for (DependanceActivity p : successorActivities) {
            successorMap.put(p.getPredecessorActivity().getId(), p.getDependencyType());
        }
        return successorMap;
    }

    public DependanceActivityDTO convertToDTO(Activity targetActivity) {
        DependanceActivityDTO dto = new DependanceActivityDTO();
        dto.setTargetActivity(targetActivity);
        dto.setPlannedStartDate(targetActivity.getPlannedStartDate());
        dto.setEffectiveStartDate(targetActivity.getEffectiveStartDate());
        dto.setPlannedEndDate(targetActivity.getPlannedEndDate());
        dto.setEffectiveEndDate(targetActivity.getEffectiveEndDate());
        Map<Long, String> successorMap = new HashMap<>(findSuccessorActivities(targetActivity));

        Map<Long, String> predecessorMap = new HashMap<>(findPredecessorActivities(targetActivity));

        dto.setPredecessorActivity(predecessorMap);
        dto.setSuccessorActivity(successorMap);
        return dto;


    }


}
