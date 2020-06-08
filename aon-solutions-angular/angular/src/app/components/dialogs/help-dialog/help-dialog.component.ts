import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { SharedService } from '../../../services/shared.service';
import { UserService } from '../../../services/user.service';

@Component({
  templateUrl: './help-dialog.component.html',
  styleUrls: ['./help-dialog.component.css'],
})
export class HelpDialogComponent {

  almaLogo = 'assets/apps/alma.png';
  constructor(public dialogRef: MatDialogRef<HelpDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private router: Router,
              private service: SharedService, private userService: UserService) { }

  onNoClick(): void {

  }

  faq(): void {
    open('https://faqs.aonsolutions.es/', '_blank', null);
  }

  alma(): void {

  }

  info() : void {

  }
}
