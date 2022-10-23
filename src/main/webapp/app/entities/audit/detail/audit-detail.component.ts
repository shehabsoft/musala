import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAudit } from '../audit.model';

@Component({
  selector: 'jhi-audit-detail',
  templateUrl: './audit-detail.component.html',
})
export class AuditDetailComponent implements OnInit {
  audit: IAudit | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ audit }) => {
      this.audit = audit;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
