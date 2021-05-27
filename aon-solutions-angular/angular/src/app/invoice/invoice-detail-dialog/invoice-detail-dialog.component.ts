import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { TediUtils } from '../../utils/tedi-utils';

@Component({
  templateUrl: './invoice-detail-dialog.component.html',
  styleUrls: ['./invoice-detail-dialog.component.css'],
})
export class InvoiceDetailDialogComponent {
  hide = true;

  detail = {description: undefined, price: undefined, purchase_price: undefined,  quantity: undefined, discount: undefined, vat: undefined};

  constructor(public dialogRef: MatDialogRef<InvoiceDetailDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
      this.detail = data || this.detail;
  }

  changeDescription(value: string): void {
    this.detail.description = value;
  }

  changeQuantity(value: string): void {
    this.detail.quantity = Number(value);
    this.calculatePrice();
  }

  changetPurchasePrice(value: string): void {
    this.detail.purchase_price = Number(value);
    this.calculatePrice();
  }

  changeDiscount(value: string): void {
    this.detail.discount = Number(value);
    this.calculatePrice();
  }

  changePrice(value: string): void {
    this.detail.price = Number(value);
  }

  calculatePrice(): void {
    if (this.detail.purchase_price && this.detail.quantity) {
      let purchasePrice = this.detail.purchase_price;
      if (!TediUtils.isNumber(purchasePrice)) { purchasePrice = purchasePrice.replace(' ', '').replace(',', '.'); }
      let quantity = this.detail.quantity;
      if (!TediUtils.isNumber(quantity)) { quantity = quantity.replace(' ', '').replace(',', '.'); }
      let price = TediUtils.round(Number(quantity) * Number(purchasePrice));
      if (this.detail.discount) {
        price = price - price * (this.detail.discount / 100);
      } else { this.detail.discount = 0.0; }
      this.detail.price = TediUtils.round(price);
    }
  }

  accept(): void {
    this.dialogRef.close(this.detail);
  }

}
