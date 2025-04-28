package com.stage.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HolidaysDTO {


    private Long id;

    private String name;
    private Long startDatePublicHolidays;
    private int  nbdays;
}
