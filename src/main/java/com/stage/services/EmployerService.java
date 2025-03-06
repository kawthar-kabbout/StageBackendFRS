package com.stage.services;

import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.repositories.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository employerRepository;


    public List<Employer> findAll() {
        return employerRepository.findAll();
    }

    public Optional<Employer> findById(Long id) {
        return employerRepository.findById(id);
    }


    public Employer save(Employer employer) {
       return employerRepository.save(employer);
    }

    public Employer update(Employer employer) {
        if (employer == null || !employerRepository.existsById(employer.getId())) {
            throw new IllegalArgumentException("Employer must not be null or must exist in the database");
        }
        return employerRepository.save(employer);
    }

    public void delete(Employer employer) {
        if (employer == null) {
            throw new IllegalArgumentException("Employer must not be null");
        }
        employerRepository.delete(employer);
    }
    ////Pour afficher la liste des employés assignés à cette activité.
    public List<Employer> findByActivities_Id(Long id) {
        List<Employer> employers= employerRepository.findByActivities_Id((id));
        if (!employers.isEmpty()) {
            return employers;
        }else {
         return null;
        }
    }


}
