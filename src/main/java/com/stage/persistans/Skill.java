package com.stage.persistans;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@ToString

@Entity
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @NonNull
    @NotBlank
    @Column(unique = true, nullable = false)
    private String name;
    @NonNull
    @NotBlank
    @Column(nullable = false)
    private String description;

    public Skill(@NonNull String name, @NonNull String description) {
        this.name = name;
        this.description = description;
    }
}
