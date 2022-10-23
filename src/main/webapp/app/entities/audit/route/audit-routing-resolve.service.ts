import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAudit } from '../audit.model';
import { AuditService } from '../service/audit.service';

@Injectable({ providedIn: 'root' })
export class AuditRoutingResolveService implements Resolve<IAudit | null> {
  constructor(protected service: AuditService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAudit | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((audit: HttpResponse<IAudit>) => {
          if (audit.body) {
            return of(audit.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
