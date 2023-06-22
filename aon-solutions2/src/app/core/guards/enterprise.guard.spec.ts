import { TestBed } from '@angular/core/testing';

import { EnterpriseGuard } from './enterprise.guard';

describe('EnterpriseGuard', () => {
  let guard: EnterpriseGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(EnterpriseGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
