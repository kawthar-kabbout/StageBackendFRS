package com.stage.persistans;

import com.stage.persistans.enums.MachineType;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true,nullable = false)
    private String SerialNumber;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MachineType machineType;
    @NonNull
    @Column(nullable = false)
    private String marque;
    @NonNull
    private Boolean status  =true;

    @ManyToMany
    private List<Activity> activities;
    @ManyToMany
    private List<Skill> skills;


}
