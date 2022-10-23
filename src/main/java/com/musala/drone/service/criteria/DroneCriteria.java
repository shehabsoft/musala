package com.musala.drone.service.criteria;

import com.musala.drone.domain.enumeration.Model;
import com.musala.drone.domain.enumeration.State;
import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.musala.drone.domain.Drone} entity. This class is used
 * in {@link com.musala.drone.web.rest.DroneResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /drones?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DroneCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Model
     */
    public static class ModelFilter extends Filter<Model> {

        public ModelFilter() {}

        public ModelFilter(ModelFilter filter) {
            super(filter);
        }

        @Override
        public ModelFilter copy() {
            return new ModelFilter(this);
        }
    }

    /**
     * Class for filtering State
     */
    public static class StateFilter extends Filter<State> {

        public StateFilter() {}

        public StateFilter(StateFilter filter) {
            super(filter);
        }

        @Override
        public StateFilter copy() {
            return new StateFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter serialNumber;

    private ModelFilter model;

    private IntegerFilter weightLimit;

    private LongFilter batteryCapacity;

    private StateFilter state;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private LongFilter droneMedicationsId;

    private Boolean distinct;

    public DroneCriteria() {}

    public DroneCriteria(DroneCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.serialNumber = other.serialNumber == null ? null : other.serialNumber.copy();
        this.model = other.model == null ? null : other.model.copy();
        this.weightLimit = other.weightLimit == null ? null : other.weightLimit.copy();
        this.batteryCapacity = other.batteryCapacity == null ? null : other.batteryCapacity.copy();
        this.state = other.state == null ? null : other.state.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.createdDate = other.createdDate == null ? null : other.createdDate.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.lastModifiedDate = other.lastModifiedDate == null ? null : other.lastModifiedDate.copy();
        this.droneMedicationsId = other.droneMedicationsId == null ? null : other.droneMedicationsId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DroneCriteria copy() {
        return new DroneCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getSerialNumber() {
        return serialNumber;
    }

    public StringFilter serialNumber() {
        if (serialNumber == null) {
            serialNumber = new StringFilter();
        }
        return serialNumber;
    }

    public void setSerialNumber(StringFilter serialNumber) {
        this.serialNumber = serialNumber;
    }

    public ModelFilter getModel() {
        return model;
    }

    public ModelFilter model() {
        if (model == null) {
            model = new ModelFilter();
        }
        return model;
    }

    public void setModel(ModelFilter model) {
        this.model = model;
    }

    public IntegerFilter getWeightLimit() {
        return weightLimit;
    }

    public IntegerFilter weightLimit() {
        if (weightLimit == null) {
            weightLimit = new IntegerFilter();
        }
        return weightLimit;
    }

    public void setWeightLimit(IntegerFilter weightLimit) {
        this.weightLimit = weightLimit;
    }

    public LongFilter getBatteryCapacity() {
        return batteryCapacity;
    }

    public LongFilter batteryCapacity() {
        if (batteryCapacity == null) {
            batteryCapacity = new LongFilter();
        }
        return batteryCapacity;
    }

    public void setBatteryCapacity(LongFilter batteryCapacity) {
        this.batteryCapacity = batteryCapacity;
    }

    public StateFilter getState() {
        return state;
    }

    public StateFilter state() {
        if (state == null) {
            state = new StateFilter();
        }
        return state;
    }

    public void setState(StateFilter state) {
        this.state = state;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            createdDate = new InstantFilter();
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            lastModifiedBy = new StringFilter();
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            lastModifiedDate = new InstantFilter();
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LongFilter getDroneMedicationsId() {
        return droneMedicationsId;
    }

    public LongFilter droneMedicationsId() {
        if (droneMedicationsId == null) {
            droneMedicationsId = new LongFilter();
        }
        return droneMedicationsId;
    }

    public void setDroneMedicationsId(LongFilter droneMedicationsId) {
        this.droneMedicationsId = droneMedicationsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DroneCriteria that = (DroneCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(serialNumber, that.serialNumber) &&
            Objects.equals(model, that.model) &&
            Objects.equals(weightLimit, that.weightLimit) &&
            Objects.equals(batteryCapacity, that.batteryCapacity) &&
            Objects.equals(state, that.state) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(droneMedicationsId, that.droneMedicationsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            serialNumber,
            model,
            weightLimit,
            batteryCapacity,
            state,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            droneMedicationsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DroneCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (serialNumber != null ? "serialNumber=" + serialNumber + ", " : "") +
            (model != null ? "model=" + model + ", " : "") +
            (weightLimit != null ? "weightLimit=" + weightLimit + ", " : "") +
            (batteryCapacity != null ? "batteryCapacity=" + batteryCapacity + ", " : "") +
            (state != null ? "state=" + state + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (createdDate != null ? "createdDate=" + createdDate + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (lastModifiedDate != null ? "lastModifiedDate=" + lastModifiedDate + ", " : "") +
            (droneMedicationsId != null ? "droneMedicationsId=" + droneMedicationsId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
