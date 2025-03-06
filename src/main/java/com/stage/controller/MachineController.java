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
        }return  ResponseEntity.notFound().build();
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
}
