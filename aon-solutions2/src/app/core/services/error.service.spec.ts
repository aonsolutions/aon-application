import { TestBed } from '@angular/core/testing';

import { ErrorService } from './error.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('ErrorService', () => {
  let service: ErrorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports  : [
        MatSnackBarModule
      ],
      providers : [
        ErrorService
      ],
    });
  });

  beforeEach(()=>{
    service = TestBed.inject(ErrorService);
  });
  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
