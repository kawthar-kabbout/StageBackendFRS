package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class Machine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    @Column(nullable = false)
    private String nom;
@NonNull
    @Column(nullable = false)
    private String marque;
    @NonNull
    private Boolean status  =true;

    @OneToMany
    private List<Activity> activities;


}
