import dayjs from 'dayjs/esm';
import { IDrone } from 'app/entities/musala/drone/drone.model';

export interface IMedication {
  id: number;
  name?: string | null;
  weight?: number | null;
  code?: string | null;
  image?: string | null;
  imageContentType?: string | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
  drone?: Pick<IDrone, 'id'> | null;
}

export type NewMedication = Omit<IMedication, 'id'> & { id: null };
