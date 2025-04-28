package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.persistans.Machine;
import com.stage.persistans.enums.StatutActivity;
import jakarta.persistence.Column;
import lombok.*;

import java.util.List;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ToString
public class ProjetDTO {
    private Long id;
    private String name;
    private Long projectTemplateId;
    private StatutActivity statut;
    private List<Activity> activites;
 
}
