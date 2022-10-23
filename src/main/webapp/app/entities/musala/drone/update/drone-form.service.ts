import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IDrone, NewDrone } from '../drone.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDrone for edit and NewDroneFormGroupInput for create.
 */
type DroneFormGroupInput = IDrone | PartialWithRequiredKeyOf<NewDrone>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IDrone | NewDrone> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type DroneFormRawValue = FormValueOf<IDrone>;

type NewDroneFormRawValue = FormValueOf<NewDrone>;

type DroneFormDefaults = Pick<NewDrone, 'id' | 'createdDate' | 'lastModifiedDate'>;

type DroneFormGroupContent = {
  id: FormControl<DroneFormRawValue['id'] | NewDrone['id']>;
  serialNumber: FormControl<DroneFormRawValue['serialNumber']>;
  model: FormControl<DroneFormRawValue['model']>;
  weightLimit: FormControl<DroneFormRawValue['weightLimit']>;
  batteryCapacity: FormControl<DroneFormRawValue['batteryCapacity']>;
  state: FormControl<DroneFormRawValue['state']>;
  createdBy: FormControl<DroneFormRawValue['createdBy']>;
  createdDate: FormControl<DroneFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<DroneFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<DroneFormRawValue['lastModifiedDate']>;
};

export type DroneFormGroup = FormGroup<DroneFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DroneFormService {
  createDroneFormGroup(drone: DroneFormGroupInput = { id: null }): DroneFormGroup {
    const droneRawValue = this.convertDroneToDroneRawValue({
      ...this.getFormDefaults(),
      ...drone,
    });
    return new FormGroup<DroneFormGroupContent>({
      id: new FormControl(
        { value: droneRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      serialNumber: new FormControl(droneRawValue.serialNumber, {
        validators: [Validators.required],
      }),
      model: new FormControl(droneRawValue.model, {
        validators: [Validators.required],
      }),
      weightLimit: new FormControl(droneRawValue.weightLimit, {
        validators: [Validators.required],
      }),
      batteryCapacity: new FormControl(droneRawValue.batteryCapacity, {
        validators: [Validators.required],
      }),
      state: new FormControl(droneRawValue.state, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(droneRawValue.createdBy, {
        validators: [Validators.required],
      }),
      createdDate: new FormControl(droneRawValue.createdDate, {
        validators: [Validators.required],
      }),
      lastModifiedBy: new FormControl(droneRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(droneRawValue.lastModifiedDate),
    });
  }

  getDrone(form: DroneFormGroup): IDrone | NewDrone {
    return this.convertDroneRawValueToDrone(form.getRawValue() as DroneFormRawValue | NewDroneFormRawValue);
  }

  resetForm(form: DroneFormGroup, drone: DroneFormGroupInput): void {
    const droneRawValue = this.convertDroneToDroneRawValue({ ...this.getFormDefaults(), ...drone });
    form.reset(
      {
        ...droneRawValue,
        id: { value: droneRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): DroneFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertDroneRawValueToDrone(rawDrone: DroneFormRawValue | NewDroneFormRawValue): IDrone | NewDrone {
    return {
      ...rawDrone,
      createdDate: dayjs(rawDrone.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawDrone.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertDroneToDroneRawValue(
    drone: IDrone | (Partial<NewDrone> & DroneFormDefaults)
  ): DroneFormRawValue | PartialWithRequiredKeyOf<NewDroneFormRawValue> {
    return {
      ...drone,
      createdDate: drone.createdDate ? drone.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: drone.lastModifiedDate ? drone.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
