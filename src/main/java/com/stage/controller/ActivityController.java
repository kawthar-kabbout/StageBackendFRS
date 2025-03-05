package com.stage.controller;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.StatutActivity;
import com.stage.persistans.TypeActivity;
import com.stage.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")

public class ActivityController {
    @Autowired
    private ActivityService activityService;


    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity createdModeleActivity = activityService.createModeleActivity(activity);
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
    public ResponseEntity<List<TypeActivity>> getTypeActivities() {
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
        // Récupérer la structure WBS depuis le service
        List<ActivityDTO> wbsStructure = activityService.getProjectWBSStructure(projectId);

        // Retourner la réponse HTTP avec la structure WBS
        return ResponseEntity.ok(wbsStructure);
    }

}
