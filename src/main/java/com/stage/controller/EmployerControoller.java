package com.stage.controller;

import com.stage.persistans.Employer;
import com.stage.services.EmployerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employeurs")

@RequiredArgsConstructor
public class EmployerControoller {
    private final EmployerService employerService;


    @GetMapping
    public ResponseEntity<List<Employer>> getAllEmployer(){
        List<Employer> employers = employerService.findAll();
        return new ResponseEntity<>(employers, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<Employer> createEmployer(@RequestBody Employer employer) {
        return new ResponseEntity<>(employerService.save(employer), HttpStatus.CREATED);
    }
/*
    @PutMapping("/{id}")
    public ResponseEntity<Employer> updateEmployer(@PathVariable("id") Long id, @RequestBody Employer employer) {
        Optional<Employer> isEmployer = employerService.findById(id);

        if (isEmployer.isPresent()) {
            Employer existingEmployer = isEmployer.get();

            if (existingEmployer != null && employer != null) {
                // Vérifier que les attributs ne sont pas nuls
                if (employer.getName() != null && employer.getPhone()!=0 && employer.getAddress() != null) {
                    existingEmployer.setName(employer.getName());
                    existingEmployer.setPhone(employer.getPhone());
                    existingEmployer.setAddress(employer.getAddress());
                    existingEmployer.setActivities(employer.getActivities());

                    Employer updatedEmployer = employerService.save(existingEmployer);
                    return new ResponseEntity<>(updatedEmployer, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);  // Si un champ est nul
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // Si l'employeur n'existe pas
    }
*/
    @DeleteMapping("/{id}")
public ResponseEntity<Employer> deleteEmployer(@PathVariable("id") Long id) {
    Optional<Employer> isEmployer = employerService.findById(id);
    if (isEmployer.isPresent()) {
        Employer employer = isEmployer.get();
        employerService.delete(employer);
        return new ResponseEntity<>(employer, HttpStatus.OK);
    } return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
}

}
