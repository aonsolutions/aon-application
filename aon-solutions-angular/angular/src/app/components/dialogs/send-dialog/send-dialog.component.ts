import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';

@Component({
  templateUrl: './send-dialog.component.html',
  styleUrls: ['./send-dialog.component.css'],
})
export class SendDialogComponent {

  constructor(public dialogRef: MatDialogRef<SendDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {

  }

  accept(to: string): void {
    this.dialogRef.close(to);
  }
}
