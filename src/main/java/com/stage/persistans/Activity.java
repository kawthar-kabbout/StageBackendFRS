package com.stage.persistans;

import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString
@Entity
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private StatutActivity statut;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActivityType typeActivity;

    @ManyToOne
    private Activity parentActivity;

    @NonNull
    @ManyToOne
    private Project project;

        @ManyToMany
        private List<Skill> skills;

    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;

    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;

    public Activity(@NonNull String name,
                    StatutActivity statut, @NonNull ActivityType typeActivity,
                    @NonNull Project project, LocalDateTime plannedStartDate,
                    LocalDateTime plannedEndDate) {
        this.name = name;
        this.statut = statut;
        this.typeActivity = typeActivity;
        this.project = project;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
    }
}
