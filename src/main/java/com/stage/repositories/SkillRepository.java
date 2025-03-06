package com.stage.repositories;

import com.stage.persistans.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillRepository extends JpaRepository<Skill,Long> {
}
