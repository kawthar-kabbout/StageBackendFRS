package com.stage.controller;

import com.stage.persistans.DependencyType;
import com.stage.persistans.DependanceActivity;
import com.stage.services.DependanceActivityService;
import com.stage.services.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/modele-dependanceactivities")
public class DependanceActivityController {
    private final DependanceActivityService dependanceActivityService;
    private final ActivityService modeledActivityService;

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



}
