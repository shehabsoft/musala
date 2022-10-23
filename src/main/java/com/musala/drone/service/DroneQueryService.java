package com.musala.drone.service;

import com.musala.drone.domain.*; // for static metamodels
import com.musala.drone.domain.Drone;
import com.musala.drone.repository.DroneRepository;
import com.musala.drone.service.criteria.DroneCriteria;
import com.musala.drone.service.dto.DroneDTO;
import com.musala.drone.service.mapper.DroneMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.SingularAttribute;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;
import tech.jhipster.service.filter.IntegerFilter;

/**
 * Service for executing complex queries for {@link Drone} entities in the database.
 * The main input is a {@link DroneCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DroneDTO} or a {@link Page} of {@link DroneDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DroneQueryService extends QueryService<Drone> {

    private final Logger log = LoggerFactory.getLogger(DroneQueryService.class);

    private final DroneRepository droneRepository;

    private final DroneMapper droneMapper;

    public DroneQueryService(DroneRepository droneRepository, DroneMapper droneMapper) {
        this.droneRepository = droneRepository;
        this.droneMapper = droneMapper;
    }

    /**
     * Return a {@link List} of {@link DroneDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DroneDTO> findByCriteria(DroneCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Drone> specification = createSpecification(criteria);
        return droneMapper.toDto(droneRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DroneDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DroneDTO> findByCriteria(DroneCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Drone> specification = createSpecification(criteria);
        return droneRepository.findAll(specification, page).map(droneMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DroneCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Drone> specification = createSpecification(criteria);
        return droneRepository.count(specification);
    }

    /**
     * Function to convert {@link DroneCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Drone> createSpecification(DroneCriteria criteria) {
        Specification<Drone> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Drone_.id));
            }
            if (criteria.getSerialNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getSerialNumber(), Drone_.serialNumber));
            }
            if (criteria.getModel() != null) {
                specification = specification.and(buildSpecification(criteria.getModel(), Drone_.model));
            }
            if (criteria.getWeightLimit() != null) {
                specification = specification.and(buildSpecification(criteria.getWeightLimit(), Drone_.weightLimit));
            }
            if (criteria.getBatteryCapacity() != null) {
                specification = specification.and(buildSpecification(criteria.getBatteryCapacity(), Drone_.batteryCapacity));
            }
            if (criteria.getState() != null) {
                specification = specification.and(buildSpecification(criteria.getState(), Drone_.state));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), Drone_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), Drone_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), Drone_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), Drone_.lastModifiedDate));
            }
            if (criteria.getDroneMedicationsId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getDroneMedicationsId(),
                            root -> root.join(Drone_.droneMedications, JoinType.LEFT).get(Medication_.id)
                        )
                    );
            }
        }
        return specification;
    }


}
