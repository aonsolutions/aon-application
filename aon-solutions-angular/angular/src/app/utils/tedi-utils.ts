import { MatDialog } from '@angular/material';
import { ErrorDialogComponent } from '../components/dialogs/error-dialog/error-dialog.component';

export class TediUtils {

  static showError(dialog: MatDialog, error: string) {
    const dialogRef = dialog.open(ErrorDialogComponent, {
      height: '200px',
      panelClass: 'aon-user-dialog-panel',
      data: JSON.stringify(error)
    });

    dialogRef.afterClosed().subscribe(result => {});
  }

  static isNumber(n): boolean {
    return !isNaN(parseFloat(n)) && isFinite(n);
  }

  static round(value): number {
      return this.decimalAdjust('round', value, -2);
  }

  static decimalAdjust(type, value, exp): number {
    // Si el exp no está definido o es cero...
    if (typeof exp === 'undefined' || +exp === 0) {
      return Math[type](value);
    }
    value = +value;
    exp = +exp;
    // Si el valor no es un número o el exp no es un entero...
    if (isNaN(value) || !(typeof exp === 'number' && exp % 1 === 0)) {
      return NaN;
    }
    // Shift
    value = value.toString().split('e');
    value = Math[type](+(value[0] + 'e' + (value[1] ? (+value[1] - exp) : -exp)));
    // Shift back
    value = value.toString().split('e');
    return +(value[0] + 'e' + (value[1] ? (+value[1] + exp) : exp));
  }
}
