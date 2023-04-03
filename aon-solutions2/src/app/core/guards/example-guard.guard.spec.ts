import { TestBed } from '@angular/core/testing';

import { ExampleGuardGuard } from './example-guard.guard';

describe('ExampleGuardGuard', () => {
  let guard: ExampleGuardGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    guard = TestBed.inject(ExampleGuardGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
