import dayjs from 'dayjs/esm';

export interface IAudit {
  id: number;
  message?: string | null;
  createdBy?: string | null;
  createdDate?: dayjs.Dayjs | null;
  lastModifiedBy?: string | null;
  lastModifiedDate?: dayjs.Dayjs | null;
}

export type NewAudit = Omit<IAudit, 'id'> & { id: null };
