import { TestBed } from '@angular/core/testing';

import { EnterpriseGuard } from './enterprise.guard';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AuthService } from '../services/auth.service';
import { EnterpriseService } from '../services/enterprise.service';


describe('EnterpriseGuard', () => {
  let guard: EnterpriseGuard;
  let enterpriseService : EnterpriseService;
  let authService : AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports : [
        RouterTestingModule,
        HttpClientTestingModule
      ],
      providers : [
        EnterpriseService,
        AuthService,
        Location
      ]
    });
    guard = TestBed.inject(EnterpriseGuard);
    enterpriseService = TestBed.inject(EnterpriseService);
    authService = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
