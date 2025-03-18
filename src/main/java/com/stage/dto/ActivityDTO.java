package com.stage.dto;

import com.stage.persistans.Activity;
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
    private String nom;
    private StatutActivity statutActivity;
    private ActivityType typeActivity;
    private Long parentActivityId;
    private Long projectId;
    private Duration duration;
    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;

    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;
    private Map<Long, String> predecessorActivity;
    private Map<Long, String> successorActivity;
    private List<ActivityDTO> childActivities;



    public ActivityDTO(Activity a) {
    }


    public <E> ActivityDTO(Long id, @NonNull @NotBlank String name, StatutActivity statut, ActivityType typeActivity, Long aLong, Long id1, LocalDateTime plannedEndDate, LocalDateTime plannedStartDate, ArrayList<E> es) {
    }

    public <E, V, K> ActivityDTO(Long id, @NonNull @NotBlank String name, StatutActivity statut, ActivityType typeActivity, Long aLong, Long id1, ArrayList<E> es, LocalDateTime effectiveEndDate, LocalDateTime plannedEndDate, LocalDateTime effectiveStartDate, LocalDateTime plannedStartDate, HashMap<K,V> kvHashMap, Object o, HashMap<K,V> kvHashMap1) {
    }

    public <E, V, K> ActivityDTO(Long id, @NonNull @NotBlank String name, StatutActivity statut, ActivityType typeActivity, Long aLong, Long id1, LocalDateTime plannedEndDate, LocalDateTime plannedStartDate, ArrayList<E> es, HashMap<K,V> kvHashMap, HashMap<K,V> kvHashMap1) {
    }
}
