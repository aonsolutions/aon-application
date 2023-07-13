import { TestBed } from '@angular/core/testing';

import { GlobalErrorHandlerService } from './global-error-handler.service';
import { ErrorService } from './error.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

describe('GlobalErrorHandlerService', () => {
  let service: GlobalErrorHandlerService;
  let errorService : ErrorService;
  let snackBar: MatSnackBar;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports : [
        MatSnackBarModule
      ],
      providers : [
        ErrorService,
        MatSnackBar
      ],
    });
    service = TestBed.inject(GlobalErrorHandlerService);
    errorService = TestBed.inject(ErrorService);
    snackBar = TestBed.inject(MatSnackBar);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
