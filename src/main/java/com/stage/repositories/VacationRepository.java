package com.stage.repositories;

import com.stage.persistans.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VacationRepository extends JpaRepository<Vacation, Long> {
    Optional<Vacation> findByName(String name);

    Optional<Vacation> findByStartDate(LocalDateTime startDate);
    // Optional<PublicHolidays> findByStartDatePublicHolidaysAndEndDatePublicHolidays(LocalDateTime start, int end);
}
