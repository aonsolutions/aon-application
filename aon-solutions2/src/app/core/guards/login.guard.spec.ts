import { TestBed } from '@angular/core/testing';

import { LoginGuard } from './login.guard';
import { AuthService } from '../services/auth.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('LoginGuard', () => {
  let guard: LoginGuard;
  let authService : AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports :[
        RouterTestingModule
      ],
      providers :[
        AuthService,
        Location
      ]
    });

    guard = TestBed.inject(LoginGuard);
    authService = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
