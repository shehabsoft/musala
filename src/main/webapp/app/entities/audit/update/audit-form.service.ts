import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IAudit, NewAudit } from '../audit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAudit for edit and NewAuditFormGroupInput for create.
 */
type AuditFormGroupInput = IAudit | PartialWithRequiredKeyOf<NewAudit>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IAudit | NewAudit> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

type AuditFormRawValue = FormValueOf<IAudit>;

type NewAuditFormRawValue = FormValueOf<NewAudit>;

type AuditFormDefaults = Pick<NewAudit, 'id' | 'createdDate' | 'lastModifiedDate'>;

type AuditFormGroupContent = {
  id: FormControl<AuditFormRawValue['id'] | NewAudit['id']>;
  message: FormControl<AuditFormRawValue['message']>;
  createdBy: FormControl<AuditFormRawValue['createdBy']>;
  createdDate: FormControl<AuditFormRawValue['createdDate']>;
  lastModifiedBy: FormControl<AuditFormRawValue['lastModifiedBy']>;
  lastModifiedDate: FormControl<AuditFormRawValue['lastModifiedDate']>;
};

export type AuditFormGroup = FormGroup<AuditFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AuditFormService {
  createAuditFormGroup(audit: AuditFormGroupInput = { id: null }): AuditFormGroup {
    const auditRawValue = this.convertAuditToAuditRawValue({
      ...this.getFormDefaults(),
      ...audit,
    });
    return new FormGroup<AuditFormGroupContent>({
      id: new FormControl(
        { value: auditRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      message: new FormControl(auditRawValue.message, {
        validators: [Validators.required],
      }),
      createdBy: new FormControl(auditRawValue.createdBy, {
        validators: [Validators.required],
      }),
      createdDate: new FormControl(auditRawValue.createdDate, {
        validators: [Validators.required],
      }),
      lastModifiedBy: new FormControl(auditRawValue.lastModifiedBy),
      lastModifiedDate: new FormControl(auditRawValue.lastModifiedDate),
    });
  }

  getAudit(form: AuditFormGroup): IAudit | NewAudit {
    return this.convertAuditRawValueToAudit(form.getRawValue() as AuditFormRawValue | NewAuditFormRawValue);
  }

  resetForm(form: AuditFormGroup, audit: AuditFormGroupInput): void {
    const auditRawValue = this.convertAuditToAuditRawValue({ ...this.getFormDefaults(), ...audit });
    form.reset(
      {
        ...auditRawValue,
        id: { value: auditRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): AuditFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdDate: currentTime,
      lastModifiedDate: currentTime,
    };
  }

  private convertAuditRawValueToAudit(rawAudit: AuditFormRawValue | NewAuditFormRawValue): IAudit | NewAudit {
    return {
      ...rawAudit,
      createdDate: dayjs(rawAudit.createdDate, DATE_TIME_FORMAT),
      lastModifiedDate: dayjs(rawAudit.lastModifiedDate, DATE_TIME_FORMAT),
    };
  }

  private convertAuditToAuditRawValue(
    audit: IAudit | (Partial<NewAudit> & AuditFormDefaults)
  ): AuditFormRawValue | PartialWithRequiredKeyOf<NewAuditFormRawValue> {
    return {
      ...audit,
      createdDate: audit.createdDate ? audit.createdDate.format(DATE_TIME_FORMAT) : undefined,
      lastModifiedDate: audit.lastModifiedDate ? audit.lastModifiedDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
