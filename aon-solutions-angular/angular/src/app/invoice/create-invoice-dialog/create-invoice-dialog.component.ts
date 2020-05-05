import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';

@Component({
  templateUrl: './create-invoice-dialog.component.html',
  styleUrls: ['./create-invoice-dialog.component.css'],
})
export class CreateInvoiceDialogComponent {
  title = 'Nueva Factura';
  type: string;
  withType: boolean;
  types: any[] = [
    {value: 'EMITIDA', viewValue: 'Emitidas'},
    {value: 'RECIBIDA', viewValue: 'Recibidas'},
    {value: 'TICKET', viewValue: 'Ticket'}
  ];

  constructor(public dialogRef: MatDialogRef<CreateInvoiceDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
      this.withType = data.type || false;
  }

  onNoClick(): void {

  }

  accept(): void {
    const result = {
      type: this.type
    };
    this.dialogRef.close(result);
  }

}
