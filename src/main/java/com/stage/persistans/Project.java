package com.stage.persistans;

import com.stage.persistans.enums.StatutActivity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
//@EqualsAndHashCode (of = "name")
@NoArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NonNull
    @Column(unique = true, nullable = false)
    private String name;
    private Long projectTemplateId;
    private StatutActivity statut;

    public Project(@NonNull String name) {
        this.name = name;
    }


}
