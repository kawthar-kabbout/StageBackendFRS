package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.TypeActivity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private Long id;
    private String nom;
    private StatutActivity statutActivity;
    private TypeActivity typeActivity;
    private Long parentActivityId;
    private Long projectId;
    private LocalDateTime dateFinPrevue;
    private  LocalDateTime dateDebutPrevue;
    private List<ActivityDTO>childActivities ;

    public ActivityDTO(Activity a) {
    }
}
