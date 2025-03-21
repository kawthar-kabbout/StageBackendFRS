package com.stage.services;


import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.DependanceActivity;
import com.stage.persistans.Project;
import com.stage.persistans.Skill;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.DependanceActivityRepository;
import com.stage.repositories.SkillRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final DependanceActivityRepository dependanceActivityRepository;
   // private final DependanceActivityService dependanceActivityService;



    public List<StatutActivity>getStatutActivities(){
        return   Arrays.asList(StatutActivity.values());
    }
    public List<ActivityType> getTypes() {
        return Arrays.asList(ActivityType.values());
    }
public List<Activity> getActivitiesByProjectId(Long projectId) {
        return activityRepository.findByProject_Id(projectId);
}
    // Create
    public Activity createActivity(Activity activity) {
        Optional<Activity>activityByname=activityRepository.findByProjectIdAndName(activity.getProject().getId(),activity.getName());
        if (activityByname.isPresent()) {
            throw new IllegalArgumentException("Une activité avec ce nom existe déjà !");
        }

        return activityRepository.save(activity);
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




    public Activity findByActivityTemplateIdAndProjectId(Long templateId,Project project) {
        return  this.activityRepository.findByActivityTemplateIdAndProjectId(templateId,project.getId());
    }



    public List<ActivityDTO> getProjectWBSStructure(Long projectId) {
        List<Activity> rootActivities = activityRepository.findByProjectIdAndParentActivityIsNull(projectId);
        List<ActivityDTO> result = new ArrayList<>();
        for (Activity root : rootActivities) {
            result.add(buildActivityTree(root));
        }
        return result;
    }






    private ActivityDTO buildActivityTree(Activity activity) {
        ActivityDTO dto = new ActivityDTO(
                activity.getId(),
                activity.getName(),
                activity.getStatut(),
                activity.getTypeActivity(),
                activity.getParentActivity() != null ? activity.getParentActivity().getId() : null,
                activity.getProject().getId(),
                null,
                activity.getDuration(),
                activity.getPlannedStartDate(),
                activity.getEffectiveStartDate(),
                activity.getPlannedEndDate(),
                activity.getEffectiveEndDate(),
                findPredecessorActivities(activity.getId()),
                findSuccessorActivities(activity.getId()),
                new ArrayList<>()
        );
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




    public Map<Long, String> findPredecessorActivities(Long id) {
        Map<Long, String> predecessorMap = new HashMap<>();

        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            List<DependanceActivity> predecessorActivities = dependanceActivityRepository.findByTargetActivity(activity);

            for (DependanceActivity p : predecessorActivities) {
                predecessorMap.put(
                        p.getPredecessorActivity().getId(),
                        p.getDependencyType().name()
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
                successorMap.put(p.getTargetActivity().getId(), p.getDependencyType().name());
            }
        }

        return successorMap;
    }

    public ActivityDTO convertToActivityDTOTemplateNew(Activity activity, Long activityTemplateId) {
        ActivityDTO dto = new ActivityDTO();

        if (activity != null) {
            dto.setId(activity.getId());
            dto.setNom(activity.getName());
            dto.setActivityTemplateId(activityTemplateId);


            if (activity.getParentActivity() != null) {
                dto.setParentActivityId(activity.getParentActivity().getId());
            } else {
                dto.setParentActivityId(null);
            }


            if (activity.getProject() != null) {
                dto.setProjectId(activity.getProject().getId());
            } else {
                dto.setProjectId(null);
            }
        }
        return dto;
    }




public void cloneActivityProjectRootTree(Project oldProject, Project newProject) {
        List<Activity> roots = activityRepository.findByProjectIdAndParentActivityIsNull(oldProject.getId());
        List<Activity> result = new ArrayList<>();
        for (Activity root : roots) {
                result.add(cloneActivity(root, newProject, null));
        }


    }

    private Activity cloneActivity(Activity oldActivity, Project newProject, Activity newParent) {
        Activity newActivity =  Activity.builder()
                .parentActivity(newParent)
                .activityTemplateId(oldActivity.getActivityTemplateId())
                .name(oldActivity.getName())
                .project(newProject)
                .duration(oldActivity.getDuration())
                .typeActivity(oldActivity.getTypeActivity())
                .effectiveEndDate(oldActivity.getEffectiveEndDate())
                .plannedStartDate(oldActivity.getPlannedStartDate())
                .plannedEndDate(oldActivity.getPlannedEndDate())
                .effectiveStartDate(oldActivity.getEffectiveStartDate())
                .statut(oldActivity.getStatut())
                .skills(oldActivity.getSkills().stream().toList())
                .activityTemplateId(oldActivity.getId())
                .build();

        newActivity = createActivity(newActivity);



        List<Activity> childrenActivities = activityRepository.findByParentActivity(oldActivity);

            for (Activity child : childrenActivities) {
                cloneActivity(child,newProject,newActivity );
            }
        return newActivity;
    }
}


