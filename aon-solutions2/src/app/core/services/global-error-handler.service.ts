import { ErrorHandler, Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandlerService implements ErrorHandler{
  
  constructor() { }

  handleError(error: any): void {
    if (error.promise && error.rejection) {
      console.log(error.rejection);
    }else {
      console.log(error);
    }
  }
  
}