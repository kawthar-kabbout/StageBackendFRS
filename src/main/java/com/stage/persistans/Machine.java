package com.stage.persistans;

import com.stage.persistans.enums.MachineType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(unique = true,nullable = false)
    private String serialNumber;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private MachineType machineType;
    @NonNull
    @Column(nullable = false)
    private String marque;
    @NonNull
    private Boolean status  =true;

@ManyToMany
    private List<CapabilityMachine> CapabilityMachines;

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
