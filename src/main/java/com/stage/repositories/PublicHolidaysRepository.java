package com.stage.repositories;

import com.stage.persistans.PublicHolidays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PublicHolidaysRepository extends JpaRepository<PublicHolidays, Long> {
    Optional<PublicHolidays> findByName(String name);
   // Optional<PublicHolidays> findByStartDatePublicHolidaysAndEndDatePublicHolidays(LocalDateTime start, int end);
}
