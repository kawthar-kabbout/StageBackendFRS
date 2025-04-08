package com.stage.persistans;

import com.stage.persistans.enums.ActivityType;
import com.stage.persistans.enums.StatutActivity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ToString
@Entity
@Builder
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    @NotBlank
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private StatutActivity statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType typeActivity;

    @ManyToOne
    private Activity parentActivity;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Employer> employees = new ArrayList<>();

    @ManyToOne
    private Machine machine ;

    @NonNull
    @ManyToOne
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    private Skill skill;

    @ManyToOne
    private CapabilityMachine capabilityMachine;

    private Long activityTemplateId;
    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;
    private Integer duration;
    private int employersNumber;



    public Activity(@NonNull String name, ActivityType typeActivity, @NonNull Project project, CapabilityMachine capabilityMachine, Skill skill, Integer duration, int employersNumber) {
        this.name = name;
        this.typeActivity = typeActivity;
        this.project = project;
        this.capabilityMachine = capabilityMachine;
        this.skill = skill;
        this.duration = duration;
        this.employersNumber = employersNumber;
    }

    public <E> Activity(String s, Activity activity1, ActivityType activityType, Project p2, List<E> s2) {
    }



    public <E> Activity(String s, ActivityType activityType, Project p2, Activity activity2, List<E> s2) {
    }


}
