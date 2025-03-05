package com.stage.repositories;

import com.stage.persistans.DependanceActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DependanceActivityRepository extends JpaRepository<DependanceActivity, Long> {


        @Query(value = "SELECT COUNT(*) FROM dependance_activity m " +
                "WHERE m.target_activity_id = ?1 AND m.predecessor_activity_id = ?2",
                nativeQuery = true)
        int countByTargetAndPredecessorNative(Long targetActivityId, Long predecessorActivityId);

}
