package com.stage.services;

import com.stage.dto.EmployerDTo;
import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.EmployerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployerService {
    private final EmployerRepository employerRepository;
private final ActivityService activityService;

    public EmployerDTo getEmployerDTo(Employer employer) {
        EmployerDTo employerDTo = new EmployerDTo(
                employer.getId(),
                employer.getFirstName(),
                employer.getLastName(),
               employer.getSkills(),
                employer.getCapabilityMachine()
        );
        if (activityService.getEmployerActivitiesNotFinish(employer) != null) {
            employerDTo.setActivitiesNotFinish(activityService.getEmployerActivitiesNotFinish(employer));
        }

        return employerDTo;
    }




    public List<Employer> findAll() {

        return employerRepository.findAll();
    }
    public List<EmployerDTo> getALLEmployerDTO() {
        List<Employer> employers = employerRepository.findAll();
        List<EmployerDTo> employerDTos = new ArrayList<>();
        for (Employer employer : employers) {
            EmployerDTo employerDTo = this.getEmployerDTo(employer);
            employerDTos.add(employerDTo);
        }
        return employerDTos;
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



}
