package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @Column( nullable = false)
    private LocalTime morningSessionStart;

    @Column( nullable = false)
    private LocalTime morningSessionEnd;

    @Column( nullable = false)
    private LocalTime afternoonSessionStart;

    @Column(nullable = false)
    private LocalTime afternoonSessionEnd;

    private String weekendDay; // Exemple : "Samedi"
    private LocalTime weekendWorkTimeStart; // Exemple : 08:00
    private LocalTime weekendWorkTimeEnd; // Exemple : 12:00
    @ElementCollection
    private List<String> workingDays;
    @ElementCollection
    private List<String> noWorkingDays; // Liste des jours de travail (ex: ["Lundi", "Mardi"])


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


    public WorkTime(LocalTime morningSessionStart, LocalTime morningSessionEnd, LocalTime afternoonSessionStart, LocalTime afternoonSessionEnd, String weekendDay, LocalTime weekendWorkTimeStart, LocalTime weekendWorkTimeEnd, List<String> workingDays, List<String> noWorkingDays) {
        this.morningSessionStart = morningSessionStart;
        this.morningSessionEnd = morningSessionEnd;
        this.afternoonSessionStart = afternoonSessionStart;
        this.afternoonSessionEnd = afternoonSessionEnd;
        this.weekendDay = weekendDay;
        this.weekendWorkTimeStart = weekendWorkTimeStart;
        this.weekendWorkTimeEnd = weekendWorkTimeEnd;
        this.workingDays = workingDays;
        this.noWorkingDays = noWorkingDays;
    }
}
