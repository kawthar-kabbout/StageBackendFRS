package com.stage.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@ToString

@AllArgsConstructor
@NoArgsConstructor
public class PublicHolidaysDTO {

    private Long id;
    private String name;
    private LocalDateTime startDatePublicHolidays;
    private LocalDateTime endDatePublicHolidays;
}
