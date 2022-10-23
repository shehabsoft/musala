package com.musala.drone.service.mapper;

import com.musala.drone.domain.Drone;
import com.musala.drone.domain.Medication;
import com.musala.drone.service.dto.DroneDTO;
import com.musala.drone.service.dto.MedicationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Medication} and its DTO {@link MedicationDTO}.
 */
@Mapper(componentModel = "spring")
public interface MedicationMapper extends EntityMapper<MedicationDTO, Medication> {
    @Mapping(target = "drone", source = "drone", qualifiedByName = "droneId")
    MedicationDTO toDto(Medication s);

    @Named("droneId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DroneDTO toDtoDroneId(Drone drone);
}
