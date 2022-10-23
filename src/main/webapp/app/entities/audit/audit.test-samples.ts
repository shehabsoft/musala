import dayjs from 'dayjs/esm';

import { IAudit, NewAudit } from './audit.model';

export const sampleWithRequiredData: IAudit = {
  id: 57661,
  message: 'enhance withdrawal silver',
  createdBy: 'sensor online',
  createdDate: dayjs('2022-10-22T13:26'),
};

export const sampleWithPartialData: IAudit = {
  id: 74033,
  message: 'JBOD',
  createdBy: 'Unbranded Birr Avon',
  createdDate: dayjs('2022-10-22T05:16'),
};

export const sampleWithFullData: IAudit = {
  id: 82264,
  message: 'cross-platform deploy Franc',
  createdBy: 'website Berkshire synergize',
  createdDate: dayjs('2022-10-22T09:40'),
  lastModifiedBy: 'Vermont connecting Carolina',
  lastModifiedDate: dayjs('2022-10-22T05:39'),
};

export const sampleWithNewData: NewAudit = {
  message: 'Fantastic',
  createdBy: 'fuchsia Shoes',
  createdDate: dayjs('2022-10-22T06:20'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
