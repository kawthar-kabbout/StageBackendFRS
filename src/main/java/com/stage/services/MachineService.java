package com.stage.services;


import com.stage.dto.MachineDTO;
import com.stage.persistans.Machine;
import com.stage.persistans.enums.ActivityType;
import com.stage.persistans.enums.MachineType;
import com.stage.repositories.ActivityRepository;
import com.stage.repositories.MachineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MachineService {
    private final MachineRepository machineRepository;
    private final ActivityService activityService;



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

    public MachineDTO getMachineDTo (Machine machine) {
        MachineDTO machineDTO = new MachineDTO(
                machine.getId(),
                machine.getName(),
                machine.getCapabilityMachines()
        );
        if (activityService.getMachineActivitiesNotFinish(machine)!= null) {
            machineDTO.setActivitiesNotFinish( activityService.getMachineActivitiesNotFinish(machine));
        }

        return machineDTO;
    }

    public List<MachineDTO> getALlMachineDTO () {

        List<MachineDTO> machineDTOs = new ArrayList<>();
        List<Machine> machines = machineRepository.findAll();
        for (Machine m : machines) {
            MachineDTO machineDTO = this.getMachineDTo(m);
            machineDTOs.add(machineDTO);
        }
        return machineDTOs;
    }


    public Machine update(Long id, Machine machine) {
      if(machineRepository.findById(id).isPresent()) {
          Machine m=machineRepository.findById(id).get();
          m.setName(machine.getName());
          m.setSerialNumber(machine.getSerialNumber());
          m.setMachineType(machine.getMachineType());
          m.setCapabilityMachines(machine.getCapabilityMachines());

          m.setStatus(machine.getStatus());
          m.setMarque(machine.getMarque());
          machineRepository.save(m);
          return m;
      }else
          return null;
    }

    public List<MachineType> getTypes() {
        return Arrays.asList(MachineType.values());
    }
    public boolean changeMachineStatus(Long id , Machine machine) {
        if (machineRepository.findById(id).isPresent()) {
            Machine m=machineRepository.findById(id).get();
            if (machine.getStatus()){
                m.setStatus(false);
                machineRepository.save(m);
                return true;
            }else {
                m.setStatus(true);
                machineRepository.save(m);
                return true;
            }
        }
        else
            return false;
    }


}
