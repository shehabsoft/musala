import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'drone',
        data: { pageTitle: 'Drones' },
        loadChildren: () => import('./musala/drone/drone.module').then(m => m.MusalaDroneModule),
      },
      {
        path: 'medication',
        data: { pageTitle: 'Medications' },
        loadChildren: () => import('./musala/medication/medication.module').then(m => m.MusalaMedicationModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
