package com.stage.repositories;

import com.stage.dto.ActivityDTO;
import com.stage.persistans.Activity;
import com.stage.persistans.Project;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,Long> {
    Optional<Activity> findByName(String nom);
    Long countByProjectId(Long projectId);
    List<Activity> findByProject_Id(Long id);

    Activity findByActivityTemplateId(Long id);
    List<Activity> findByProjectIdAndParentActivityIsNull(Long projectId);

    @Query(value = "SELECT * FROM activity m WHERE m.project_id = :projectId AND m.name = :name LIMIT 1",
            nativeQuery = true)
    Optional<Activity> findByProjectIdAndName(@Param("projectId") Long projectId,
                                              @Param("name") String name);
    
    boolean existsActivityByProject_IdAndNameContainsIgnoreCase(Long projectId, @NonNull @NotBlank String name);

    List<Activity> findByParentActivity(Activity activity);

    @Query(value = "SELECT * FROM activity a WHERE a.activity_template_id = :templateId AND a.project_id = :projectId", nativeQuery = true)
    Activity findByActivityTemplateIdAndProjectId(@Param("templateId") Long templateId, @Param("projectId") Long projectId);

}
