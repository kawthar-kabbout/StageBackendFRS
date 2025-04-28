package com.stage.controller;

import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.persistans.Skill;
import com.stage.repositories.SkillRepository;
import com.stage.services.ActivityService;
import com.stage.services.EmployerService;
import com.stage.services.MachineService;
import com.stage.services.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;
    private final ActivityService activityService;
    private final EmployerService  employerService;

    @GetMapping
    public ResponseEntity<List<Skill>> getALLSkills() {
        List<Skill> skills = skillService.findAllSkills();
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Long id) {
        if (id == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Optional<Skill> skill = skillService.findSkillById(id);

        return skill.map(s -> new ResponseEntity<>(s, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<?> createSkill(@RequestBody Skill skill) {
        if (skillService.findSkillByName(skill.getName()) != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Une compétence avec ce nom existe déjà.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Skill savedSkill = skillService.save(skill);
        return new ResponseEntity<>(savedSkill, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill, @PathVariable Long id) {
        if (skillService.findSkillById(id).isPresent()) {
            Skill savedSkill = skillService.updateSkill(id , skill);

            return new ResponseEntity<>(savedSkill, HttpStatus.OK);
        } return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        Optional<Skill> optionalSkill = skillService.findSkillById(id);

        if (optionalSkill.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "La compétence n'existe pas.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Skill skill = optionalSkill.get();

        // Vérifie si la compétence est utilisée dans une activité
        List<Activity> activities = activityService.getAllActivities();
        for (Activity activity : activities) {
            if (activity.getSkill() != null && Objects.equals(activity.getSkill().getId(), skill.getId())) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Impossible de supprimer cette compétence car elle est utilisée dans une activité.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        }

        // Vérifie si un employé possède cette compétence
        List<Employer> employers = employerService.findAll();
        for (Employer employer : employers) {
            if (employer.getSkills().contains(skill)) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Impossible de supprimer cette compétence car elle est associée à un employé.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        }

        // Suppression
        if (skillService.deleteSkill(skill)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Erreur lors de la suppression de la compétence.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
