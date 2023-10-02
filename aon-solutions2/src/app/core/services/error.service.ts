import { Injectable, NgZone } from '@angular/core';
import { CustomErrorList } from '../models/enum/custom-error-list.enum';
import { MatSnackBar, MatSnackBarRef } from '@angular/material/snack-bar';
import { ErrorSnackBarComponent } from 'src/app/shared/components/error-snack-bar/error-snack-bar.component';
import { CustomError } from '../models/class/custom-error';
import { ICustomError } from '../models/interface/icustom-error';
import { IResponse } from 'libraries/AonSDK/src/aon';
import { Response } from 'libraries/AonSDK/src/aon';
import { ErrorResponse } from 'libraries/AonSDK/src/aon';

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

  displayError(code: ICustomError | IResponse<any>): void {
    let dataError: Array<string> = [];

    if (code instanceof CustomError) {
      if (typeof code.data !== 'string') {
        code.data.forEach((error) => {
          const uniqueError = this.getError(error);

          // Verificar si el mensaje de error ya existe en dataError
          if (uniqueError && !dataError.includes(uniqueError)) {
            dataError.push(uniqueError);
          }
        });
      } else {
        const uniqueError = this.getError(code.data);

        if (uniqueError && !dataError.includes(uniqueError)) {
          dataError.push(uniqueError);
        }
      }
    } else if (code instanceof Response) {
      const uniqueError = this.getError(code.code);

      if (uniqueError && !dataError.includes(uniqueError)) {
        dataError.push(uniqueError);
      }
    } else if (code instanceof ErrorResponse) {
      const uniqueError = code.result;

      if (uniqueError && !dataError.includes(uniqueError)) {
        dataError.push(uniqueError);
      }
    }

    if (this.snackBar._openedSnackBarRef) {
      // Reemplazar el array completo en lugar de agregar elementos individuales
      this.snackBar._openedSnackBarRef.instance.data.errorList = dataError;
    } else {
      this.zone.run(() => {
        this.snackBar.openFromComponent(ErrorSnackBarComponent, {
          horizontalPosition: 'center',
          verticalPosition: 'top',
          data: {
            errorList: dataError,
            preClose: () => {
              this.dismiss();
            },
          },
        });
      });
    }
  }

  dismiss(){
    this.snackBar.dismiss();
  }

}
