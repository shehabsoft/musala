import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDrone } from '../drone.model';

@Component({
  selector: 'jhi-drone-detail',
  templateUrl: './drone-detail.component.html',
})
export class DroneDetailComponent implements OnInit {
  drone: IDrone | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ drone }) => {
      this.drone = drone;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
