package com.stage.repositories;

import com.stage.persistans.DailyWorkTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyWorkTimeRepository extends JpaRepository<DailyWorkTime,Long> {
}
