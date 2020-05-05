import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, OnDestroy, Inject } from '@angular/core';

@Component({
  templateUrl: './type-dialog.component.html',
  styleUrls: ['./type-dialog.component.css'],
})
export class TypeDialogComponent implements OnDestroy {

  type: string;

  constructor(public dialogRef: MatDialogRef<TypeDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: string) {
    this.type = data;
  }

  ngOnDestroy() {
    this.dialogRef.close(this.type);
  }

  accept() {
    this.dialogRef.close(this.type);
  }
}
