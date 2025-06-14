package com.stage.persistans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String firstName;
    @NonNull
    private String lastName;
    @Column(unique = true, updatable = false)
    private String  matricule;

    @Column(unique = true, nullable = false)
    @NonNull

    private String phone;
    @NonNull
    @Column(nullable = false)
    private String address;

    @Column(unique = true, updatable = false)
    private String email;


    @NonNull
    @ManyToMany
    private List<Skill>skills;
    @NonNull
    @ManyToMany
    private List<CapabilityMachine>CapabilityMachine;

    @NotBlank
    private String grade;
    @NonNull
    private Boolean status  =true;

    @ManyToOne
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private WorkTime workTime;

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





}
