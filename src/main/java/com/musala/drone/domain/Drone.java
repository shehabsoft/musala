package com.musala.drone.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.musala.drone.domain.enumeration.Model;
import com.musala.drone.domain.enumeration.State;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * SysDomain (sys_domain) entity.\n@author Shehab Tarek.
 */
@Entity
@Table(name = "drone")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Drone implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "model", nullable = false)
    private Model model;

    @NotNull
    @Column(name = "weight_limit", nullable = false)
    private Integer weightLimit;

    @NotNull
    @Column(name = "battery_capacity", nullable = false)
    private Long batteryCapacity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private State state;

    @NotNull
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @NotNull
    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @OneToMany(mappedBy = "drone")
    @JsonIgnoreProperties(value = { "drone" }, allowSetters = true)
    private Set<Medication> droneMedications = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    @Column(name="audit_flage_notification")
    private Boolean auditFlageNotification;



    public Boolean getAuditFlageNotification() {
        return auditFlageNotification;
    }

    public void setAuditFlageNotification(Boolean auditFlageNotification) {
        this.auditFlageNotification = auditFlageNotification;
    }

    public Long getId() {
        return this.id;
    }

    public Drone id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return this.serialNumber;
    }

    public Drone serialNumber(String serialNumber) {
        this.setSerialNumber(serialNumber);
        return this;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Model getModel() {
        return this.model;
    }

    public Drone model(Model model) {
        this.setModel(model);
        return this;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Integer getWeightLimit() {
        return this.weightLimit;
    }

    public Drone weightLimit(Integer weightLimit) {
        this.setWeightLimit(weightLimit);
        return this;
    }

    public void setWeightLimit(Integer weightLimit) {
        this.weightLimit = weightLimit;
    }

    public Long getBatteryCapacity() {
        return this.batteryCapacity;
    }

    public Drone batteryCapacity(Long batteryCapacity) {
        this.setBatteryCapacity(batteryCapacity);
        return this;
    }

    public void setBatteryCapacity(Long batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public State getState() {
        return this.state;
    }

    public Drone state(State state) {
        this.setState(state);
        return this;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Drone createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Drone createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public Drone lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public Drone lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Set<Medication> getDroneMedications() {
        return this.droneMedications;
    }

    public void setDroneMedications(Set<Medication> medications) {
        if (this.droneMedications != null) {
            this.droneMedications.forEach(i -> i.setDrone(null));
        }
        if (medications != null) {
            medications.forEach(i -> i.setDrone(this));
        }
        this.droneMedications = medications;
    }

    public Drone droneMedications(Set<Medication> medications) {
        this.setDroneMedications(medications);
        return this;
    }

    public Drone addDroneMedications(Medication medication) {
        this.droneMedications.add(medication);
        medication.setDrone(this);
        return this;
    }

    public Drone removeDroneMedications(Medication medication) {
        this.droneMedications.remove(medication);
        medication.setDrone(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Drone)) {
            return false;
        }
        return id != null && id.equals(((Drone) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Drone{" +
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
