package com.musala.drone.web.rest;

import com.musala.drone.repository.AuditRepository;
import com.musala.drone.service.AuditQueryService;
import com.musala.drone.service.AuditService;
import com.musala.drone.service.criteria.AuditCriteria;
import com.musala.drone.service.dto.AuditDTO;
import com.musala.drone.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.musala.drone.domain.Audit}.
 */
@RestController
@RequestMapping("/api")
public class AuditResource {

    private final Logger log = LoggerFactory.getLogger(AuditResource.class);

    private static final String ENTITY_NAME = "audit";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AuditService auditService;

    private final AuditRepository auditRepository;

    private final AuditQueryService auditQueryService;

    public AuditResource(AuditService auditService, AuditRepository auditRepository, AuditQueryService auditQueryService) {
        this.auditService = auditService;
        this.auditRepository = auditRepository;
        this.auditQueryService = auditQueryService;
    }

    /**
     * {@code POST  /audits} : Create a new audit.
     *
     * @param auditDTO the auditDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new auditDTO, or with status {@code 400 (Bad Request)} if the audit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/audits")
    public ResponseEntity<AuditDTO> createAudit(@Valid @RequestBody AuditDTO auditDTO) throws URISyntaxException {
        log.debug("REST request to save Audit : {}", auditDTO);
        if (auditDTO.getId() != null) {
            throw new BadRequestAlertException("A new audit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AuditDTO result = auditService.save(auditDTO);
        return ResponseEntity
            .created(new URI("/api/audits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /audits/:id} : Updates an existing audit.
     *
     * @param id the id of the auditDTO to save.
     * @param auditDTO the auditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditDTO,
     * or with status {@code 400 (Bad Request)} if the auditDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the auditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/audits/{id}")
    public ResponseEntity<AuditDTO> updateAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AuditDTO auditDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Audit : {}, {}", id, auditDTO);
        if (auditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        AuditDTO result = auditService.update(auditDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /audits/:id} : Partial updates given fields of an existing audit, field will ignore if it is null
     *
     * @param id the id of the auditDTO to save.
     * @param auditDTO the auditDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated auditDTO,
     * or with status {@code 400 (Bad Request)} if the auditDTO is not valid,
     * or with status {@code 404 (Not Found)} if the auditDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the auditDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/audits/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AuditDTO> partialUpdateAudit(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AuditDTO auditDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Audit partially : {}, {}", id, auditDTO);
        if (auditDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, auditDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!auditRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AuditDTO> result = auditService.partialUpdate(auditDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, auditDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /audits} : get all the audits.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of audits in body.
     */
    @GetMapping("/audits")
    public ResponseEntity<List<AuditDTO>> getAllAudits(
        AuditCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get Audits by criteria: {}", criteria);
        Page<AuditDTO> page = auditQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /audits/count} : count all the audits.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/audits/count")
    public ResponseEntity<Long> countAudits(AuditCriteria criteria) {
        log.debug("REST request to count Audits by criteria: {}", criteria);
        return ResponseEntity.ok().body(auditQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /audits/:id} : get the "id" audit.
     *
     * @param id the id of the auditDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the auditDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/audits/{id}")
    public ResponseEntity<AuditDTO> getAudit(@PathVariable Long id) {
        log.debug("REST request to get Audit : {}", id);
        Optional<AuditDTO> auditDTO = auditService.findOne(id);
        return ResponseUtil.wrapOrNotFound(auditDTO);
    }

    /**
     * {@code DELETE  /audits/:id} : delete the "id" audit.
     *
     * @param id the id of the auditDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/audits/{id}")
    public ResponseEntity<Void> deleteAudit(@PathVariable Long id) {
        log.debug("REST request to delete Audit : {}", id);
        auditService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
