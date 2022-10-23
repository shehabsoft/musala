package com.musala.drone.service.dto;

import com.musala.drone.domain.enumeration.Model;
import com.musala.drone.domain.enumeration.State;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.musala.drone.domain.Drone} entity.
 */
@Schema(description = "SysDomain (sys_domain) entity.\n@author Shehab Tarek.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneDTO implements Serializable {

    private Long id;

    @NotNull
    private String serialNumber;

    @NotNull
    private Model model;

    @NotNull
    private Integer weightLimit;

    @NotNull
    private Long batteryCapacity;

    @NotNull
    private State state;

    @NotNull
    private String createdBy;

    @NotNull
    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private Boolean auditFlageNotification;



    public Boolean getAuditFlageNotification() {
        return auditFlageNotification;
    }

    public void setAuditFlageNotification(Boolean auditFlageNotification) {
        this.auditFlageNotification = auditFlageNotification;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Integer getWeightLimit() {
        return weightLimit;
    }

    public void setWeightLimit(Integer weightLimit) {
        this.weightLimit = weightLimit;
    }

    public Long getBatteryCapacity() {
        return batteryCapacity;
    }

    public void setBatteryCapacity(Long batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DroneDTO)) {
            return false;
        }

        DroneDTO droneDTO = (DroneDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, droneDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneDTO{" +
            "id=" + getId() +
            ", serialNumber='" + getSerialNumber() + "'" +
            ", model='" + getModel() + "'" +
            ", weightLimit='" + getWeightLimit() + "'" +
            ", batteryCapacity='" + getBatteryCapacity() + "'" +
            ", state='" + getState() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
