import { ErrorHandler, Injectable } from '@angular/core';

import { CustomError } from '../models/class/custom-error';
import { environment } from 'src/environments/environment';
import { ErrorResponse, Response } from 'libraries/AonSDK/src/aon';
import { ErrorService } from './error.service';

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandlerService implements ErrorHandler{

  constructor(public errorService: ErrorService) { }

  handleError(error: any): void {
    if(environment.production == false){
      console.log(error);
    }
    if (error.promise && error.rejection) {
      this.launchError(error.rejection)
    }else {
      this.launchError(error)
    }
  }

  launchError(errorList: any){

    if(errorList instanceof CustomError || errorList instanceof Response || errorList instanceof ErrorResponse)
      this.errorService.displayError(errorList)
  }

}
