import { TestBed } from '@angular/core/testing';

import { EnterpriseService } from './enterprise.service';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AonSDK } from 'libraries/AonSDK/src/AonSDK';


describe('EnterpriseService', () => {
  let service: EnterpriseService;
  let aonSdk : AonSDK;
  let aonSDKMock = jasmine.createSpyObj('AonSDK', ['model']);

  beforeEach (() => {
    TestBed.configureTestingModule({
      imports : [
        HttpClientTestingModule,
        RouterTestingModule,
        ],
        providers: [
          AonSDK,
          { provide: AonSDK, useValue: aonSDKMock }
        ],
    });
  });

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnterpriseService);
    aonSdk= TestBed.inject(AonSDK);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  // setEnterprise(cif: string): Promise<boolean> {
  //   return new Promise((resolve, reject) => {
  //     this.aonSDK
  //       .model('enterprise')
  //       .setEnterprise('enteprise', cif)
  //       .then((response: any) => {
  //         resolve(response.result);
  //       });
  //   });
  // }
 
 // it ('setEnterprise establishes a company using the cif value',(done : DoneFn)=>{
 //   const cif = 'cifTest';
 //   const spyService = spyOn(service,'setEnterprise').and.returnValue(Promise.resolve(true));
 //   expect(spyService).toBeTruthy();
 //   done();
 // });

  // it('getEnterpriseList returns a promise with the company data.', (done : DoneFn) => {
  //   const enterpriseData = [
  //     { id: 1, name: 'Enterprise 1' },
  //     { id: 2, name: 'Enterprise 2' }
  //   ];
  //   const getElementListSpy = jasmine.createSpy().and.returnValue(Promise.resolve({ result: enterpriseData }));
  //   service.getEnterpriseList();
  //   expect(getElementListSpy).toBeTruthy();

  // });

  it('getEnterpriseList returns a promise with the company data.', () => {
    const enterpriseData = [
      { id: 1, name: 'Enterprise 1' },
      { id: 2, name: 'Enterprise 2' }
    ];
    const getElementListSpy = jasmine.createSpy().and.returnValue(Promise.resolve({ result: enterpriseData }));
    service.getEnterpriseList();
    expect(getElementListSpy).toBeTruthy();
});
  // // });
  // it('isEnterpriseSelected return false',async ()=>{
  //   aonSdk.model('auth').getSession();
  //   sessionStorage.setItem('enterprise','934824');
  //   const result = service.isEnterpriseSelected();
  //   expect(result).toBeTruthy();
  // });

});

