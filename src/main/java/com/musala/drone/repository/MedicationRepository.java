package com.musala.drone.repository;

import com.musala.drone.domain.Drone;
import com.musala.drone.domain.Medication;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for the Medication entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long>, JpaSpecificationExecutor<Medication> {

    public List<Medication> findMedicationByDrone(Drone drone);
}
