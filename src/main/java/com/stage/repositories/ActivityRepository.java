package com.stage.repositories;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,Long> {
    Optional<Activity> findByName(String nom);
    long countByProjectId(Long projectId);
    List<Activity> findByProject_Id(Long id);

    List<Activity> findByProjectIdAndParentActivityIsNull(Long projectId);

    List<Activity> findByParentActivity(Activity activity);
}
