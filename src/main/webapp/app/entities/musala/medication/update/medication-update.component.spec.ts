import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MedicationFormService } from './medication-form.service';
import { MedicationService } from '../service/medication.service';
import { IMedication } from '../medication.model';
import { IDrone } from 'app/entities/musala/drone/drone.model';
import { DroneService } from 'app/entities/musala/drone/service/drone.service';

import { MedicationUpdateComponent } from './medication-update.component';

describe('Medication Management Update Component', () => {
  let comp: MedicationUpdateComponent;
  let fixture: ComponentFixture<MedicationUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let medicationFormService: MedicationFormService;
  let medicationService: MedicationService;
  let droneService: DroneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MedicationUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(MedicationUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MedicationUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    medicationFormService = TestBed.inject(MedicationFormService);
    medicationService = TestBed.inject(MedicationService);
    droneService = TestBed.inject(DroneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Drone query and add missing value', () => {
      const medication: IMedication = { id: 456 };
      const drone: IDrone = { id: 27815 };
      medication.drone = drone;

      const droneCollection: IDrone[] = [{ id: 23836 }];
      jest.spyOn(droneService, 'query').mockReturnValue(of(new HttpResponse({ body: droneCollection })));
      const additionalDrones = [drone];
      const expectedCollection: IDrone[] = [...additionalDrones, ...droneCollection];
      jest.spyOn(droneService, 'addDroneToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      expect(droneService.query).toHaveBeenCalled();
      expect(droneService.addDroneToCollectionIfMissing).toHaveBeenCalledWith(
        droneCollection,
        ...additionalDrones.map(expect.objectContaining)
      );
      expect(comp.dronesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const medication: IMedication = { id: 456 };
      const drone: IDrone = { id: 39467 };
      medication.drone = drone;

      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      expect(comp.dronesSharedCollection).toContain(drone);
      expect(comp.medication).toEqual(medication);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedication>>();
      const medication = { id: 123 };
      jest.spyOn(medicationFormService, 'getMedication').mockReturnValue(medication);
      jest.spyOn(medicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: medication }));
      saveSubject.complete();

      // THEN
      expect(medicationFormService.getMedication).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(medicationService.update).toHaveBeenCalledWith(expect.objectContaining(medication));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedication>>();
      const medication = { id: 123 };
      jest.spyOn(medicationFormService, 'getMedication').mockReturnValue({ id: null });
      jest.spyOn(medicationService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ medication: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: medication }));
      saveSubject.complete();

      // THEN
      expect(medicationFormService.getMedication).toHaveBeenCalled();
      expect(medicationService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMedication>>();
      const medication = { id: 123 };
      jest.spyOn(medicationService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ medication });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(medicationService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDrone', () => {
      it('Should forward to droneService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(droneService, 'compareDrone');
        comp.compareDrone(entity, entity2);
        expect(droneService.compareDrone).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
