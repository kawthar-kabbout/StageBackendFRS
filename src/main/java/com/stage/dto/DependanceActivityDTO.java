package com.stage.dto;

import com.stage.persistans.Activity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@ToString

public class DependanceActivityDTO {

    private Long id;
    private Activity targetActivity;
    private Map<Long, String> predecessorActivity;
   private Map<Long, String> successorActivity;

    private LocalDateTime plannedStartDate;
    private LocalDateTime effectiveStartDate;

    private LocalDateTime plannedEndDate;
    private LocalDateTime effectiveEndDate;
}
