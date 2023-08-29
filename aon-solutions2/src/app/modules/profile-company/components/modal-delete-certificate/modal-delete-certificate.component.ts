import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-modal-delete-certificate',
  templateUrl: './modal-delete-certificate.component.html',
  styleUrls: ['./modal-delete-certificate.component.scss']
})
export class ModalDeleteCertificateComponent implements OnInit {

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialogRef: MatDialogRef<ModalDeleteCertificateComponent>
  ) {}

  closeModal(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
  }

}
