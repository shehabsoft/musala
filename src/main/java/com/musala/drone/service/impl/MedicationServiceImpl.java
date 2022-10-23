package com.musala.drone.service.impl;

import com.musala.drone.domain.Drone;
import com.musala.drone.domain.Medication;
import com.musala.drone.domain.enumeration.State;
import com.musala.drone.repository.MedicationRepository;
import com.musala.drone.service.DroneService;
import com.musala.drone.service.MedicationService;
import com.musala.drone.service.dto.DroneDTO;
import com.musala.drone.service.dto.MedicationDTO;
import com.musala.drone.service.mapper.DroneMapper;
import com.musala.drone.service.mapper.MedicationMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing {@link Medication}.
 */
@Service
@Transactional
public class MedicationServiceImpl implements MedicationService {

    private final Logger log = LoggerFactory.getLogger(MedicationServiceImpl.class);

    private final MedicationRepository medicationRepository;

    private final DroneService droneSerive;

    private final MedicationMapper medicationMapper;

    private final DroneMapper droneMapper;


    public MedicationServiceImpl(MedicationRepository medicationRepository, MedicationMapper medicationMapper,DroneService droneSerive,DroneMapper droneMapper) {
        this.medicationRepository = medicationRepository;
        this.medicationMapper = medicationMapper;
        this.droneSerive=droneSerive;
        this.droneMapper=droneMapper;
    }

    @Override
    public MedicationDTO save(MedicationDTO medicationDTO) throws Exception {
        log.debug("Request to save Medication : {}", medicationDTO);
        Medication medication = medicationMapper.toEntity(medicationDTO);
        Optional<DroneDTO> one = droneSerive.findOne(medicationDTO.getDrone().getId());
        validateMedication(medicationDTO);
        droneSerive.save(one.get());


        medication = medicationRepository.save(medication);
            return medicationMapper.toDto(medication);
    }

    @Override
    public MedicationDTO update(MedicationDTO medicationDTO) throws Exception {
        log.debug("Request to update Medication : {}", medicationDTO);

        Medication medication = medicationMapper.toEntity(medicationDTO);
        validateMedication(medicationDTO);

        medication = medicationRepository.save(medication);
        return medicationMapper.toDto(medication);
    }
    private void validateMedication(MedicationDTO medicationDTO) throws Exception {
        Long droneID = medicationDTO.getDrone().getId();
        Optional<DroneDTO> one = droneSerive.findOne(droneID);

        Integer newWeight= medicationDTO.getWeight();
        List<MedicationDTO> medicationsByDrone = getMedicationsByDrone(one.get());
        Integer sum = medicationsByDrone.stream().mapToInt(value -> value.getWeight()).sum();
        if( one.get().getWeightLimit()<sum+newWeight)
        {
            throw new Exception("This Drone Is Fully Loaded and Max Wight is "+ one.get().getWeightLimit()+" Please specify another one ");
        }else if(one.get().getWeightLimit() ==sum+newWeight)
        {
            one.get().setState(State.LOADED);


        }else  if(one.get().getWeightLimit() >=sum+newWeight){
            if(one.get().getBatteryCapacity()<25)
            {
                throw new Exception("The  Drone Battery Capacity is less than 25 %  ");
            }else {
                one.get().setState(State.LOADING);
            }
        }

    }
    public List<MedicationDTO> getMedicationsByDrone(DroneDTO droneDTO)
    {
        List<MedicationDTO> medicationDTOList = medicationRepository.findMedicationByDrone(droneMapper.toEntity(droneDTO)).stream().map(medication -> medicationMapper.toDto(medication)).collect(Collectors.toList());
        return  medicationDTOList;

    }
    @Override
    public Optional<MedicationDTO> partialUpdate(MedicationDTO medicationDTO) {
        log.debug("Request to partially update Medication : {}", medicationDTO);

        return medicationRepository
            .findById(medicationDTO.getId())
            .map(existingMedication -> {


                medicationMapper.partialUpdate(existingMedication, medicationDTO);

                return existingMedication;
            })
            .map(medicationRepository::save)
            .map(medicationMapper::toDto);

    }

    @Override
    @Transactional(readOnly = true)
    public Page<MedicationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Medications");
        return medicationRepository.findAll(pageable).map(medicationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MedicationDTO> findOne(Long id) {
        log.debug("Request to get Medication : {}", id);
        return medicationRepository.findById(id).map(medicationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Medication : {}", id);
        medicationRepository.deleteById(id);
    }
}
