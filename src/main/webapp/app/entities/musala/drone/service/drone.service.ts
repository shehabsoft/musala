import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDrone, NewDrone } from '../drone.model';

export type PartialUpdateDrone = Partial<IDrone> & Pick<IDrone, 'id'>;

type RestOf<T extends IDrone | NewDrone> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestDrone = RestOf<IDrone>;

export type NewRestDrone = RestOf<NewDrone>;

export type PartialUpdateRestDrone = RestOf<PartialUpdateDrone>;

export type EntityResponseType = HttpResponse<IDrone>;
export type EntityArrayResponseType = HttpResponse<IDrone[]>;

@Injectable({ providedIn: 'root' })
export class DroneService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/drones');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(drone: NewDrone): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(drone);
    return this.http.post<RestDrone>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(drone: IDrone): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(drone);
    return this.http
      .put<RestDrone>(`${this.resourceUrl}/${this.getDroneIdentifier(drone)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(drone: PartialUpdateDrone): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(drone);
    return this.http
      .patch<RestDrone>(`${this.resourceUrl}/${this.getDroneIdentifier(drone)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestDrone>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestDrone[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getDroneIdentifier(drone: Pick<IDrone, 'id'>): number {
    return drone.id;
  }

  compareDrone(o1: Pick<IDrone, 'id'> | null, o2: Pick<IDrone, 'id'> | null): boolean {
    return o1 && o2 ? this.getDroneIdentifier(o1) === this.getDroneIdentifier(o2) : o1 === o2;
  }

  addDroneToCollectionIfMissing<Type extends Pick<IDrone, 'id'>>(
    droneCollection: Type[],
    ...dronesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const drones: Type[] = dronesToCheck.filter(isPresent);
    if (drones.length > 0) {
      const droneCollectionIdentifiers = droneCollection.map(droneItem => this.getDroneIdentifier(droneItem)!);
      const dronesToAdd = drones.filter(droneItem => {
        const droneIdentifier = this.getDroneIdentifier(droneItem);
        if (droneCollectionIdentifiers.includes(droneIdentifier)) {
          return false;
        }
        droneCollectionIdentifiers.push(droneIdentifier);
        return true;
      });
      return [...dronesToAdd, ...droneCollection];
    }
    return droneCollection;
  }

  protected convertDateFromClient<T extends IDrone | NewDrone | PartialUpdateDrone>(drone: T): RestOf<T> {
    return {
      ...drone,
      createdDate: drone.createdDate?.toJSON() ?? null,
      lastModifiedDate: drone.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restDrone: RestDrone): IDrone {
    return {
      ...restDrone,
      createdDate: restDrone.createdDate ? dayjs(restDrone.createdDate) : undefined,
      lastModifiedDate: restDrone.lastModifiedDate ? dayjs(restDrone.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestDrone>): HttpResponse<IDrone> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestDrone[]>): HttpResponse<IDrone[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
