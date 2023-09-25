import { ErrorHandler, Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { ErrorService } from './error.service';
import { CustomError } from '../models/class/custom-error';
import { Response } from 'libraries/AonSDK/AonSDK';
import { ErrorResponse } from 'libraries/AonSDK/aon';

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
