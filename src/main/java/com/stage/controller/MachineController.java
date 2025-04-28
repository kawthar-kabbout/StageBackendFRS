package com.stage.controller;

import com.stage.persistans.Activity;
import com.stage.persistans.Machine;
import com.stage.persistans.enums.MachineType;
import com.stage.services.ActivityService;
import com.stage.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;
    private final ActivityService activityService;


    @GetMapping
    public ResponseEntity<List<Machine>> getMachines() {
        List<Machine> machines = machineService.findAll();
        return ResponseEntity.ok(machines);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Machine> getMachineById(@PathVariable Long id) {
        if (machineService.findById(id).isPresent()) {
            return ResponseEntity.ok(machineService.findById(id).get());
        }
        return ResponseEntity.notFound().build();
    }
   /* @GetMapping("/name/{name}")
    public ResponseEntity<Machine> getMachineByName(@PathVariable String name) {

    }*/

    @PostMapping
    public ResponseEntity<?> createMachine(@RequestBody Machine machine) {
        Map<String, String> response = new HashMap<>();

        if (machineService.findBySerialNumber(machine.getSerialNumber()) != null) {
            response.put("message", "Une machine avec ce numéro de série existe déjà.");
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(response);
        }

        machineService.save(machine);
        response.put("message", "Machine enregistrée avec succès.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/machienType")
    public ResponseEntity<List<MachineType>> getMachineTypes() {
        return ResponseEntity.ok(machineService.getTypes());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Machine> updateMachine(@PathVariable Long id, @RequestBody Machine machine) {
        if (machineService.findById(id).isPresent()) {
            machineService.update(id , machine);
            return ResponseEntity.ok(machine);
        }
        return ResponseEntity.notFound().build();

    }
    @PutMapping("/statusMachine/{id}")
    public ResponseEntity<Machine> changeMachineStatus(@PathVariable Long id, @RequestBody Machine machine) {
        if (machineService.findById(id).isPresent()) {
            if (machineService.changeMachineStatus(id, machine)) {
                return ResponseEntity.ok(machine);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMachine(@PathVariable Long id) {
        Optional<Machine> optionalMachine = machineService.findById(id);

        if (optionalMachine.isEmpty()) {
            return new ResponseEntity<>("Machine introuvable.", HttpStatus.NOT_FOUND);
        }

        Machine machine = optionalMachine.get();
        List<Activity> activities = activityService.getAllActivities();
        for (Activity activity : activities) {
            if (activity.getMachine() != null && activity.getMachine().getId().equals(id)) {
                return new ResponseEntity<>("Impossible de supprimer cette machine : elle est liée à une activité.", HttpStatus.CONFLICT);
            }
        }

        machineService.delete(machine);
        return new ResponseEntity<>("Machine supprimée avec succès.", HttpStatus.NO_CONTENT);
    }



}
