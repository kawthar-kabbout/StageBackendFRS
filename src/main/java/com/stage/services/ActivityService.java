package com.stage.services;


import com.stage.dto.ActiviteFrontDTO;
import com.stage.dto.ActivityDTO;
import com.stage.persistans.*;
import com.stage.persistans.enums.Statut;
import com.stage.persistans.enums.ActivityType;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.DependanceActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final DependanceActivityRepository dependanceActivityRepository;
   // private final DependanceActivityService dependanceActivityService;



    public List<Statut>getStatutActivities(){
        return   Arrays.asList(Statut.values());
    }
    public List<ActivityType> getTypes() {
        return Arrays.asList(ActivityType.values());
    }


public List<Activity> getActivitiesByProjectId(Long projectId) {
List<Activity> res = new ArrayList<>();
        List <Activity> activities = activityRepository.findByProject_Id(projectId);
        for (Activity activity : activities) {
            if(activity.getArchived()==0)
                res.add(activity);
        }


        return res;
}
    // Create
    public Activity createActivity(Activity activity) {


        return activityRepository.save(activity);
    }

    // Read All
    public List<Activity> getAllActivities() {
List<Activity> res = new ArrayList<>();
        List<Activity> activities= activityRepository.findAll();
        for (Activity activity : activities) {
            if (activity.getArchived()==0)
                res.add(activity);
        }

        return res;
    }
public Long countActivitiesByProjectId(Long id){
return  activityRepository.countByProjectId(id);
}
    // Read One by ID
    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public Optional<Activity> getActivityByName(String name) {
        return activityRepository.findByName(name);
    }

    public Activity updateActivity(Activity modeleActivity) {

            return this.createActivity(modeleActivity);


    }
    public Activity updateActivityDurationAndEmpLNumber(Activity modeleActivity) {
        Optional<Activity>activity = activityRepository.findById(modeleActivity.getId());
        if (activity.isPresent()) {
            Activity modeleActivity1 = activity.get();
            modeleActivity1.setDuration(modeleActivity.getDuration());
            modeleActivity1.setEmployersNumber(modeleActivity.getEmployersNumber());
            return this.createActivity(modeleActivity1);
        }
        return null;

    }

    public Boolean deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id).get();
        if (activity != null) {
            {

                activityRepository.save(activity);
                List<DependanceActivity> deps=  dependanceActivityRepository.findByTargetActivity(activity);
                List<DependanceActivity>depsProdese = dependanceActivityRepository.findByPredecessorActivity(activity);

                for (DependanceActivity dep : deps) {
                    dep.setArchived(1);

                }
                for (DependanceActivity dep : depsProdese) {
                    dep.setArchived(1);
                }
                activity.setArchived(1);

            }
            return true;
        }
        return  false;
    }

public Boolean deleteActivitiesByProject(Long projectId) {
        List<Activity> activities= activityRepository.findByProject_Id(projectId);
        for (Activity activity : activities) {
            if (this.deleteActivity(activity.getId())==false) {
                return false;
            }
        }
        return true;
}


    public Activity findByActivityTemplateIdAndProjectId(Long templateId,Project project) {
        return  this.activityRepository.findByActivityTemplateIdAndProjectId(templateId,project.getId());
    }



    public List<ActivityDTO> getProjectWBSStructure(Long projectId) {
        List<Activity> rootActivities = activityRepository.findByProjectIdAndParentActivityIsNull(projectId);
        List<ActivityDTO> result = new ArrayList<>();
        for (Activity root : rootActivities) {
           if (root.getArchived()==0){
               result.add(buildActivityTree(root));
           }
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
                activity.getProject(),
                null,
                activity.getDuration(),
                activity.getPlannedStartDate(),
                activity.getEffectiveStartDate(),
                activity.getPlannedEndDate(),
                activity.getEffectiveEndDate(),

                findPredecessorActivities(activity.getId()),
                findSuccessorActivities(activity.getId()),
                new ArrayList<>(),
                activity.getEmployees()!=null?activity.getEmployees().stream().toList():null,
                activity.getMachine(),
                activity.getEmployersNumber()
        );
        List<Activity> childrenActivities = activityRepository.findByParentActivity(activity);
        if (!childrenActivities.isEmpty()) {
            List<ActivityDTO> childrenDTOs = new ArrayList<>();
            for (Activity child : childrenActivities) {
              if (child.getArchived()==0){
                  childrenDTOs.add(buildActivityTree(child));
              }
            }
            dto.setChildActivities(childrenDTOs);
        }
        return dto;
    }



    public List<ActivityDTO> getActivitesHasNoChildren(Long projectId) {
        List<ActivityDTO> activitesTree = getProjectWBSStructure(projectId);
        List<ActivityDTO> activitesHasNoChildren = new ArrayList<>();

        for (ActivityDTO dto : activitesTree) {
            collectLeafActivities(dto, activitesHasNoChildren);
        }

        return activitesHasNoChildren;
    }

    private void collectLeafActivities(ActivityDTO activity, List<ActivityDTO> leaves) {
        List<ActivityDTO> children = activity.getChildActivities();
        if (children == null || children.isEmpty()) {
            leaves.add(activity);
        } else {
            for (ActivityDTO child : children) {
                collectLeafActivities(child, leaves);
            }
        }


    }









    public Map<Long, String> findPredecessorActivities(Long id) {
        Map<Long, String> predecessorMap = new HashMap<>();

        Optional<Activity> optionalActivity = activityRepository.findById(id);

        if (optionalActivity.isPresent()) {
            Activity activity = optionalActivity.get();
            List<DependanceActivity> predecessorActivities = dependanceActivityRepository.findByTargetActivity(activity);

            for (DependanceActivity p : predecessorActivities) {
                if (p.getArchived()==0)
                {predecessorMap.put(
                        p.getPredecessorActivity().getId(),
                        p.getDependencyType().name()
                );}
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
                if (p.getArchived()==0)
                {successorMap.put(p.getTargetActivity().getId(), p.getDependencyType().name());}
            }
        }

        return successorMap;
    }


    public List <Activity> findByProjectIdAndParentActivityIsNull(Long projectId) {
        List<Activity>rootActivitres=this.activityRepository.findByProjectIdAndParentActivityIsNull(projectId);
        List <Activity>resultActivitres=new ArrayList<>();
        for (Activity a : rootActivitres) {
            if (a.getArchived()==0)
                resultActivitres.add(a);
        }

        return resultActivitres;
    }


    public List<Activity>getEmployerActivitiesNotFinish(Employer employer) {
        List<Activity> activities = activityRepository.findAll();
        List<Activity> result = new ArrayList<>();
        for (Activity activity : activities) {
            if(activity.getEmployees()!=null){
            for (Employer e :activity.getEmployees()){
                if (employer.getId().equals(e.getId()) && activity.getStatut() != Statut.Finish)
                        result.add(activity);

            }
        }}
        return result;
    }

    public List<Activity>getMachineActivitiesNotFinish(Machine machine ) {
        List<Activity> activities = activityRepository.findAll();
        List<Activity> result = new ArrayList<>();
        for (Activity activity : activities) {
            if(activity.getMachine()!=null
            && machine.getId().equals(activity.getMachine().getId())
                    && activity.getStatut()!=Statut.Finish)

               result.add(activity);

        }
        return result;
    }


