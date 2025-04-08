package com.stage.dto;

import com.stage.persistans.Activity;
import com.stage.persistans.Employer;
import com.stage.persistans.Machine;
import com.stage.persistans.Project;
import com.stage.persistans.enums.StatutActivity;
import com.stage.persistans.enums.ActivityType;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDTO {
    private Long id;
    private String name;
    private StatutActivity statutActivity;
    private ActivityType typeActivity;
    private Long parentActivityId;
    private Project projectId;
    private Long activityTemplateId;
    private Integer duration;
    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;
    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;
    private Map<Long, String> predecessorActivity;
    private Map<Long, String> successorActivity;
    private List<ActivityDTO> childActivities;
    private List<Employer>employees ;
    private Machine machine;



    public ActivityDTO(Activity a) {
    }

}
