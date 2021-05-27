import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';

@Component({
  templateUrl: './sure-dialog.component.html',
  styleUrls: ['./sure-dialog.component.css'],
})
export class SureDialogComponent {

  constructor(public dialogRef: MatDialogRef<SureDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {

  }

  accept(): void {
    this.dialogRef.close(true);
  }
}
