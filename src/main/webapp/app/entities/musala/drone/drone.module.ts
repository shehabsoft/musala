import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DroneComponent } from './list/drone.component';
import { DroneDetailComponent } from './detail/drone-detail.component';
import { DroneUpdateComponent } from './update/drone-update.component';
import { DroneDeleteDialogComponent } from './delete/drone-delete-dialog.component';
import { DroneRoutingModule } from './route/drone-routing.module';

@NgModule({
  imports: [SharedModule, DroneRoutingModule],
  declarations: [DroneComponent, DroneDetailComponent, DroneUpdateComponent, DroneDeleteDialogComponent],
})
export class MusalaDroneModule {}
