import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { AonMaker } from '../../models/models';
import { FormControl} from '@angular/forms';

@Component({
  templateUrl: './invoice-finance-dialog.component.html',
  styleUrls: ['./invoice-finance-dialog.component.css'],
})
export class InvoiceFinanceDialogComponent {
  hide = true;

  finance = AonMaker.createFinance();
  dateControl: FormControl = new FormControl();
  constructor(public dialogRef: MatDialogRef<InvoiceFinanceDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
      this.finance = data || this.finance;
      this.dateControl = this.getFormControl(this.finance.due_date);
  }

  updateFinanceDate(value: string): void {
    const date = new Date(this.getYear(value), this.getMonth(value) - 1, this.getDay(value), 2);
    this.finance.due_date = date;
  }

  updateFinanceAmount(value: string): void {
    this.finance.amount = Number(value);
  }

  updateFinanceIban(value: string): void {
    this.finance.iban = value;
  }

  accept(): void {
    this.dialogRef.close(this.finance);
  }


  getYear(str: string): number {
    const arr = str.split('/');
    return + arr[2];
  }
  getMonth(str: string): number {
    const arr = str.split('/');
    return + arr[1];
  }
  getDay(str: string): number {
    const arr = str.split('/');
    return + arr[0];
  }

  getFormControl(value: Date): FormControl {
    console.log('get FC  ' + value);
    if (value) {
      return new FormControl(value);
    } else {
      return new FormControl();
    }
  }

}
