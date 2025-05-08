package com.stage.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class VacationDTO {


    private Long id;

    private String name;
    private Long startDate;
    private int  nbdays;
}
