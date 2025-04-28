package com.stage.controller;

import com.stage.persistans.Activity;
import com.stage.persistans.CapabilityMachine;
import com.stage.persistans.Employer;
import com.stage.persistans.Machine;
import com.stage.services.ActivityService;
import com.stage.services.CategoryTypeService;
import com.stage.services.EmployerService;
import com.stage.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/CapabilityMachine")
public class CapabilityMachineController {
    private final CategoryTypeService categoryTypeService;
    private final ActivityService activityService;
    private final MachineService machineService;
    private final EmployerService employerService;


    @GetMapping
    public ResponseEntity<List<CapabilityMachine>> getAll() {
       return ResponseEntity.ok(categoryTypeService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CapabilityMachine> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryTypeService.findById(id));
    }
    @PostMapping
    public ResponseEntity<CapabilityMachine> save(@RequestBody CapabilityMachine categoryType) {
        return ResponseEntity.ok(categoryTypeService.save(categoryType));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CapabilityMachine> update(@PathVariable Long id, @RequestBody CapabilityMachine categoryType) {
      CapabilityMachine existingCategoryType = categoryTypeService.findById(id);
      if (existingCategoryType != null) {
          categoryType.setId(id);
          categoryTypeService.save(categoryType);
          return ResponseEntity.ok(categoryType);
      }else  return ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        CapabilityMachine existingCategoryType = categoryTypeService.findById(id);

        if (existingCategoryType == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "La catégorie de machine n'existe pas.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Vérifier si une activité utilise cette capacité
        List<Activity> activities = activityService.getAllActivities();
        for (Activity activity : activities) {
            if (activity.getCapabilityMachine() != null && activity.getCapabilityMachine().equals(existingCategoryType)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Impossible de supprimer : cette capacité est utilisée dans une activité.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        }

        // Vérifier si une machine utilise cette capacité
        List<Machine> machines = machineService.findAll();
        for (Machine machine : machines) {
            if (machine.getCapabilityMachines() != null && machine.getCapabilityMachines().contains(existingCategoryType)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Impossible de supprimer : cette capacité est liée à une machine.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        }

        // Vérifier si un employé utilise cette capacité
        List<Employer> employers = employerService.findAll();
        for (Employer employer : employers) {
            if (employer.getCapabilityMachine() != null && employer.getCapabilityMachine().contains(existingCategoryType)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Impossible de supprimer : cette capacité est attribuée à un employé.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        }

        // Si tout est bon : suppression
        categoryTypeService.delete(existingCategoryType);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Catégorie de machine supprimée avec succès.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
