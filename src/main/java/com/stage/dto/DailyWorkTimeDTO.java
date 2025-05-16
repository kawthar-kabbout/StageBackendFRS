package com.stage.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DailyWorkTimeDTO {
    private Long id;
    private String day; // DayOfWeek en String
    private String morningStart;
    private String morningEnd;
    private String afternoonStart;
    private String afternoonEnd;
}
