import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DroneComponent } from '../list/drone.component';
import { DroneDetailComponent } from '../detail/drone-detail.component';
import { DroneUpdateComponent } from '../update/drone-update.component';
import { DroneRoutingResolveService } from './drone-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const droneRoute: Routes = [
  {
    path: '',
    component: DroneComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DroneDetailComponent,
    resolve: {
      drone: DroneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DroneUpdateComponent,
    resolve: {
      drone: DroneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DroneUpdateComponent,
    resolve: {
      drone: DroneRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(droneRoute)],
  exports: [RouterModule],
})
export class DroneRoutingModule {}
