import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-payment',
  templateUrl: './modal-payment.component.html',
  styleUrls: ['./modal-payment.component.scss'],
})
export class ModalPaymentComponent implements OnInit {
  selectedOption: string = '1';
  showText: boolean = false;
  showCreateBank: boolean = false;
  showConfirmation: boolean = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalPaymentComponent>
  ) {}

  ngOnInit(): void {}

  toggleCreateBank() {
    this.showCreateBank = !this.showCreateBank;
  }

  showConfirmationContent() {
    this.showConfirmation = true;
  }
}


