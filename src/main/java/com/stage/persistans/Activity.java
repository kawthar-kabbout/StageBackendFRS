package com.stage.persistans;

import com.stage.persistans.enums.ActivityType;
import com.stage.persistans.enums.Statut;
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
    private Statut statut;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType typeActivity;

    @ManyToOne
    private Activity parentActivity;

    @ManyToMany(fetch = FetchType.EAGER )
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

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime updatedDate;

    @Column(nullable = false)
    private int archived = 0;


    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

    public Activity(@NonNull String name, ActivityType typeActivity, @NonNull Project project, CapabilityMachine capabilityMachine, Skill skill, Integer duration, int employersNumber) {
        this.name = name;
        this.typeActivity = typeActivity;
        this.project = project;
        this.capabilityMachine = capabilityMachine;
        this.skill = skill;
        this.duration = duration;
        this.employersNumber = employersNumber;
    }


    public Activity(@NonNull String name, Statut statut, ActivityType typeActivity, Skill skill, @NonNull Project project, CapabilityMachine capabilityMachine, int employersNumber, Integer duration) {
        this.name = name;
        this.statut = statut;
        this.typeActivity = typeActivity;
        this.skill = skill;
        this.project = project;
        this.capabilityMachine = capabilityMachine;
        this.employersNumber = employersNumber;
        this.duration = duration;
    }

    public Activity(@NonNull String name,Activity parentActivity, ActivityType typeActivity, @NonNull Project project ,Integer duration) {
        this.name = name;
        this.parentActivity = parentActivity;
        this.typeActivity = typeActivity;
        this.project = project;
        this.duration = duration;
    }


    public Activity(@NonNull String name, Statut statut, ActivityType typeActivity, Activity parentActivity, @NonNull Project project, Skill skill, CapabilityMachine capabilityMachine, Integer duration, int employersNumber) {
        this.name = name;
        this.statut = statut;
        this.typeActivity = typeActivity;
        this.parentActivity = parentActivity;
        this.project = project;
        this.skill = skill;
        this.capabilityMachine = capabilityMachine;
        this.duration = duration;
        this.employersNumber = employersNumber;
    }

    public <E> Activity(String s, Activity activity1, ActivityType activityType, Project p2, List<E> s2) {
    }



    public <E> Activity(String s, ActivityType activityType, Project p2, Activity activity2, List<E> s2) {
    }


}
