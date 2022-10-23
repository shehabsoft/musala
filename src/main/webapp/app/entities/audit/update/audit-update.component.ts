import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AuditFormService, AuditFormGroup } from './audit-form.service';
import { IAudit } from '../audit.model';
import { AuditService } from '../service/audit.service';

@Component({
  selector: 'jhi-audit-update',
  templateUrl: './audit-update.component.html',
})
export class AuditUpdateComponent implements OnInit {
  isSaving = false;
  audit: IAudit | null = null;

  editForm: AuditFormGroup = this.auditFormService.createAuditFormGroup();

  constructor(
    protected auditService: AuditService,
    protected auditFormService: AuditFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ audit }) => {
      this.audit = audit;
      if (audit) {
        this.updateForm(audit);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const audit = this.auditFormService.getAudit(this.editForm);
    if (audit.id !== null) {
      this.subscribeToSaveResponse(this.auditService.update(audit));
    } else {
      this.subscribeToSaveResponse(this.auditService.create(audit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAudit>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(audit: IAudit): void {
    this.audit = audit;
    this.auditFormService.resetForm(this.editForm, audit);
  }
}
