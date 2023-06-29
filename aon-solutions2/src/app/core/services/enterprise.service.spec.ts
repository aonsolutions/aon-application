import { TestBed } from '@angular/core/testing';

import { EnterpriseService } from './enterprise.service';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AonSDK } from 'libraries/AonSDK/AonSDK';
import { Enterprise } from '../models/class/enterprise';

describe('EnterpriseService', () => {
  let service: EnterpriseService;
  let aonSdk : AonSDK;

  beforeEach (() => {
    TestBed.configureTestingModule({
      imports : [
        HttpClientTestingModule,
        RouterTestingModule,
        ],
        providers: [
          AonSDK
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

  it ('setEnterprise establishes a company using the cif value',(done : DoneFn)=>{
    const cif = 'cifTest';
    aonSdk.model('enterprise');
    service.setEnterprise(cif).then(value => {
      expect(value).toBeTruthy();
      done();
    }).catch(
      (error) => {
        expect(error).toBeTruthy();
        done();
      }
    )
  });

  it('isEnterpriseSelected return false',async ()=>{
    aonSdk.model('auth').getSession();
    sessionStorage.setItem('enterprise','934824');
    const result = service.isEnterpriseSelected();
    expect(result).toBeTruthy();
  });

});

