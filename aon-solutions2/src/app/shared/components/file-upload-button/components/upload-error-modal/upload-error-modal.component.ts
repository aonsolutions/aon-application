import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-upload-error-modal',
  templateUrl: './upload-error-modal.component.html',
  styleUrls: ['./upload-error-modal.component.scss']
})
export class UploadErrorModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<UploadErrorModalComponent>
  ) { }

  ngOnInit(): void {
  }

  closeModal(): void {
    this.dialogRef.close();
  }

}
