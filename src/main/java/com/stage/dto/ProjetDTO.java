package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.enums.Statut;
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
    private Statut statut;
    private List<Activity> activites;
 
}
