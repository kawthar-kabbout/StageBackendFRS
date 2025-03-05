package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Entity
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Employer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    @NonNull
    private int phone;
    @NonNull
    @Column(nullable = false)
    private String address;

    @NonNull
    @OneToMany
    private List<Activity> activities;


}
