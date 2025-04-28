package com.stage.persistans;

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
    @Column(unique = true, nullable = false)
    @NonNull

    private String phone;
    @NonNull
    @Column(nullable = false)
    private String address;
    @NonNull
    @ManyToMany
    private List<Skill>skills;
    @NonNull
    @ManyToMany
    private List<CapabilityMachine>CapabilityMachine;

    @NotBlank
    private String grade;

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
