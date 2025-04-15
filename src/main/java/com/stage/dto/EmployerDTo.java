package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.CapabilityMachine;
import com.stage.persistans.Skill;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@ToString

@RequiredArgsConstructor
@AllArgsConstructor
public class EmployerDTo {
    private Long id;
    private String firstName;
    private String lastName;
    private List<Activity> activitiesNotFinish;
    private List<Skill> skills= new ArrayList<>();
    private List<CapabilityMachine> capabilityMachine = new ArrayList<>();


    public EmployerDTo(Long id, String firstName, String lastName, List<Skill> skills, List<CapabilityMachine> capabilityMachine) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.skills = skills;
        this.capabilityMachine = capabilityMachine;
    }
}
