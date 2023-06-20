import { TestBed } from '@angular/core/testing';

import { EnterpriseService } from './enterprise.service';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Enterprise } from '../models/class/enterprise';


describe('EnterpriseService', () => {
  let service: EnterpriseService;
  beforeEach (() => {
    TestBed.configureTestingModule({
      imports : [
        HttpClientTestingModule,
        RouterTestingModule
        ],
    });
  });

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(EnterpriseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });


});
