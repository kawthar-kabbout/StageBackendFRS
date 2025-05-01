package com.stage.repositories;

import com.stage.persistans.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkTimeRepository extends JpaRepository<WorkTime, Long> {
}
