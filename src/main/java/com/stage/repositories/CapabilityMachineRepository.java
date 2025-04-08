package com.stage.repositories;

import com.stage.persistans.CapabilityMachine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapabilityMachineRepository extends JpaRepository<CapabilityMachine, Long> {
}
