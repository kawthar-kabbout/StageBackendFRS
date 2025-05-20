package com.stage.repositories;

import com.stage.persistans.Employer;
import com.stage.persistans.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findByPhone(String phone);
    @Query("SELECT MAX(e.matricule) FROM Employer e")
    Integer findMaxMatricule();
}
