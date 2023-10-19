import { TestBed } from '@angular/core/testing';


import { MatSnackBar, MatSnackBarModule, MatSnackBarRef } from '@angular/material/snack-bar';
import { ErrorService } from 'src/app/core/services/error.service';
import { GlobalErrorHandlerService } from 'src/app/core/services/global-error-handler.service';

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
