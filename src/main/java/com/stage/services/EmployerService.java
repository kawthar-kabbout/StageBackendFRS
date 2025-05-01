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
List<Employer>employers = new ArrayList<>();
List<Employer> employerList = employerRepository.findAll();
for (Employer employer:employerList){
    if (employer.getArchived()==0)
        employers.add(employer);
}
return employers;
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

public Employer findEmployerByPhone(String phone) {
        Optional<Employer> employer = employerRepository.findByPhone(phone);
        if (employer.isPresent()) {
            return employer.get();
        }
        return null;
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

    public boolean delete(Employer employer) {
        Employer employerToDelete = employerRepository.findById(employer.getId()).orElse(null);
        if (employerToDelete != null) {
            employerToDelete.setArchived(1);
            employerRepository.save(employerToDelete);
            return true;
        }
        return false;

    }

public Employer updateStatus(Employer employer) {
        Employer employerToUpdate = employerRepository.findById(employer.getId()).get();

        if (employerToUpdate != null) {
            employerToUpdate.setStatus(employer.getStatus());
            employerRepository.save(employerToUpdate);
            return employerToUpdate;
        }
        return null;
}

}
