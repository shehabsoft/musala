import { browser, ExpectedConditions as ec, protractor, promise } from 'protractor';
import { NavBarPage, SignInPage } from '../../../page-objects/jhi-page-objects';

import { MedicationComponentsPage, MedicationDeleteDialog, MedicationUpdatePage } from './medication.page-object';
import path from 'path';

const expect = chai.expect;

describe('Medication e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let medicationComponentsPage: MedicationComponentsPage;
  let medicationUpdatePage: MedicationUpdatePage;
  let medicationDeleteDialog: MedicationDeleteDialog;
  const fileNameToUpload = 'logo-jhipster.png';
  const fileToUpload = '../../../../../../../src/main/webapp/content/images/' + fileNameToUpload;
  const absolutePath = path.resolve(__dirname, fileToUpload);
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing(username, password);
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Medications', async () => {
    await navBarPage.goToEntity('medication');
    medicationComponentsPage = new MedicationComponentsPage();
    await browser.wait(ec.visibilityOf(medicationComponentsPage.title), 5000);
    expect(await medicationComponentsPage.getTitle()).to.eq('Medications');
    await browser.wait(ec.or(ec.visibilityOf(medicationComponentsPage.entities), ec.visibilityOf(medicationComponentsPage.noResult)), 1000);
  });

  it('should load create Medication page', async () => {
    await medicationComponentsPage.clickOnCreateButton();
    medicationUpdatePage = new MedicationUpdatePage();
    expect(await medicationUpdatePage.getPageTitle()).to.eq('Create or edit a Medication');
    await medicationUpdatePage.cancel();
  });

  it('should create and save Medications', async () => {
    const nbButtonsBeforeCreate = await medicationComponentsPage.countDeleteButtons();

    await medicationComponentsPage.clickOnCreateButton();

    await promise.all([
      medicationUpdatePage.setNameInput('name'),
      medicationUpdatePage.setWeightInput('5'),
      medicationUpdatePage.setCodeInput('code'),
      medicationUpdatePage.setImageInput(absolutePath),
      medicationUpdatePage.setCreatedByInput('createdBy'),
      medicationUpdatePage.setCreatedDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      medicationUpdatePage.setLastModifiedByInput('lastModifiedBy'),
      medicationUpdatePage.setLastModifiedDateInput('01/01/2001' + protractor.Key.TAB + '02:30AM'),
      medicationUpdatePage.droneSelectLastOption(),
    ]);

    await medicationUpdatePage.save();
    expect(await medicationUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

    expect(await medicationComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
  });

  it('should delete last Medication', async () => {
    const nbButtonsBeforeDelete = await medicationComponentsPage.countDeleteButtons();
    await medicationComponentsPage.clickOnLastDeleteButton();

    medicationDeleteDialog = new MedicationDeleteDialog();
    expect(await medicationDeleteDialog.getDialogTitle()).to.eq('Are you sure you want to delete this Medication?');
    await medicationDeleteDialog.clickOnConfirmButton();
    await browser.wait(ec.visibilityOf(medicationComponentsPage.title), 5000);

    expect(await medicationComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
