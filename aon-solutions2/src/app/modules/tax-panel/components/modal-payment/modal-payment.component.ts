import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

export interface OptionModalPayment {
  name: string;
  value: number;
  checked: boolean;
}
@Component({
  selector: 'app-modal-payment',
  templateUrl: './modal-payment.component.html',
  styleUrls: ['./modal-payment.component.scss'],
})
export class ModalPaymentComponent implements OnInit {
  radioButtonOptions: OptionModalPayment[] = [
    { name: 'Domiciliación Bancaria', value: 1, checked: true },
    { name: 'nrc (banca online u oficina bancaria)', value: 2, checked: false },
    { name: 'aplazamiento/fraccionamiento', value: 3, checked: false },
  ];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalPaymentComponent>
  ) {}

  ngOnInit(): void {}
}
