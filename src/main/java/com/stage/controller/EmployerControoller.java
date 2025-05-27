package com.stage.controller;

import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.services.ActivityService;
import com.stage.services.EmployerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/employeurs")

@RequiredArgsConstructor
public class EmployerControoller {
    private final EmployerService employerService;
    private final ActivityService activityService;


    @GetMapping
    public ResponseEntity<List<Employer>> getAllEmployer(){
        List<Employer> employers = employerService.findAll();
        return new ResponseEntity<>(employers, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<?> createEmployer(@RequestBody Employer employer) {
        if (employerService.findEmployerByPhone(employer.getPhone()) != null) {

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Un employeur avec ce numéro de téléphone existe déjà.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        if (employerService.findEmployerByEmail(employer.getEmail()) != null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Un employeur avec cet email existe déjà.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        Employer savedEmployer = employerService.save(employer);
        return new ResponseEntity<>(savedEmployer, HttpStatus.CREATED);
    }



    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployer(@PathVariable("id") Long id, @RequestBody Employer employerDetails) {
        Optional<Employer> existingEmployerOpt = employerService.findById(id);

        if (existingEmployerOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Employer existingEmployer = existingEmployerOpt.get();

        // Vérifier si un autre employeur utilise déjà ce numéro
        Employer employerWithSamePhone = employerService.findEmployerByPhone(employerDetails.getPhone());
        if (employerWithSamePhone != null && !employerWithSamePhone.getId().equals(id)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Un employeur avec ce numéro de téléphone existe déjà.");
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }

        // Mise à jour des données
        existingEmployer.setFirstName(employerDetails.getFirstName());
        existingEmployer.setLastName(employerDetails.getLastName());
        existingEmployer.setPhone(employerDetails.getPhone());
        existingEmployer.setAddress(employerDetails.getAddress());
        existingEmployer.setSkills(employerDetails.getSkills());
        existingEmployer.setGrade(employerDetails.getGrade());

        Employer updatedEmployer = employerService.save(existingEmployer);
        return new ResponseEntity<>(updatedEmployer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployer(@PathVariable Long id) {
        Optional<Employer> optionalEmployer = employerService.findById(id);

        if (optionalEmployer.isEmpty()) {
            return new ResponseEntity<>("Employé introuvable.", HttpStatus.NOT_FOUND);
        }

        Employer employer = optionalEmployer.get();
        List<Activity> activities = activityService.getAllActivities();

        for (Activity activity : activities) {
            if (activity.getEmployees() != null && activity.getEmployees().contains(employer)) {
                return new ResponseEntity<>("Impossible de supprimer cet employé : il est lié à une activité.", HttpStatus.CONFLICT);
            }
        }

        employerService.delete(employer);
        return new ResponseEntity<>("Employé supprimé avec succès.", HttpStatus.NO_CONTENT);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Employer> getEmployerById(@PathVariable("id") Long id) {
        Optional<Employer> isEmployer = employerService.findById(id);
        if (isEmployer.isPresent()) {
            return new ResponseEntity<>(isEmployer.get(), HttpStatus.OK);
        }return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
}


@PutMapping("/updateStatus/{id}")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Long id, @RequestBody Employer employer) {
        Optional<Employer> existingEmployerOpt = employerService.findById(id);
        if (existingEmployerOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Employer existingEmployer = existingEmployerOpt.get();
        employerService.updateStatus(employer);
        return new ResponseEntity<>(existingEmployer, HttpStatus.OK);
}


}
