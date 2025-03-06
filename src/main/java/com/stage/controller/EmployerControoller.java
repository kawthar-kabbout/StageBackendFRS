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


    @PutMapping("/{id}")
    public ResponseEntity<Employer> updateEmployer(@PathVariable("id") Long id, @RequestBody Employer employerDetails) {
        Optional<Employer> existingEmployerOpt = employerService.findById(id);

        if (existingEmployerOpt.isPresent()) {
            Employer existingEmployer = existingEmployerOpt.get();


            existingEmployer.setFirstName(employerDetails.getFirstName());
            existingEmployer.setLastName(employerDetails.getLastName());
            existingEmployer.setPhone(employerDetails.getPhone());
            existingEmployer.setAddress(employerDetails.getAddress());
            existingEmployer.setActivities(employerDetails.getActivities());
            existingEmployer.setSkills(employerDetails.getSkills());
            Employer updatedEmployer = employerService.save(existingEmployer);

            return new ResponseEntity<>(updatedEmployer, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
public ResponseEntity<Employer> deleteEmployer(@PathVariable("id") Long id) {
    Optional<Employer> isEmployer = employerService.findById(id);
    if (isEmployer.isPresent()) {
        Employer employer = isEmployer.get();
        employerService.delete(employer);
        return new ResponseEntity<>(employer, HttpStatus.OK);
    } return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
}


@GetMapping("/{id}")
    public ResponseEntity<Employer> getEmployerById(@PathVariable("id") Long id) {
        Optional<Employer> isEmployer = employerService.findById(id);
        if (isEmployer.isPresent()) {
            return new ResponseEntity<>(isEmployer.get(), HttpStatus.OK);
        }return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
}



}
