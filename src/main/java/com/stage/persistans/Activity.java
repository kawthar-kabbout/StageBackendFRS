package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


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
    @Column(unique = true, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private StatutActivity statut;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeActivity typeActivity;

    @ManyToOne
    private Activity parentActivity;

    @NonNull
    @ManyToOne
    private Project project;



    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;

    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;

    public Activity(@NonNull String name,
                    StatutActivity statut, @NonNull TypeActivity typeActivity,
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
