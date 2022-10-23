import dayjs from 'dayjs/esm';

import { Model } from 'app/entities/enumerations/model.model';
import { State } from 'app/entities/enumerations/state.model';

import { IDrone, NewDrone } from './drone.model';

export const sampleWithRequiredData: IDrone = {
  id: 54162,
  serialNumber: 'Soft whiteboard',
  model: Model['Heavyweight'],
  weightLimit: 'withdrawal',
  batteryCapacity: 'out-of-the-box',
  state: State['DELIVERED'],
  createdBy: 'back-end Networked silver',
  createdDate: dayjs('2022-10-15T18:03'),
};

export const sampleWithPartialData: IDrone = {
  id: 85033,
  serialNumber: 'Finland',
  model: Model['Cruiserweight'],
  weightLimit: 'infrastructures Profit-focused Checking',
  batteryCapacity: 'Lights Delaware Account',
  state: State['IDLE'],
  createdBy: 'Computers',
  createdDate: dayjs('2022-10-15T19:52'),
};

export const sampleWithFullData: IDrone = {
  id: 40251,
  serialNumber: 'Saint generating compress',
  model: Model['Cruiserweight'],
  weightLimit: 'Bacon transmitter Personal',
  batteryCapacity: 'Internal Borders Card',
  state: State['DELIVERED'],
  createdBy: 'deposit haptic',
  createdDate: dayjs('2022-10-15T18:28'),
  lastModifiedBy: 'Mouse',
  lastModifiedDate: dayjs('2022-10-16T09:22'),
};

export const sampleWithNewData: NewDrone = {
  serialNumber: 'Research virtual',
  model: Model['Middleweight'],
  weightLimit: 'Health',
  batteryCapacity: 'Table users Oklahoma',
  state: State['IDLE'],
  createdBy: 'Bacon web-enabled synergize',
  createdDate: dayjs('2022-10-15T19:44'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
