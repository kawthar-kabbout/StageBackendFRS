package com.stage.controller;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import com.stage.services.ActivityService;
import com.stage.services.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private  final ActivityService activityService;
    private final  ProjectService projectService;


    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody @Valid Activity activity) {
        Activity createdModeleActivity = activityService.createActivity(activity);
        return ResponseEntity.ok(createdModeleActivity);
    }




    //get all
    @GetMapping
    public ResponseEntity<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }
    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<Activity>> getActivitiesByProjectId(@PathVariable Long projectId) {
        List<Activity> activities = activityService.getActivitiesByProjectId(projectId);
        return ResponseEntity.ok(activities);
    }
    @GetMapping("/count-by-project/{projectId}")
    public long countActivitiesByProjectId(@PathVariable Long projectId) {
        return activityService.countActivitiesByProjectId(projectId);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Activity>> getActivityById(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        if (activity.isPresent()) {
            return ResponseEntity.ok(activity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



    @GetMapping("/name/{name}")
    public ResponseEntity<Optional<Activity>> getActivityByName(@PathVariable String name) {
        Optional<Activity> activity = activityService.getActivityByName(name);
        if (activity.isPresent()) {
            return ResponseEntity.ok(activity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/check-name/{name}")
    public ResponseEntity<Boolean> checkActivityName(@PathVariable String name) {

        if (activityService.getActivityByName(name).isPresent()) {
            return ResponseEntity.ok(true);
        }else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/statuts")
    public ResponseEntity<List<StatutActivity>> getStatutActivities() {
        return ResponseEntity.ok(activityService.getStatutActivities());
    }

    @GetMapping("/types")
    public ResponseEntity<List<ActivityType>> getTypeActivities() {
        return ResponseEntity.ok(activityService.getTypes());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity updatedActivity) {
        Optional<Activity> existingActivity = activityService.getActivityById(id);
        if (existingActivity.isPresent()) {
            updatedActivity.setId(id);
            Activity savedActivity =activityService.updateActivity(updatedActivity);
            return ResponseEntity.ok(savedActivity);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/{projectId}/wbs-structure")
    public ResponseEntity<List<ActivityDTO>> getProjectWBSStructure(@PathVariable Long projectId) {

        List<ActivityDTO> wbsStructure = activityService.getProjectWBSStructure(projectId);


        return ResponseEntity.ok(wbsStructure);
    }



    @GetMapping("/activitesNoChildren/{id}")
    public ResponseEntity<List<ActivityDTO>> getActivitesNoChildren(@PathVariable Long id) {
        Optional<Project> project = projectService.getProjectById(id);
        if (project.isPresent()) {

                if (activityService.getProjectWBSStructure(id) != null) {
                    List<ActivityDTO> wbsStructure = activityService.getActivitesHasNoChildren(project.get().getId());
                    return ResponseEntity.ok(wbsStructure);
                }
            }

        return ResponseEntity.notFound().build();
    }

}
