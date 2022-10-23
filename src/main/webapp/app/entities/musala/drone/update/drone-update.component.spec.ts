import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DroneFormService } from './drone-form.service';
import { DroneService } from '../service/drone.service';
import { IDrone } from '../drone.model';

import { DroneUpdateComponent } from './drone-update.component';

describe('Drone Management Update Component', () => {
  let comp: DroneUpdateComponent;
  let fixture: ComponentFixture<DroneUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let droneFormService: DroneFormService;
  let droneService: DroneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DroneUpdateComponent],
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
      .overrideTemplate(DroneUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DroneUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    droneFormService = TestBed.inject(DroneFormService);
    droneService = TestBed.inject(DroneService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const drone: IDrone = { id: 456 };

      activatedRoute.data = of({ drone });
      comp.ngOnInit();

      expect(comp.drone).toEqual(drone);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDrone>>();
      const drone = { id: 123 };
      jest.spyOn(droneFormService, 'getDrone').mockReturnValue(drone);
      jest.spyOn(droneService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drone });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: drone }));
      saveSubject.complete();

      // THEN
      expect(droneFormService.getDrone).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(droneService.update).toHaveBeenCalledWith(expect.objectContaining(drone));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDrone>>();
      const drone = { id: 123 };
      jest.spyOn(droneFormService, 'getDrone').mockReturnValue({ id: null });
      jest.spyOn(droneService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drone: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: drone }));
      saveSubject.complete();

      // THEN
      expect(droneFormService.getDrone).toHaveBeenCalled();
      expect(droneService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDrone>>();
      const drone = { id: 123 };
      jest.spyOn(droneService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ drone });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(droneService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
