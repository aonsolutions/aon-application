import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';

@Component({
  templateUrl: './comment-dialog.component.html',
  styleUrls: ['./comment-dialog.component.css'],
})
export class CommentDialogComponent {
  comentario: string = '';

  constructor(public dialogRef: MatDialogRef<CommentDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    if(data !== undefined && data !== null && data.comment) {
      this.comentario = data.comment;
    }
  }

  accept(value: string ): void {
    this.dialogRef.close(value);
  }
}
