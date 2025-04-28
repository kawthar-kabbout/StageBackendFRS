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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {
    private  final ActivityService activityService;
    private final  ProjectService projectService;


    @PostMapping
    public ResponseEntity<?> createActivity(@RequestBody @Valid Activity activity) {
     //Optional<Activity>act =activityService.findByProjectIdAndName(activity.getProject().getId(), activity.getName())

        // Vérifier si une activité avec le même nom existe déjà pour le projet
        if (activityService.findByProjectIdAndName(activity.getProject().getId(), activity.getName()) != null ) {


            // Retourner une réponse avec le code 409 (Conflict) et un message d'erreur
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Une activité avec ce nom existe déjà pour ce projet.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // Créer l'activité et retourner la réponse avec le code 200 (OK)
        Activity createdModeleActivity = activityService.createActivity(activity);
        return ResponseEntity.ok(createdModeleActivity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Optional<Activity> existingOpt = activityService.getActivityById(id);

        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Vérifier l'unicité du nom d'activité pour ce projet
        Activity existingByName = activityService.findByProjectIdAndName(activity.getProject().getId(), activity.getName());

        if (existingByName != null && !existingByName.getId().equals(id)) {
            // Il existe déjà une autre activité avec le même nom pour le même projet
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Une activité avec ce nom existe déjà pour ce projet.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // Mettre à jour l'activité
        activity.setId(id); // S'assurer que l'ID correspond à celui de l'URL
        Activity updated = activityService.updateActivity(activity);

        return ResponseEntity.ok(updated);
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






    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        Optional<Activity> activity = activityService.getActivityById(id);
        if (activity.isPresent()) {
            // Si l'activité existe, on la supprime
            activityService.deleteActivity(id);
            return ResponseEntity.noContent().build(); // Retourne 204 No Content, activité supprimée
        } else {
            // Si l'activité n'existe pas, on retourne une erreur 404 Not Found avec un message
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("L'activité avec l'ID " + id + " n'existe pas.");
        }
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
