package com.stage.persistans;

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

    public Project(@NonNull String name) {
        this.name = name;
    }


}
