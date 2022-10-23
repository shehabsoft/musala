import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMedication, NewMedication } from '../medication.model';

export type PartialUpdateMedication = Partial<IMedication> & Pick<IMedication, 'id'>;

type RestOf<T extends IMedication | NewMedication> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestMedication = RestOf<IMedication>;

export type NewRestMedication = RestOf<NewMedication>;

export type PartialUpdateRestMedication = RestOf<PartialUpdateMedication>;

export type EntityResponseType = HttpResponse<IMedication>;
export type EntityArrayResponseType = HttpResponse<IMedication[]>;

@Injectable({ providedIn: 'root' })
export class MedicationService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/medications');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(medication: NewMedication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(medication);
    return this.http
      .post<RestMedication>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(medication: IMedication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(medication);
    return this.http
      .put<RestMedication>(`${this.resourceUrl}/${this.getMedicationIdentifier(medication)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(medication: PartialUpdateMedication): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(medication);
    return this.http
      .patch<RestMedication>(`${this.resourceUrl}/${this.getMedicationIdentifier(medication)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestMedication>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestMedication[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getMedicationIdentifier(medication: Pick<IMedication, 'id'>): number {
    return medication.id;
  }

  compareMedication(o1: Pick<IMedication, 'id'> | null, o2: Pick<IMedication, 'id'> | null): boolean {
    return o1 && o2 ? this.getMedicationIdentifier(o1) === this.getMedicationIdentifier(o2) : o1 === o2;
  }

  addMedicationToCollectionIfMissing<Type extends Pick<IMedication, 'id'>>(
    medicationCollection: Type[],
    ...medicationsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const medications: Type[] = medicationsToCheck.filter(isPresent);
    if (medications.length > 0) {
      const medicationCollectionIdentifiers = medicationCollection.map(medicationItem => this.getMedicationIdentifier(medicationItem)!);
      const medicationsToAdd = medications.filter(medicationItem => {
        const medicationIdentifier = this.getMedicationIdentifier(medicationItem);
        if (medicationCollectionIdentifiers.includes(medicationIdentifier)) {
          return false;
        }
        medicationCollectionIdentifiers.push(medicationIdentifier);
        return true;
      });
      return [...medicationsToAdd, ...medicationCollection];
    }
    return medicationCollection;
  }

  protected convertDateFromClient<T extends IMedication | NewMedication | PartialUpdateMedication>(medication: T): RestOf<T> {
    return {
      ...medication,
      createdDate: medication.createdDate?.toJSON() ?? null,
      lastModifiedDate: medication.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restMedication: RestMedication): IMedication {
    return {
      ...restMedication,
      createdDate: restMedication.createdDate ? dayjs(restMedication.createdDate) : undefined,
      lastModifiedDate: restMedication.lastModifiedDate ? dayjs(restMedication.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestMedication>): HttpResponse<IMedication> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestMedication[]>): HttpResponse<IMedication[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
