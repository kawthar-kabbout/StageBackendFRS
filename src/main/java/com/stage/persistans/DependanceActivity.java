package com.stage.persistans;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ToString
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames =
                {"targetActivity_id", "predecessorActivity_id"})
)
public class DependanceActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn( nullable = false)
    private Activity targetActivity;
    @ManyToOne
    @JoinColumn( nullable = false)
    private Activity predecessorActivity;
    @Column(nullable = false)
    private String dependencyType;
    @NonNull
    @ManyToOne
    private Project project;

}
