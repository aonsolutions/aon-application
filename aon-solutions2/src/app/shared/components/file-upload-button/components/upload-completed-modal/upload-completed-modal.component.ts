import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-upload-completed-modal',
  templateUrl: './upload-completed-modal.component.html',
  styleUrls: ['./upload-completed-modal.component.scss']
})
export class UploadCompletedModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<UploadCompletedModalComponent>
  ) { }

  ngOnInit(): void {
  }

  closeModal(): void {
    this.dialogRef.close();
  }

}
