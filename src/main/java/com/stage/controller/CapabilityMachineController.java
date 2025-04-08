package com.stage.controller;

import com.stage.persistans.CapabilityMachine;
import com.stage.services.CategoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/CapabilityMachine")
public class CapabilityMachineController {
    private final CategoryTypeService categoryTypeService;


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
}
