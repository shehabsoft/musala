package com.musala.drone.repository;

import com.musala.drone.domain.Drone;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the Drone entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DroneRepository extends JpaRepository<Drone, Long>, JpaSpecificationExecutor<Drone> {

    Optional<List<Drone>> findAllByBatteryCapacityLessThanAndAuditFlageNotification(Long battery,Boolean audit);
}
