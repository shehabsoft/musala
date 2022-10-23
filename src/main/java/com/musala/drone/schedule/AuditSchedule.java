package com.musala.drone.schedule;

import com.musala.drone.service.AuditService;
import com.musala.drone.service.DroneService;
import com.musala.drone.service.dto.AuditDTO;
import com.musala.drone.service.dto.DroneDTO;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
public class AuditSchedule {

    private final DroneService droneService;
    private final AuditService auditService;

    public  AuditSchedule(DroneService droneService,AuditService auditService)
    {
        this.droneService=droneService;
        this.auditService=  auditService;
    }

    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTaskAsync() throws InterruptedException {
        System.out.println(
            "Fixed rate task async - " + System.currentTimeMillis() / 1000);

        Optional<List<DroneDTO>> dronesWithBatteryCapacity = droneService.getDronesWithBatteryCapacity(Long.valueOf(20));
       AuditDTO auditDTO=new AuditDTO();
        dronesWithBatteryCapacity.ifPresent(droneDTOS ->
            {

                droneDTOS.stream().forEach(droneDTO -> {

                        auditDTO.setMessage("Drone " + droneDTO.getSerialNumber() + " is Less than 20 % and the Value is  :" +droneDTO.getBatteryCapacity());
                        auditDTO.setCreatedBy("AuditSystemJob");
                        auditDTO.setCreatedDate(Instant.now());
                        auditService.save(auditDTO);
                        droneDTO.setAuditFlageNotification(true);
                        droneService.update(droneDTO);
                    }

                    );

            }

            );

        Thread.sleep(2000);
    }

}