public void cloneActivityProjectRootTree(Project oldProject, Project newProject,List<ActiviteFrontDTO> activitesFrontDTO) {
        List<Activity> roots = activityRepository.findByProjectIdAndParentActivityIsNull(oldProject.getId());
        List<Activity> result = new ArrayList<>();
        for (Activity root : roots) {
               if (root.getArchived()==0)
               {
                   result.add(cloneActivity(root, newProject, null,activitesFrontDTO));
               }
        }


    }

    private Activity cloneActivity(Activity oldActivity, Project newProject, Activity newParent,List<ActiviteFrontDTO> activitesFrontDTO) {


        ActiviteFrontDTO dto = activitesFrontDTO.stream().filter((activityDto -> activityDto.getId() == oldActivity.getId())).findFirst().orElse(null);

        Activity newActivity =  Activity.builder()
                .parentActivity(newParent)
                .activityTemplateId(oldActivity.getActivityTemplateId())
                .name(oldActivity.getName())
                .project(newProject)
                .duration(dto !=null?dto.getDuration():null)
                .typeActivity(oldActivity.getTypeActivity())
                .effectiveEndDate(null)
                .plannedStartDate(null)
                .plannedEndDate(null)
                .effectiveStartDate(null)
                .statut(Statut.Pending)
                .skill(oldActivity.getSkill())
                .capabilityMachine(oldActivity.getCapabilityMachine())
                .activityTemplateId(oldActivity.getId())
                .machine(null)
                .employees(null)
                .employersNumber(dto.getEmployersNumber())
                .build();

        newActivity = createActivity(newActivity);



        List<Activity> childrenActivities = activityRepository.findByParentActivity(oldActivity);

            for (Activity child : childrenActivities) {
                if (child.getArchived()==0){
                    cloneActivity(child,newProject,newActivity,activitesFrontDTO );
                }
            }
        return newActivity;
    }

  public void updateActivitiesCloningRoot(List<ActiviteFrontDTO> newActiviteFrontDTO ,Long newProject ) {
        List<Activity> roots = activityRepository.findByProjectIdAndParentActivityIsNull(newProject);
        List<Activity> result = new ArrayList<>();
        for (Activity root : roots) {
            result.add(updateActivityCloning(root , newActiviteFrontDTO));
        }
    }
    private  Activity updateActivityCloning(Activity activity ,List<ActiviteFrontDTO> newActiviteFrontDTO ) {


        for (ActiviteFrontDTO f : newActiviteFrontDTO) {
            if(activity.getActivityTemplateId()==f.getId())
            {

                activity.setEmployees(List.of(f.getEmployers().get(0)));
              //  activity.setMachines(f.getMachines().stream().toList());
//                activity.setDuration(f.getDuration());
               updateActivity(activity);
            }
        }
        List<Activity> childrenActivities = activityRepository.findByParentActivity(activity);

        for (Activity child : childrenActivities) {
            updateActivityCloning(child,newActiviteFrontDTO );
        }
        return activity;

    }

    public Activity findByProjectIdAndName(Long id,  String name) {
        Optional<Activity>activityByname=activityRepository.findByProjectIdAndName(id, name);
        if (activityByname.isPresent()) {
           return activityByname.get();
        }

        return null;
    }



}


