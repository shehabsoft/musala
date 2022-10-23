package com.musala.drone.service.mapper;

import com.musala.drone.domain.Drone;
import com.musala.drone.service.dto.DroneDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Drone} and its DTO {@link DroneDTO}.
 */
@Mapper(componentModel = "spring")
public interface DroneMapper extends EntityMapper<DroneDTO, Drone> {}
