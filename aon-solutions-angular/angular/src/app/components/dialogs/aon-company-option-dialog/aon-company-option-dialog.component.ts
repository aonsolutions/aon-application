import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { MatDialog } from '@angular/material';
import { Company } from '../../../models/models';
import { AonCompanyShareDialogComponent } from '../aon-company-share-dialog/aon-company-share-dialog.component';

@Component({
  templateUrl: './aon-company-option-dialog.component.html',
  styleUrls: ['./aon-company-option-dialog.component.css'],
})
export class AonCompanyOptionDialogComponent {

  company: Company;
  constructor(public dialogRef: MatDialogRef<AonCompanyOptionDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: Company, public dialog: MatDialog) {
    this.company = data;
  }

  onNoClick(): void {

  }

  share(): void {
    this.dialogRef.close(true);
    const ref = this.dialog.open(AonCompanyShareDialogComponent, {
      height: '200px',
      backdropClass: 'aon-user-dialog-backdrop',
      panelClass: 'aon-user-dialog-panel',
      data: this.company
    });

    ref.afterClosed().subscribe();
  }

  market(): void {
    this.dialogRef.close();
  }

  configuration(): void {
    this.dialogRef.close();
  }
}
