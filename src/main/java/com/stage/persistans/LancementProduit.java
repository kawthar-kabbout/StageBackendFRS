package com.stage.persistans;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
@Getter
@Setter
@Entity

public class LancementProduit {
     @Id
     @GeneratedValue(strategy = GenerationType.SEQUENCE)
     private Long id;

     private String activity;
     private LocalDateTime plannedStartDate;
     private LocalDateTime effectiveStartDate;

     private LocalDateTime plannedEndDate;
     private LocalDateTime effectiveEndDate;
     private String dependencyActivity;

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
