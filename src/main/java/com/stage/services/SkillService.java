package com.stage.services;

import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.persistans.Skill;
import com.stage.repositories.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SkillService {
    private final ActivityService activityService;
    private final SkillRepository skillRepository;
    private final EmployerService employerService;

   public Skill save(Skill skill) {

       return skillRepository.save(skill);
   }
   public List<Skill> findAllSkills() {
   List<Skill> skills = skillRepository.findAll();
   List<Skill> filteredSkills = new ArrayList<>();
   for (Skill skill : skills) {
    if (skill.getArchived()==0){
        filteredSkills.add(skill);
    }

}
       return filteredSkills;
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
public Skill findSkillByName(String name) {
       if (skillRepository.findByName(name).isPresent()){
           return skillRepository.findByName(name).get();
       }
       return null;
}

public boolean deleteSkill(Skill skill) {

       if (skillRepository.findById(skill.getId()).isPresent()){
           skill.setArchived(1);
           skillRepository.save(skill);
           return true;
       }return
            false;
}

}
