package com.musala.drone.service.mapper;

import com.musala.drone.domain.Audit;
import com.musala.drone.service.dto.AuditDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Audit} and its DTO {@link AuditDTO}.
 */
@Mapper(componentModel = "spring")
public interface AuditMapper extends EntityMapper<AuditDTO, Audit> {}
