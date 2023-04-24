import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-rate-inbox',
  templateUrl: './rate-inbox.component.html',
  styleUrls: ['./rate-inbox.component.scss']
})
export class RateInboxComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<RateInboxComponent>, @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
