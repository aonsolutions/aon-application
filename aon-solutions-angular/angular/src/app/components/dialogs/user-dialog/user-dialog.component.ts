import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { Router } from '@angular/router';
import { SharedService } from '../../../services/shared.service';
import { UserService } from '../../../services/user.service';

@Component({
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.css'],
})
export class UserDialogComponent {

  constructor(public dialogRef: MatDialogRef<UserDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private router: Router,
              private service: SharedService, private userService: UserService) { }

  onNoClick(): void {

  }

  closeSession(): void {
    localStorage.clear();
    this.dialogRef.close();
    this.service.isUserLoggedIn = false;
    this.service.close();
    this.router.navigate(['login']);
  }

  myAccount(): void {
    this.dialogRef.close();
    this.service.getUser().subscribe(r => {
      this.userService.user = r;
      this.router.navigate(['myAccount']);
    });
  }
}
