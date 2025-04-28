package com.stage.persistans;

import com.stage.persistans.enums.MachineType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString

@AllArgsConstructor
@NoArgsConstructor
public class PublicHolidays {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String name;
    private LocalDateTime startDatePublicHolidays;
    private int nbdays;

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


    public PublicHolidays(Long id, String name, LocalDateTime startDatePublicHolidays, int nbdays) {
        this.id = id;
        this.name = name;
        this.startDatePublicHolidays = startDatePublicHolidays;
        this.nbdays = nbdays;
    }
}
