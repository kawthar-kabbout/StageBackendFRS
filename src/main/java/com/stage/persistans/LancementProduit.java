package com.stage.persistans;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

}
