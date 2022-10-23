package com.musala.drone.service;

import com.musala.drone.domain.*; // for static metamodels
import com.musala.drone.domain.Audit;
import com.musala.drone.repository.AuditRepository;
import com.musala.drone.service.criteria.AuditCriteria;
import com.musala.drone.service.dto.AuditDTO;
import com.musala.drone.service.mapper.AuditMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Audit} entities in the database.
 * The main input is a {@link AuditCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link AuditDTO} or a {@link Page} of {@link AuditDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class AuditQueryService extends QueryService<Audit> {

    private final Logger log = LoggerFactory.getLogger(AuditQueryService.class);

    private final AuditRepository auditRepository;

    private final AuditMapper auditMapper;

    public AuditQueryService(AuditRepository auditRepository, AuditMapper auditMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
    }

    /**
     * Return a {@link List} of {@link AuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<AuditDTO> findByCriteria(AuditCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Audit> specification = createSpecification(criteria);
        return auditMapper.toDto(auditRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link AuditDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<AuditDTO> findByCriteria(AuditCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Audit> specification = createSpecification(criteria);
        return auditRepository.findAll(specification, page).map(auditMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(AuditCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Audit> specification = createSpecification(criteria);
        return auditRepository.count(specification);
    }

    /**
     * Function to convert {@link AuditCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Audit> createSpecification(AuditCriteria criteria) {
        Specification<Audit> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Audit_.id));
            }
            if (criteria.getMessage() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMessage(), Audit_.message));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Audit_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Audit_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Audit_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Audit_.lastModifiedDate));
            }
        }
        return specification;
    }
}
