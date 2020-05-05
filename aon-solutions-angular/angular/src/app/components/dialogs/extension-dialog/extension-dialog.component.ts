import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material';
import { SharedService } from '../../../services/shared.service';

@Component({
  selector: 'app-extension-dialog',
  templateUrl: './extension-dialog.component.html',
  styleUrls: ['./extension-dialog.component.css']
})
export class ExtensionDialogComponent {

  constructor(public dialogRef: MatDialogRef<ExtensionDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any, public dialog: MatDialog,
      public service: SharedService) {}

  aon() {
    this.service.getCompany().subscribe(company => {
      if (company.aon && company.aon.domain) {
        open('https://' + company.aon.domain);
        this.dialogRef.close();
      } else { open('https://aonsolutions.es'); }
    });
  }
}
