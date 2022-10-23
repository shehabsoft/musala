import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MedicationFormService, MedicationFormGroup } from './medication-form.service';
import { IMedication } from '../medication.model';
import { MedicationService } from '../service/medication.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IDrone } from 'app/entities/musala/drone/drone.model';
import { DroneService } from 'app/entities/musala/drone/service/drone.service';

@Component({
  selector: 'jhi-medication-update',
  templateUrl: './medication-update.component.html',
})
export class MedicationUpdateComponent implements OnInit {
  isSaving = false;
  medication: IMedication | null = null;

  dronesSharedCollection: IDrone[] = [];

  editForm: MedicationFormGroup = this.medicationFormService.createMedicationFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected medicationService: MedicationService,
    protected medicationFormService: MedicationFormService,
    protected droneService: DroneService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareDrone = (o1: IDrone | null, o2: IDrone | null): boolean => this.droneService.compareDrone(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ medication }) => {
      this.medication = medication;
      if (medication) {
        this.updateForm(medication);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('musalaApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const medication = this.medicationFormService.getMedication(this.editForm);
    if (medication.id !== null) {
      this.subscribeToSaveResponse(this.medicationService.update(medication));
    } else {
      this.subscribeToSaveResponse(this.medicationService.create(medication));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMedication>>): void {
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

  protected updateForm(medication: IMedication): void {
    this.medication = medication;
    this.medicationFormService.resetForm(this.editForm, medication);

    this.dronesSharedCollection = this.droneService.addDroneToCollectionIfMissing<IDrone>(this.dronesSharedCollection, medication.drone);
  }

  protected loadRelationshipsOptions(): void {
    this.droneService
      .query()
      .pipe(map((res: HttpResponse<IDrone[]>) => res.body ?? []))
      .pipe(map((drones: IDrone[]) => this.droneService.addDroneToCollectionIfMissing<IDrone>(drones, this.medication?.drone)))
      .subscribe((drones: IDrone[]) => (this.dronesSharedCollection = drones));
  }
}
