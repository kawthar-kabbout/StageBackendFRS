package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.CapabilityMachine;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class MachineDTO {
    private Long id;
    private String name;
    private List<Activity> activitiesNotFinish;
    private List<CapabilityMachine> capabilityMachine;

    public MachineDTO(Long id, String name, List<CapabilityMachine> capabilityMachine) {
        this.id = id;
        this.name = name;
        this.capabilityMachine = capabilityMachine;
    }
}
