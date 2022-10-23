import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDrone } from '../drone.model';
import { DroneService } from '../service/drone.service';

@Injectable({ providedIn: 'root' })
export class DroneRoutingResolveService implements Resolve<IDrone | null> {
  constructor(protected service: DroneService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDrone | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((drone: HttpResponse<IDrone>) => {
          if (drone.body) {
            return of(drone.body);
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
