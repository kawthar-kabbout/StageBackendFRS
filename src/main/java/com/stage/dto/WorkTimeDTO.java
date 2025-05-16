package com.stage.dto;

import lombok.*;

import java.util.List;
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class WorkTimeDTO {
    private Long id;
    private List<DailyWorkTimeDTO> dailyWorkTimes;
}
