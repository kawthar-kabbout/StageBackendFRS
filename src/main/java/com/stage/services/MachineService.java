package com.stage.services;


import com.stage.persistans.Machine;
import com.stage.repositories.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;



    //public  {}
    public List<Machine> findAll() {
        return machineRepository.findAll();
    }
    public Optional<Machine> findById(Long id) {
        return machineRepository.findById(id);
    }

    public Machine save(Machine machine) {
        return machineRepository.save(machine);
    }

    public void delete(Machine machine) {
        machineRepository.delete(machine);
    }

    public List<Machine> finbByIdActivity(Long id) {
       List<Machine>machines=machineRepository.findByActivities_Id(id);
        if (!machines.isEmpty()) {
       return machines;
       }else {
           return null;
       }
    }


    public Machine update(Machine machine ) {
        if (machineRepository.findById(machine.getId()).isPresent()) {
            return machineRepository.save(machine);
        } else {
            return null;
        }
    }



}
