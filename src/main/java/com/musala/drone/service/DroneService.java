package com.musala.drone.service;

import com.musala.drone.service.dto.DroneDTO;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.musala.drone.domain.Drone}.
 */
public interface DroneService {
    /**
     * Save a drone.
     *
     * @param droneDTO the entity to save.
     * @return the persisted entity.
     */
    DroneDTO save(DroneDTO droneDTO);

    /**
     * Updates a drone.
     *
     * @param droneDTO the entity to update.
     * @return the persisted entity.
     */
    DroneDTO update(DroneDTO droneDTO);

    /**
     * Partially updates a drone.
     *
     * @param droneDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DroneDTO> partialUpdate(DroneDTO droneDTO);



    Optional<List<DroneDTO>> getDronesWithBatteryCapacity(Long batteryCapacity);

    /**
     * Get all the drones.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DroneDTO> findAll(Pageable pageable);

    /**
     * Get the "id" drone.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DroneDTO> findOne(Long id);

    /**
     * Delete the "id" drone.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
