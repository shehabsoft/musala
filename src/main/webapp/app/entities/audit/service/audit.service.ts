import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAudit, NewAudit } from '../audit.model';

export type PartialUpdateAudit = Partial<IAudit> & Pick<IAudit, 'id'>;

type RestOf<T extends IAudit | NewAudit> = Omit<T, 'createdDate' | 'lastModifiedDate'> & {
  createdDate?: string | null;
  lastModifiedDate?: string | null;
};

export type RestAudit = RestOf<IAudit>;

export type NewRestAudit = RestOf<NewAudit>;

export type PartialUpdateRestAudit = RestOf<PartialUpdateAudit>;

export type EntityResponseType = HttpResponse<IAudit>;
export type EntityArrayResponseType = HttpResponse<IAudit[]>;

@Injectable({ providedIn: 'root' })
export class AuditService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/audits');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(audit: NewAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(audit);
    return this.http.post<RestAudit>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(audit: IAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(audit);
    return this.http
      .put<RestAudit>(`${this.resourceUrl}/${this.getAuditIdentifier(audit)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(audit: PartialUpdateAudit): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(audit);
    return this.http
      .patch<RestAudit>(`${this.resourceUrl}/${this.getAuditIdentifier(audit)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestAudit>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestAudit[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getAuditIdentifier(audit: Pick<IAudit, 'id'>): number {
    return audit.id;
  }

  compareAudit(o1: Pick<IAudit, 'id'> | null, o2: Pick<IAudit, 'id'> | null): boolean {
    return o1 && o2 ? this.getAuditIdentifier(o1) === this.getAuditIdentifier(o2) : o1 === o2;
  }

  addAuditToCollectionIfMissing<Type extends Pick<IAudit, 'id'>>(
    auditCollection: Type[],
    ...auditsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const audits: Type[] = auditsToCheck.filter(isPresent);
    if (audits.length > 0) {
      const auditCollectionIdentifiers = auditCollection.map(auditItem => this.getAuditIdentifier(auditItem)!);
      const auditsToAdd = audits.filter(auditItem => {
        const auditIdentifier = this.getAuditIdentifier(auditItem);
        if (auditCollectionIdentifiers.includes(auditIdentifier)) {
          return false;
        }
        auditCollectionIdentifiers.push(auditIdentifier);
        return true;
      });
      return [...auditsToAdd, ...auditCollection];
    }
    return auditCollection;
  }

  protected convertDateFromClient<T extends IAudit | NewAudit | PartialUpdateAudit>(audit: T): RestOf<T> {
    return {
      ...audit,
      createdDate: audit.createdDate?.toJSON() ?? null,
      lastModifiedDate: audit.lastModifiedDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restAudit: RestAudit): IAudit {
    return {
      ...restAudit,
      createdDate: restAudit.createdDate ? dayjs(restAudit.createdDate) : undefined,
      lastModifiedDate: restAudit.lastModifiedDate ? dayjs(restAudit.lastModifiedDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestAudit>): HttpResponse<IAudit> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestAudit[]>): HttpResponse<IAudit[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
