import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../audit.test-samples';

import { AuditFormService } from './audit-form.service';

describe('Audit Form Service', () => {
  let service: AuditFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuditFormService);
  });

  describe('Service methods', () => {
    describe('createAuditFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createAuditFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          })
        );
      });

      it('passing IAudit should create a new form with FormGroup', () => {
        const formGroup = service.createAuditFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            message: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          })
        );
      });
    });

    describe('getAudit', () => {
      it('should return NewAudit for default Audit initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createAuditFormGroup(sampleWithNewData);

        const audit = service.getAudit(formGroup) as any;

        expect(audit).toMatchObject(sampleWithNewData);
      });

      it('should return NewAudit for empty Audit initial value', () => {
        const formGroup = service.createAuditFormGroup();

        const audit = service.getAudit(formGroup) as any;

        expect(audit).toMatchObject({});
      });

      it('should return IAudit', () => {
        const formGroup = service.createAuditFormGroup(sampleWithRequiredData);

        const audit = service.getAudit(formGroup) as any;

        expect(audit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IAudit should not enable id FormControl', () => {
        const formGroup = service.createAuditFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewAudit should disable id FormControl', () => {
        const formGroup = service.createAuditFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
