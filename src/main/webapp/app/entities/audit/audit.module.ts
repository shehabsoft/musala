import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AuditComponent } from './list/audit.component';
import { AuditDetailComponent } from './detail/audit-detail.component';
import { AuditUpdateComponent } from './update/audit-update.component';
import { AuditDeleteDialogComponent } from './delete/audit-delete-dialog.component';
import { AuditRoutingModule } from './route/audit-routing.module';

@NgModule({
  imports: [SharedModule, AuditRoutingModule],
  declarations: [AuditComponent, AuditDetailComponent, AuditUpdateComponent, AuditDeleteDialogComponent],
})
export class MusalaAuditModule {}
