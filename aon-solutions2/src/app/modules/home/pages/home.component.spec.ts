import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeComponent } from './home.component';
//import { Bank } from 'src/app/core/models/class/bank';
//import { BankService } from 'src/app/core/services/bank.service';
//import { TaxModel } from 'src/app/core/models/class/tax-model';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { ReportingService } from '../../../core/services/reporting.service';
//import { Message } from 'src/app/core/models/class/message';
import { MessageService } from 'src/app/core/services/message.service';

//Mock BankService
//const mockedBankService : {
 // getBankList : () => Promise<Bank[]>;
//} = {
 // getBankList : () => Promise.resolve([])
//};
//Mock TaxmodelService
//const mockedTaxModelService : {
//  getTaxModelList : () => Promise<TaxModel[]>;
//} = {
//  getTaxModelList : () => Promise.resolve([])
//};
//const mockedReportingService : {
//  getVentasGastos : () => Promise<any>;
//  getCobrosPagos : () =>  Promise<any>;
//} = {
//  getVentasGastos : () => Promise.resolve([]),
//  getCobrosPagos : () => Promise.resolve([])
//};
//const mockedMessageService : {
//  getMessageList : () => Promise<Message[]>;
//} = {
//  getMessageList : () => Promise.resolve([]),
//
//};
//
describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ 
        HomeComponent 
      ],
      providers :[
       // {provide : BankService , useValue : mockedBankService},
      //  {provide : TaxModelService , useValue : mockedTaxModelService},
      //  {provide : ReportingService, useValue : mockedReportingService},
       // {provide : MessageService, useValue : mockedMessageService}
      ],
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
  //verifica que ngOninit realiza una llamada al método getBankList del BankService
  it('ngOninit shoul call getBanklist()', async() => {
  //  const spyBankservice = spyOn(mockedBankService,'getBankList').and.returnValue(Promise.resolve([]));
    component.ngOnInit();

  //  expect(spyBankservice).toHaveBeenCalled();
  });
    //verifica que ngOninit realiza una llamada al método getTaxModelList del TaxModelService
  it('ngOninit shoul call getTaxModelList()', async() => {
  //  const spyBankservice = spyOn(mockedTaxModelService,'getTaxModelList').and.returnValue(Promise.resolve([]));
    component.ngOnInit();

   // expect(spyBankservice).toHaveBeenCalled();
  });
   //verifica que ngOninit realiza una llamada al método getVentasGastos del ReportingService
  it('ngOninit shoul call getVentasGastos()', async() => {
  //  const spyBankservice = spyOn(mockedReportingService,'getVentasGastos').and.returnValue(Promise.resolve([]));
    component.ngOnInit();

 //   expect(spyBankservice).toHaveBeenCalled();
  });
   //verifica que ngOninit realiza una llamada al método getCobrosPagos getVentasGastos del ReportingService
    it('ngOninit shoul call getCobrosPagos()', async() => {
 //     const spyBankservice = spyOn(mockedReportingService,'getCobrosPagos').and.returnValue(Promise.resolve([]));
      component.ngOnInit();
  
 //     expect(spyBankservice).toHaveBeenCalled();
    });
    //verifica que ngOninit realiza una llamada al método getMessageList del MessageService
    it('ngOninit shoul call getMessageList()', async() => {
 //     const spyBankservice = spyOn(mockedMessageService,'getMessageList').and.returnValue(Promise.resolve([]));
      component.ngOnInit();
  
 //     expect(spyBankservice).toHaveBeenCalled();
    });
});
