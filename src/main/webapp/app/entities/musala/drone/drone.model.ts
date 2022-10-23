import dayjs from 'dayjs/esm';
import { Model } from 'app/entities/enumerations/model.model';
import { State } from 'app/entities/enumerations/state.model';

export interface IDrone {
  id: number;
  serialNumber?: string | null;
  model?: Model | null;
  weightLimit?: string | null;
  batteryCapacity?: string | null;
  state?: State | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewDrone = Omit<IDrone, 'id'> & { id: null };
