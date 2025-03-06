package com.stage.controller;

import com.stage.persistans.Skill;
import com.stage.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillRepository skillRepository;

    @GetMapping
    public ResponseEntity<List<Skill>> getALLSkills() {
        List<Skill> skills = skillRepository.findAll();
        return new ResponseEntity<>(skills, HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(Long id) {
        Optional<Skill> skill = skillRepository.findById(id);
        return new ResponseEntity<>(skill.orElse(null), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        Skill savedSkill = skillRepository.save(skill);
        return new ResponseEntity<>(savedSkill, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill, @PathVariable Long id) {
        if (skillRepository.findById(id).isPresent()) {
            Skill savedSkill = skillRepository.save(skill);
            return new ResponseEntity<>(savedSkill, HttpStatus.OK);
        } return ResponseEntity.notFound().build();
    }



}
