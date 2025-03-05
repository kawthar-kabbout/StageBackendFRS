package com.stage.services;


import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.TypeActivity;
import com.stage.repositories.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;


    public List<StatutActivity>getStatutActivities(){
        return   Arrays.asList(StatutActivity.values());
    }
    public List<TypeActivity> getTypes() {
        return Arrays.asList(TypeActivity.values());
    }
public List<Activity> getActivitiesByProjectId(Long projectId) {
        return activityRepository.findByProject_Id(projectId);
}
    // Create
    public Activity createModeleActivity(Activity modeleActivity) {
        return activityRepository.save(modeleActivity);
    }

    // Read All
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
public Long countActivitiesByProjectId(Long id){
return  activityRepository.countByProjectId(id);
}
    // Read One by ID
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    // Read One by Name
    public Optional<Activity> getActivityByName(String name) {
        return activityRepository.findByName(name);
    }



    // Update
    public Activity updateActivity(Activity modeleActivity) {
        return activityRepository.save(modeleActivity);
    }

    // Delete
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }



    public List<ActivityDTO> getProjectWBSStructure(Long projectId) {
        // Récupérer les activités racines (sans activité parente) pour le projet donné
        List<Activity> rootActivities = activityRepository.findByProjectIdAndParentActivityIsNull(projectId);

        List<ActivityDTO> result = new ArrayList<>();

        // Construire l'arbre pour chaque activité racine
        for (Activity root : rootActivities) {
            result.add(buildActivityTree(root));
        }

        return result;
    }

    private ActivityDTO buildActivityTree(Activity activity) {
        // Créer un DTO pour l'activité courante
        ActivityDTO dto = new ActivityDTO(
                activity.getId(),
                activity.getName(),
                activity.getStatut(),
                activity.getTypeActivity(),
                activity.getParentActivity() != null ? activity.getParentActivity().getId() : null,
                activity.getProject().getId(),
                activity.getPlannedEndDate(),
                activity.getPlannedStartDate(),
                new ArrayList<>()
        );

        // Récupérer les activités enfants
        List<Activity> childrenActivities = activityRepository.findByParentActivity(activity);

        if (!childrenActivities.isEmpty()) {
            List<ActivityDTO> childrenDTOs = new ArrayList<>();
            for (Activity child : childrenActivities) {
                childrenDTOs.add(buildActivityTree(child));
            }
            dto.setChildActivities(childrenDTOs);
        }

        return dto;
    }






}
