package com.musala.drone.service;

import com.musala.drone.service.dto.AuditDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.musala.drone.domain.Audit}.
 */
public interface AuditService {
    /**
     * Save a audit.
     *
     * @param auditDTO the entity to save.
     * @return the persisted entity.
     */
    AuditDTO save(AuditDTO auditDTO);

    /**
     * Updates a audit.
     *
     * @param auditDTO the entity to update.
     * @return the persisted entity.
     */
    AuditDTO update(AuditDTO auditDTO);

    /**
     * Partially updates a audit.
     *
     * @param auditDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AuditDTO> partialUpdate(AuditDTO auditDTO);

    /**
     * Get all the audits.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AuditDTO> findAll(Pageable pageable);

    /**
     * Get the "id" audit.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AuditDTO> findOne(Long id);

    /**
     * Delete the "id" audit.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
