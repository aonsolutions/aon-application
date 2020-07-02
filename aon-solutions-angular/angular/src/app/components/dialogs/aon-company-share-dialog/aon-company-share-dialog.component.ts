import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, OnDestroy, Inject } from '@angular/core';
import { Company } from '../../../models/models';

@Component({
  templateUrl: './aon-company-share-dialog.component.html',
  styleUrls: ['./aon-company-share-dialog.component.css'],
})
export class AonCompanyShareDialogComponent implements OnDestroy {

  company: Company;

  constructor(public dialogRef: MatDialogRef<AonCompanyShareDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: Company) {
    console.log(data.name);
    this.company = data;
  }

  ngOnDestroy() {
    this.dialogRef.close();
  }

  accept(email: string) {
    console.log(email);
    this.dialogRef.close();
  }
}
