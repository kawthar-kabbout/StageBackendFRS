package com.stage.services;

import com.stage.persistans.Skill;
import com.stage.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

   public Skill createSkill(Skill skill) {
       return skillRepository.save(skill);
   }
   public List<Skill> findAllSkills() {
       return skillRepository.findAll();
   }
   public Optional<Skill> findSkillById(Long id) {
       return skillRepository.findById(id);
   }

   public Skill updateSkill(Long id, Skill skill) {
      if (skillRepository.findById(id).isPresent()){
          Skill s=skillRepository.findById(id).get();
          s.setName(skill.getName());
          s.setDescription(skill.getDescription());
          skillRepository.save(s);
          return s;

      }else
          return null;

   }

}
