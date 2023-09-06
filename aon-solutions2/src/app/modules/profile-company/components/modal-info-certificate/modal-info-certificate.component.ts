import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-info-certificate',
  templateUrl: './modal-info-certificate.component.html',
  styleUrls: ['./modal-info-certificate.component.scss'],
})
export class ModalInfoCertificateComponent implements OnInit {
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalInfoCertificateComponent>
  ) {}

  closeModal(): void {
    this.dialogRef.close();
  }
  ngOnInit() {}
}


