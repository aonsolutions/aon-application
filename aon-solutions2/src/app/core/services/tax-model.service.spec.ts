import { TestBed } from '@angular/core/testing';

import { TaxModelService } from './tax-model.service';

describe('TaxModelService', () => {
  let service: TaxModelService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TaxModelService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
