import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AuditComponent } from '../list/audit.component';
import { AuditDetailComponent } from '../detail/audit-detail.component';
import { AuditUpdateComponent } from '../update/audit-update.component';
import { AuditRoutingResolveService } from './audit-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const auditRoute: Routes = [
  {
    path: '',
    component: AuditComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AuditDetailComponent,
    resolve: {
      audit: AuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AuditUpdateComponent,
    resolve: {
      audit: AuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AuditUpdateComponent,
    resolve: {
      audit: AuditRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(auditRoute)],
  exports: [RouterModule],
})
export class AuditRoutingModule {}
