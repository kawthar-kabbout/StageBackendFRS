package com.stage.persistans;

import com.stage.persistans.enums.ActivityType;
import com.stage.persistans.enums.DependencyType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DependencyType dependencyType;
    private int delay;

    public DependanceActivity(Activity targetActivity, Activity predecessorActivity, DependencyType dependencyType) {
        this.targetActivity = targetActivity;
        this.predecessorActivity = predecessorActivity;
        this.dependencyType = dependencyType;
    }

    public DependanceActivity(Activity targetActivity, Activity predecessorActivity, DependencyType dependencyType, int delay) {
        this.targetActivity = targetActivity;
        this.predecessorActivity = predecessorActivity;
        this.dependencyType = dependencyType;
        this.delay = delay;
    }
}
