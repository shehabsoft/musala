import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDrone } from '../drone.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../drone.test-samples';

import { DroneService, RestDrone } from './drone.service';

const requireRestSample: RestDrone = {
  ...sampleWithRequiredData,
  createdDate: sampleWithRequiredData.createdDate?.toJSON(),
  lastModifiedDate: sampleWithRequiredData.lastModifiedDate?.toJSON(),
};

describe('Drone Service', () => {
  let service: DroneService;
  let httpMock: HttpTestingController;
  let expectedResult: IDrone | IDrone[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DroneService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Drone', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const drone = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(drone).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Drone', () => {
      const drone = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(drone).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Drone', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Drone', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Drone', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDroneToCollectionIfMissing', () => {
      it('should add a Drone to an empty array', () => {
        const drone: IDrone = sampleWithRequiredData;
        expectedResult = service.addDroneToCollectionIfMissing([], drone);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(drone);
      });

      it('should not add a Drone to an array that contains it', () => {
        const drone: IDrone = sampleWithRequiredData;
        const droneCollection: IDrone[] = [
          {
            ...drone,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDroneToCollectionIfMissing(droneCollection, drone);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Drone to an array that doesn't contain it", () => {
        const drone: IDrone = sampleWithRequiredData;
        const droneCollection: IDrone[] = [sampleWithPartialData];
        expectedResult = service.addDroneToCollectionIfMissing(droneCollection, drone);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(drone);
      });

      it('should add only unique Drone to an array', () => {
        const droneArray: IDrone[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const droneCollection: IDrone[] = [sampleWithRequiredData];
        expectedResult = service.addDroneToCollectionIfMissing(droneCollection, ...droneArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const drone: IDrone = sampleWithRequiredData;
        const drone2: IDrone = sampleWithPartialData;
        expectedResult = service.addDroneToCollectionIfMissing([], drone, drone2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(drone);
        expect(expectedResult).toContain(drone2);
      });

      it('should accept null and undefined values', () => {
        const drone: IDrone = sampleWithRequiredData;
        expectedResult = service.addDroneToCollectionIfMissing([], null, drone, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(drone);
      });

      it('should return initial array if no Drone is added', () => {
        const droneCollection: IDrone[] = [sampleWithRequiredData];
        expectedResult = service.addDroneToCollectionIfMissing(droneCollection, undefined, null);
        expect(expectedResult).toEqual(droneCollection);
      });
    });

    describe('compareDrone', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDrone(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDrone(entity1, entity2);
        const compareResult2 = service.compareDrone(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDrone(entity1, entity2);
        const compareResult2 = service.compareDrone(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDrone(entity1, entity2);
        const compareResult2 = service.compareDrone(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
