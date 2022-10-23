import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DroneDetailComponent } from './drone-detail.component';

describe('Drone Management Detail Component', () => {
  let comp: DroneDetailComponent;
  let fixture: ComponentFixture<DroneDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DroneDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ drone: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DroneDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DroneDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load drone on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.drone).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
