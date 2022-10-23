package com.musala.drone.service.impl;

import com.musala.drone.domain.Audit;
import com.musala.drone.repository.AuditRepository;
import com.musala.drone.service.AuditService;
import com.musala.drone.service.dto.AuditDTO;
import com.musala.drone.service.mapper.AuditMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Audit}.
 */
@Service
@Transactional
public class AuditServiceImpl implements AuditService {

    private final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    private final AuditRepository auditRepository;

    private final AuditMapper auditMapper;

    public AuditServiceImpl(AuditRepository auditRepository, AuditMapper auditMapper) {
        this.auditRepository = auditRepository;
        this.auditMapper = auditMapper;
    }

    @Override
    public AuditDTO save(AuditDTO auditDTO) {
        log.debug("Request to save Audit : {}", auditDTO);
        Audit audit = auditMapper.toEntity(auditDTO);
        audit = auditRepository.save(audit);
        return auditMapper.toDto(audit);
    }

    @Override
    public AuditDTO update(AuditDTO auditDTO) {
        log.debug("Request to update Audit : {}", auditDTO);
        Audit audit = auditMapper.toEntity(auditDTO);
        audit = auditRepository.save(audit);
        return auditMapper.toDto(audit);
    }

    @Override
    public Optional<AuditDTO> partialUpdate(AuditDTO auditDTO) {
        log.debug("Request to partially update Audit : {}", auditDTO);

        return auditRepository
            .findById(auditDTO.getId())
            .map(existingAudit -> {
                auditMapper.partialUpdate(existingAudit, auditDTO);

                return existingAudit;
            })
            .map(auditRepository::save)
            .map(auditMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Audits");
        return auditRepository.findAll(pageable).map(auditMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AuditDTO> findOne(Long id) {
        log.debug("Request to get Audit : {}", id);
        return auditRepository.findById(id).map(auditMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Audit : {}", id);
        auditRepository.deleteById(id);
    }
}
