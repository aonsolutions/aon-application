import { Injectable, NgZone } from '@angular/core';
import { CustomErrorList } from '../models/enum/custom-error-list.enum';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { ErrorSnackBarComponent } from 'src/app/shared/components/error-snack-bar/error-snack-bar.component';
import { CustomError } from '../models/class/custom-error';
import { ICustomError } from '../models/interface/icustom-error';
import { IResponse } from 'libraries/AonSDK/AonSDK';
import { Response } from 'libraries/AonSDK/AonSDK';

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  snackBarRef: any;

  constructor(public snackBar: MatSnackBar,private zone: NgZone) {
  }

  getError(code: string): string {
    return CustomErrorList[('error' + code) as keyof typeof CustomErrorList];
  }

  displayError(code: ICustomError | IResponse): void {
    let dataError: Array<string> = []
    if(code instanceof CustomError){
      if(typeof code.data != 'string'){
        code.data.forEach((error) => {
          if(this.getError(error))
            dataError.push(this.getError(error))
        })
      }else{
        if(this.getError(code.data))
          dataError.push(this.getError(code.data))
      }
    }else if(code instanceof Response){
      if(this.getError(code.code))
        dataError.push(this.getError(code.code))
    }
    if(this.snackBar._openedSnackBarRef){
      this.snackBar._openedSnackBarRef?.instance.data.errorList.push(dataError)
    } else {    
      this.zone.run(() => {
        this.snackBar.openFromComponent(ErrorSnackBarComponent,{
          horizontalPosition: 'center',
          verticalPosition: 'top',
          data: {
            errorList: dataError,
            preClose: () => {this.dismiss()}
          }
        });
      });
    }
  }

  dismiss(){
    this.snackBar.dismiss();
  }

}
