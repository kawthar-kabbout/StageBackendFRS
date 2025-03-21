package com.stage.persistans;

import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
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
    @Column( nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private StatutActivity statut;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType typeActivity;
    @ManyToOne
    private Activity parentActivity;
    @NonNull
    @ManyToOne
    private Project project;
    @NotEmpty(message = "L'activité doit contenir au moins une compétence")
        @ManyToMany(fetch = FetchType.EAGER)
        private List<Skill> skills;

    private Long activityTemplateId;
    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;
    private Duration duration;

    public Activity(@NonNull String name, @NonNull ActivityType typeActivity, @NonNull Project project, List<Skill> skills) {
        this.name = name;
        this.typeActivity = typeActivity;
        this.project = project;
        this.skills = skills;
    }
}
