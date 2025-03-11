package com.stage.controller;

import com.stage.dto.DependanceActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.enums.DependencyType;
import com.stage.persistans.DependanceActivity;
import com.stage.repositories.DependanceActivityRepository;
import com.stage.services.DependanceActivityService;
import com.stage.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dependanceactivities")
public class DependanceActivityController {
    private final DependanceActivityService dependanceActivityService;
    private final ActivityService activityService;



    @GetMapping

    public ResponseEntity<List<DependanceActivity>> getAllModelDependanceActivity() {
        List<DependanceActivity> modelDependanceActivities = dependanceActivityService.findAll();
        return ResponseEntity.ok(modelDependanceActivities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<DependanceActivity>> getModelDependanceActivityById(@RequestParam Long id) {
        Optional<DependanceActivity> modelDependanceActivity = dependanceActivityService.findById(id);
        if (modelDependanceActivity.isPresent()) {
            return ResponseEntity.ok(modelDependanceActivity);
        }
        else
            return ResponseEntity.notFound().build();

    }

    @GetMapping("/dependencyType")
    public ResponseEntity<List<DependencyType>> getdependencyTyp() {
        return ResponseEntity.ok(dependanceActivityService.getdependencyTyp());
    }
    
    @PostMapping
    public ResponseEntity<DependanceActivity> createModelDependanceActivity(@RequestBody DependanceActivity modelDependanceActivity) {
      dependanceActivityService.save(modelDependanceActivity);
        return ResponseEntity.ok(modelDependanceActivity);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DependanceActivity> updateModelDependanceActivity(@RequestBody DependanceActivity modelDependanceActivity) {
     Optional<DependanceActivity> modelDependanceActivityOptional = dependanceActivityService.findById(modelDependanceActivity.getId());
     if (modelDependanceActivityOptional.isPresent()) {
         modelDependanceActivity = dependanceActivityService.update(modelDependanceActivity);
         return ResponseEntity.ok(modelDependanceActivity);
     } else {
         return ResponseEntity.notFound().build();
     }
    }


    @GetMapping("/dependanceActDTO/{id}")
   public ResponseEntity<DependanceActivityDTO> getModelDependanceActivityDTO(@PathVariable Long id) {

        if (activityService.getActivityById(id).isPresent()) {
            Activity a = activityService.getActivityById(id).get();
            DependanceActivityDTO dependanceActivityDTO = dependanceActivityService.convertToDTO(a);
            return ResponseEntity.ok(dependanceActivityDTO);
        }return ResponseEntity.notFound().build();
    }
    }
