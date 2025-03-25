package com.stage.persistans;

import com.stage.persistans.enums.ActivityType;
import com.stage.persistans.enums.StatutActivity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.Duration;
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
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Machine> machines = new ArrayList<>();
    @NonNull
    @ManyToOne
    private Project project;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Skill> skills;

    private Long activityTemplateId;
    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;
    private Integer duration;


    public Activity(@NonNull String name, @NonNull ActivityType typeActivity, @NonNull Project project, List<Skill> skills) {
        this.name = name;
        this.typeActivity = typeActivity;
        this.project = project;
        this.skills = skills;
    }

    public <E> Activity(String s, Activity activity1, ActivityType activityType, Project p2, List<E> s2) {
    }



    public <E> Activity(String s, ActivityType activityType, Project p2, Activity activity2, List<E> s2) {
    }
}
