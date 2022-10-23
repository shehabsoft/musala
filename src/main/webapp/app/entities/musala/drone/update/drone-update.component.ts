import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { DroneFormService, DroneFormGroup } from './drone-form.service';
import { IDrone } from '../drone.model';
import { DroneService } from '../service/drone.service';
import { Model } from 'app/entities/enumerations/model.model';
import { State } from 'app/entities/enumerations/state.model';

@Component({
  selector: 'jhi-drone-update',
  templateUrl: './drone-update.component.html',
})
export class DroneUpdateComponent implements OnInit {
  isSaving = false;
  drone: IDrone | null = null;
  modelValues = Object.keys(Model);
  stateValues = Object.keys(State);

  editForm: DroneFormGroup = this.droneFormService.createDroneFormGroup();

  constructor(
    protected droneService: DroneService,
    protected droneFormService: DroneFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ drone }) => {
      this.drone = drone;
      if (drone) {
        this.updateForm(drone);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const drone = this.droneFormService.getDrone(this.editForm);
    if (drone.id !== null) {
      this.subscribeToSaveResponse(this.droneService.update(drone));
    } else {
      this.subscribeToSaveResponse(this.droneService.create(drone));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDrone>>): void {
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

  protected updateForm(drone: IDrone): void {
    this.drone = drone;
    this.droneFormService.resetForm(this.editForm, drone);
  }
}
