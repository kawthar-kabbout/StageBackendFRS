package com.stage.controller;

import com.stage.dto.ActivityDTO;
import com.stage.dto.DependanceActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import com.stage.persistans.enums.DependencyType;
import com.stage.persistans.DependanceActivity;
import com.stage.repositories.DependanceActivityRepository;
import com.stage.services.DependanceActivityService;
import com.stage.services.ActivityService;
import com.stage.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dependanceactivities")
public class DependanceActivityController {
    private final DependanceActivityService dependanceActivityService;
    private final ActivityService activityService;
    private final ProjectService projectService;
    private final PathMatcher pathMatcher;


    @GetMapping

    public ResponseEntity<List<DependanceActivity>> getAllDependanceActivity() {
        List<DependanceActivity> modelDependanceActivities = dependanceActivityService.findAll();
        return ResponseEntity.ok(modelDependanceActivities);
    }

    @GetMapping("/dep/{id}")
    public ResponseEntity<DependanceActivity> getDependanceActivityById(@PathVariable Long id) {
        Optional<DependanceActivity> modelDependanceActivity = dependanceActivityService.findById(id);
        if (modelDependanceActivity.isPresent()) {
            return ResponseEntity.ok(modelDependanceActivity.get());
        }
        else
            return ResponseEntity.notFound().build();

    }
    @GetMapping("/project/{id}")
    public ResponseEntity<List<DependanceActivity>> getDependanceActivityByProjectId(@PathVariable Long id) {
        Optional<Project>project=projectService.getProjectById(id);
        if (project.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        else {
            List<DependanceActivity>deps=dependanceActivityService.getDependenceActivitiesByProjectId(id);
            return ResponseEntity.ok(deps);
        }
    }


    @GetMapping("/dependencyType")

    public ResponseEntity<List<DependencyType>> getdependencyTyp() {
        return ResponseEntity.ok(dependanceActivityService.getdependencyTyp());
    }

    @GetMapping("/{idTarget}/{idProdesesseur}")
    public ResponseEntity<DependanceActivity> findByTargetActivityAndPredecessorActivity(@PathVariable Long idTarget,   @PathVariable Long idProdesesseur) {
        Optional<Activity> target = activityService.getActivityById(idTarget);
        Optional <Activity> prodesesseur = activityService.getActivityById(idProdesesseur);
        if (prodesesseur.isEmpty()|| target.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

         DependanceActivity dep=   dependanceActivityService.findByTargetAndProdesesseur(target.get(),prodesesseur.get());
        return ResponseEntity.ok(dep);
    }
    
    @PostMapping
    public ResponseEntity<DependanceActivity> createModelDependanceActivity(@RequestBody DependanceActivity modelDependanceActivity) {
      dependanceActivityService.save(modelDependanceActivity);
        return ResponseEntity.ok(modelDependanceActivity);
    }



    @PutMapping("/{id}")
    public ResponseEntity<DependanceActivity> updateModelDependanceActivity(@PathVariable Long id ,@RequestBody DependanceActivity newDep) {
     Optional<DependanceActivity> modelDependanceActivityOptional = dependanceActivityService.findById(id);
     if (modelDependanceActivityOptional.isPresent()) {
     DependanceActivity    depUpdated = dependanceActivityService.update( id,newDep);
     if (depUpdated == null) {
         return ResponseEntity.notFound().build();
     }
         return ResponseEntity.ok(depUpdated);
     } else {
         return ResponseEntity.notFound().build();
     }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModelDependanceActivity(@PathVariable Long id) {
        Optional<DependanceActivity> modelDependanceActivity = dependanceActivityService.findById(id);

        if (modelDependanceActivity.isPresent()) {
            dependanceActivityService.deleteById(modelDependanceActivity.get());
            // Retourner un message clair de succès
            Map<String, String> response = new HashMap<>();
            response.put("message", "La dépendance d'activité a été supprimée avec succès.");
            return ResponseEntity.ok(response);
        } else {
            // Retourner un message clair si non trouvé
            Map<String, String> response = new HashMap<>();
            response.put("message", "La dépendance d'activité avec cet ID n'existe pas.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


}
