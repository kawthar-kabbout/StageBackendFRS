package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Builder
public class WorkTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime morningSessionStart;

    @Column(nullable = false)
    private LocalTime morningSessionEnd;

    @Column(nullable = false)
    private LocalTime afternoonSessionStart;

    @Column(nullable = false)
    private LocalTime afternoonSessionEnd;

    // Liste des jours travaillés (ex: Lundi, Mardi, Samedi...)
    @ElementCollection
    @CollectionTable(name = "work_time_working_days")
    @Column(name = "day")
    private Set<DayOfWeek> workingDays;

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

    /**
     * Vérifie si le jour est travaillé
     */
    public boolean isWorkingDay(DayOfWeek day) {
        return workingDays != null && workingDays.contains(day);
    }

    /**
     * Génère toutes les heures valides entre planningStart et planningEnd
     */
    public List<Integer> getAllValidHours(LocalDateTime planningStart, LocalDateTime planningEnd) {
        List<Integer> validHours = new ArrayList<>();
        LocalDateTime current = planningStart;

        while (!current.isAfter(planningEnd)) {
            DayOfWeek day = current.getDayOfWeek();

            if (!isWorkingDay(day)) {
                current = current.with(LocalTime.MIDNIGHT).plusDays(1);
                continue;
            }

            // Ajout des heures du matin
            if (morningSessionStart != null && morningSessionEnd != null) {
                LocalDateTime start = current.with(morningSessionStart);
                LocalDateTime end = current.with(morningSessionEnd);
                for (LocalDateTime t = start; t.isBefore(end); t = t.plusHours(1)) {
                    validHours.add((int) Duration.between(planningStart, t).toHours());
                }
            }

            // Ajout des heures de l'après-midi
            if (afternoonSessionStart != null && afternoonSessionEnd != null) {
                LocalDateTime start = current.with(afternoonSessionStart);
                LocalDateTime end = current.with(afternoonSessionEnd);
                for (LocalDateTime t = start; t.isBefore(end); t = t.plusHours(1)) {
                    validHours.add((int) Duration.between(planningStart, t).toHours());
                }
            }

            current = current.with(LocalTime.MIDNIGHT).plusDays(1);
        }

        return validHours;
    }

    /**
     * Constructeur avec validation des données
     */
    public WorkTime(
            LocalTime morningSessionStart,
            LocalTime morningSessionEnd,
            LocalTime afternoonSessionStart,
            LocalTime afternoonSessionEnd,
            Set<DayOfWeek> workingDays) {
        this.morningSessionStart = morningSessionStart;
        this.morningSessionEnd = morningSessionEnd;
        this.afternoonSessionStart = afternoonSessionStart;
        this.afternoonSessionEnd = afternoonSessionEnd;
        this.workingDays = workingDays == null ? new HashSet<>(Arrays.asList(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
        )) : workingDays;
    }
}