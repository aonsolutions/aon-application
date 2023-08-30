import { TestBed } from '@angular/core/testing';

import { RegistryEnterpriseService } from './registry-enterprise.service';

describe('RegistryEnterpriseService', () => {
  let service: RegistryEnterpriseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegistryEnterpriseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
