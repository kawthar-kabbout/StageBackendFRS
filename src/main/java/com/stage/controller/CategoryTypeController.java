package com.stage.controller;

import com.stage.persistans.CategoryType;
import com.stage.repositories.CategoryTypeRepository;
import com.stage.services.CategoryTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categoryType")
public class CategoryTypeController {
    private final CategoryTypeService categoryTypeService;


    @GetMapping
    public ResponseEntity<List<CategoryType>> getAll() {
       return ResponseEntity.ok(categoryTypeService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<CategoryType> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryTypeService.findById(id));
    }
    @PostMapping
    public ResponseEntity<CategoryType> save( @RequestBody CategoryType categoryType) {
        return ResponseEntity.ok(categoryTypeService.save(categoryType));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryType> update(@PathVariable Long id, @RequestBody CategoryType categoryType) {
        if (categoryTypeService.findById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categoryTypeService.update(categoryType));
    }
}
