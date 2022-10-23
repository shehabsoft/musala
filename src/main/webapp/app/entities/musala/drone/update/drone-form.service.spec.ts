import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../drone.test-samples';

import { DroneFormService } from './drone-form.service';

describe('Drone Form Service', () => {
  let service: DroneFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DroneFormService);
  });

  describe('Service methods', () => {
    describe('createDroneFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDroneFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            serialNumber: expect.any(Object),
            model: expect.any(Object),
            weightLimit: expect.any(Object),
            batteryCapacity: expect.any(Object),
            state: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          })
        );
      });

      it('passing IDrone should create a new form with FormGroup', () => {
        const formGroup = service.createDroneFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            serialNumber: expect.any(Object),
            model: expect.any(Object),
            weightLimit: expect.any(Object),
            batteryCapacity: expect.any(Object),
            state: expect.any(Object),
            createdBy: expect.any(Object),
            createdDate: expect.any(Object),
            lastModifiedBy: expect.any(Object),
            lastModifiedDate: expect.any(Object),
          })
        );
      });
    });

    describe('getDrone', () => {
      it('should return NewDrone for default Drone initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDroneFormGroup(sampleWithNewData);

        const drone = service.getDrone(formGroup) as any;

        expect(drone).toMatchObject(sampleWithNewData);
      });

      it('should return NewDrone for empty Drone initial value', () => {
        const formGroup = service.createDroneFormGroup();

        const drone = service.getDrone(formGroup) as any;

        expect(drone).toMatchObject({});
      });

      it('should return IDrone', () => {
        const formGroup = service.createDroneFormGroup(sampleWithRequiredData);

        const drone = service.getDrone(formGroup) as any;

        expect(drone).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDrone should not enable id FormControl', () => {
        const formGroup = service.createDroneFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDrone should disable id FormControl', () => {
        const formGroup = service.createDroneFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
