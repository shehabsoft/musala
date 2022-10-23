import dayjs from 'dayjs/esm';

import { IMedication, NewMedication } from './medication.model';

export const sampleWithRequiredData: IMedication = {
  id: 73796,
  name: 'cross-platform',
  weight: 30713,
  code: 'ADP',
  createdBy: 'withdrawal motivating',
  createdDate: dayjs('2022-10-15T17:04'),
};

export const sampleWithPartialData: IMedication = {
  id: 86713,
  name: 'Sleek SMS',
  weight: 60146,
  code: 'Dynamic ivory Wisconsin',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  createdBy: 'white Rustic',
  createdDate: dayjs('2022-10-16T01:00'),
  lastModifiedBy: 'payment',
  lastModifiedDate: dayjs('2022-10-15T22:02'),
};

export const sampleWithFullData: IMedication = {
  id: 41024,
  name: 'protocol',
  weight: 94547,
  code: 'Credit',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
  createdBy: 'Bedfordshire Mouse',
  createdDate: dayjs('2022-10-16T02:32'),
  lastModifiedBy: 'withdrawal Beauty',
  lastModifiedDate: dayjs('2022-10-16T08:57'),
};

export const sampleWithNewData: NewMedication = {
  name: 'payment monitor Outdoors',
  weight: 32274,
  code: 'Division Incredible',
  createdBy: 'Loan',
  createdDate: dayjs('2022-10-16T01:45'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
