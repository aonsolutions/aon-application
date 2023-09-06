import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-edit-tax-model',
  templateUrl: './modal-edit-tax-model.component.html',
  styleUrls: ['./modal-edit-tax-model.component.scss'],
})
export class ModalEditTaxModelComponent implements OnInit {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalEditTaxModelComponent>
  ) {}

  closeModal(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {}
}
