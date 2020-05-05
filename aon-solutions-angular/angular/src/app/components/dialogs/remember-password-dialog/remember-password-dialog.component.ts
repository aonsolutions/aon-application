import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material';
import { Component, OnInit, Inject } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { SharedService } from '../../../services/shared.service';

@Component({
  templateUrl: './remember-password-dialog.component.html',
  styleUrls: ['./remember-password-dialog.component.css'],
})
export class RememberPasswordDialogComponent implements OnInit {
  title = 'Recuperar Contraseña';

  constructor(public dialogRef: MatDialogRef<RememberPasswordDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private userService: UserService, private service: SharedService,
              public dialog: MatDialog) {}

  ngOnInit() {}


  accept(email: string): void {
    const user = {
      email: email
    };
    const token = this.service.getToken() || 'LsJgU81czVQRlPM';
    this.userService.rememberPassword(user, token)
    .subscribe(
        () => this.dialogRef.close(),
        () => this.dialogRef.close()
    );
  }
}
