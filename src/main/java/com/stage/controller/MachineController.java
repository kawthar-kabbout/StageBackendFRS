package com.stage.controller;

import com.stage.persistans.Machine;
import com.stage.persistans.enums.MachineType;
import com.stage.services.MachineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/machines")
@RequiredArgsConstructor
public class MachineController {

    private final MachineService machineService;


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
    public ResponseEntity<Machine> createMachine(@RequestBody Machine machine) {
        machineService.save(machine);
        return ResponseEntity.ok(machine);
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


}
