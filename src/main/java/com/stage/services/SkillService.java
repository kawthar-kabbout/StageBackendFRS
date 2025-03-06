package com.stage.services;

import com.stage.persistans.Skill;
import com.stage.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
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

   public Skill updateSkill( Skill skill) {
      return skillRepository.save(skill);
   }

}
