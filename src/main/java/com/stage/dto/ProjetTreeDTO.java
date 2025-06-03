package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.enums.Statut;
import lombok.*;

import java.util.List;
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProjetTreeDTO {
    private Long id;
    private String name;
    private Long projectTemplateId;
    private Statut statut;
    private List<ActivityDTO> activites;
}
