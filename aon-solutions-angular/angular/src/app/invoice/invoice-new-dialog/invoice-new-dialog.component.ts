import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { SharedService } from '../../services/shared.service';
//import { environment } from '../../../environments/environment';
import { SpeechService } from 'ngx-speech';
import { RootLoader } from '../../utils/loader';

@Component({
  templateUrl: './invoice-new-dialog.component.html',
  styleUrls: ['./invoice-new-dialog.component.css'],
})
export class InvoiceNewDialogComponent {

  constructor(public dialogRef: MatDialogRef<InvoiceNewDialogComponent>,
          @Inject(MAT_DIALOG_DATA) public data: any, private router: Router,
        public service: SharedService, public speech: SpeechService,
        private location: Location) {

  }

  onNoClick(): void {

  }

  isSnapshot(): boolean {
    return false; // !environment.production;
  }

  received(): void {
    const result = {
      type: 'RECIBIDA'
    };
    this.dialogRef.close(result);
  }

  issued(): void {
    const result = {
      type: 'EMITIDA'
    };
    this.dialogRef.close(result);
  }

  ticket(): void {
    const result = {
      type: 'TICKET'
    };
    this.dialogRef.close(result);
  }

  upload(): void {
    RootLoader.angularPanel(this.router, this.location, '/invoice/upload');
    this.dialogRef.close();
  }

  record(): void {
    this.speech.start();
  }

}
