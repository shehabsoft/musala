import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMedication, NewMedication } from '../medication.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMedication for edit and NewMedicationFormGroupInput for create.
 */
type MedicationFormGroupInput = IMedication | PartialWithRequiredKeyOf<NewMedication>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMedication | NewMedication> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type MedicationFormRawValue = FormValueOf<IMedication>;

type NewMedicationFormRawValue = FormValueOf<NewMedication>;

type MedicationFormDefaults = Pick<NewMedication, 'id' | 'createdDate' | 'lastModifiedDate'>;

type MedicationFormGroupContent = {
  id: FormControl<MedicationFormRawValue['id'] | NewMedication['id']>;
  name: FormControl<MedicationFormRawValue['name']>;
  weight: FormControl<MedicationFormRawValue['weight']>;
  code: FormControl<MedicationFormRawValue['code']>;
  image: FormControl<MedicationFormRawValue['image']>;
  imageContentType: FormControl<MedicationFormRawValue['imageContentType']>;
  createdBy: FormControl<MedicationFormRawValue['createdBy']>;
  createdDate: FormControl<MedicationFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<MedicationFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<MedicationFormRawValue['lastModifiedDate']>;
  drone: FormControl<MedicationFormRawValue['drone']>;
};

export type MedicationFormGroup = FormGroup<MedicationFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MedicationFormService {
  createMedicationFormGroup(medication: MedicationFormGroupInput = { id: null }): MedicationFormGroup {
    const medicationRawValue = this.convertMedicationToMedicationRawValue({
      ...this.getFormDefaults(),
      ...medication,
    });
    return new FormGroup<MedicationFormGroupContent>({
      id: new FormControl(
        { value: medicationRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(medicationRawValue.name, {
        validators: [Validators.required],
      }),
      weight: new FormControl(medicationRawValue.weight, {
        validators: [Validators.required],
      }),
      code: new FormControl(medicationRawValue.code, {
        validators: [Validators.required],
      }),
      image: new FormControl(medicationRawValue.image),
      imageContentType: new FormControl(medicationRawValue.imageContentType),
      createdBy: new FormControl(medicationRawValue.createdBy, {
        validators: [Validators.required],
      }),
      createdDate: new FormControl(medicationRawValue.createdDate, {
        validators: [Validators.required],
      }),
      lastModifiedBy: new FormControl(medicationRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(medicationRawValue.lastModifiedDate),
      drone: new FormControl(medicationRawValue.drone),
    });
  }

  getMedication(form: MedicationFormGroup): IMedication | NewMedication {
    return this.convertMedicationRawValueToMedication(form.getRawValue() as MedicationFormRawValue | NewMedicationFormRawValue);
  }

  resetForm(form: MedicationFormGroup, medication: MedicationFormGroupInput): void {
    const medicationRawValue = this.convertMedicationToMedicationRawValue({ ...this.getFormDefaults(), ...medication });
    form.reset(
      {
        ...medicationRawValue,
        id: { value: medicationRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MedicationFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertMedicationRawValueToMedication(
    rawMedication: MedicationFormRawValue | NewMedicationFormRawValue
  ): IMedication | NewMedication {
    return {
      ...rawMedication,
      createdDate: dayjs(rawMedication.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawMedication.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertMedicationToMedicationRawValue(
    medication: IMedication | (Partial<NewMedication> & MedicationFormDefaults)
  ): MedicationFormRawValue | PartialWithRequiredKeyOf<NewMedicationFormRawValue> {
    return {
      ...medication,
      createdDate: medication.createdDate ? medication.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: medication.lastModifiedDate ? medication.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
