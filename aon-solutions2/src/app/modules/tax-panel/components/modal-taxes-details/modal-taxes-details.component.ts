import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-taxes-details',
  templateUrl: './modal-taxes-details.component.html',
  styleUrls: ['./modal-taxes-details.component.scss'],
})
export class ModalTaxesDetailsComponent implements OnInit {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalTaxesDetailsComponent>
  ) {}

  ngOnInit() {}
}
