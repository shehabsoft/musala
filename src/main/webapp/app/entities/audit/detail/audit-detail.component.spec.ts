import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AuditDetailComponent } from './audit-detail.component';

describe('Audit Management Detail Component', () => {
  let comp: AuditDetailComponent;
  let fixture: ComponentFixture<AuditDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AuditDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ audit: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AuditDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AuditDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load audit on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.audit).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
