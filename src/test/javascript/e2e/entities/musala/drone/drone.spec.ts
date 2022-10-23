import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../../page-objects/jhi-page-objects';

import { DroneComponentsPage, DroneDeleteDialog, DroneUpdatePage } from './drone.page-object';

const expect = chai.expect;

describe('Drone e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let droneComponentsPage: DroneComponentsPage;
  let droneUpdatePage: DroneUpdatePage;
  let droneDeleteDialog: DroneDeleteDialog;
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing(username, password);
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Drones', async () => {
    await navBarPage.goToEntity('drone');
    droneComponentsPage = new DroneComponentsPage();
    await browser.wait(ec.visibilityOf(droneComponentsPage.title), 5000);
    expect(await droneComponentsPage.getTitle()).to.eq('Drones');
    await browser.wait(ec.or(ec.visibilityOf(droneComponentsPage.entities), ec.visibilityOf(droneComponentsPage.noResult)), 1000);
  });

  it('should load create Drone page', async () => {
    await droneComponentsPage.clickOnCreateButton();
    droneUpdatePage = new DroneUpdatePage();
    expect(await droneUpdatePage.getPageTitle()).to.eq('Create or edit a Drone');
    await droneUpdatePage.cancel();
  });

  it('should create and save Drones', async () => {
    const nbButtonsBeforeCreate = await droneComponentsPage.countDeleteButtons();

    await droneComponentsPage.clickOnCreateButton();

    await promise.all([
      droneUpdatePage.setSerialNumberInput('serialNumber'),
      droneUpdatePage.modelSelectLastOption(),
      droneUpdatePage.setWeightLimitInput('weightLimit'),
      droneUpdatePage.setBatteryCapacityInput('batteryCapacity'),
      droneUpdatePage.stateSelectLastOption(),
      droneUpdatePage.setCreatedByInput('createdBy'),
      droneUpdatePage.setCreatedDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      droneUpdatePage.setLastModifiedByInput('lastModifiedBy'),
      droneUpdatePage.setLastModifiedDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
    ]);

    await droneUpdatePage.save();
    expect(await droneUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await droneComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Drone', async () => {
    const nbButtonsBeforeDelete = await droneComponentsPage.countDeleteButtons();
    await droneComponentsPage.clickOnLastDeleteButton();

    droneDeleteDialog = new DroneDeleteDialog();
    expect(await droneDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Drone?');
    await droneDeleteDialog.clickOnConfirmButton();
    await browser.wait(ec.visibilityOf(droneComponentsPage.title), 5000);

    expect(await droneComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
