package com.stage.persistans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class DatePlanningResult {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

}
