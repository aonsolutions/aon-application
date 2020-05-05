import { MAT_DIALOG_DATA, MatDialogRef, MatDialog} from '@angular/material';
import { Component, OnInit, Inject } from '@angular/core';
import { SharedService } from '../../../services/shared.service';
import { User } from '../../../models/models';
import { TediUtils } from '../../../utils/tedi-utils';

@Component({
  templateUrl: './user-list-dialog.component.html',
  styleUrls: ['./user-list-dialog.component.css'],
})
export class UserListDialogComponent implements OnInit {
  title = 'Usuarios';
  public users: any[] = [];
  user: any;

  constructor(public dialogRef: MatDialogRef<UserListDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any,
              private service: SharedService, public dialog: MatDialog) {
      this.user = data;
  }

  ngOnInit() {
    this.service.getUsers().subscribe(
      (result: User[]) => this.users = result,
      (error: any) => TediUtils.showError(this.dialog, error.error)
    );
  }

  onNoClick(): void {

  }

  search(value: string): void {
    this.service.getUsers({filter: value}).subscribe(
      (result: User[]) => this.users =  result,
      (error: any) => TediUtils.showError(this.dialog, error.error)
    );
  }

  onSelect(user: any, event: any): void {
    if (event) {
      user.users.push(this.user.email);
    } else {
      const index = user.users.indexOf(this.user.email, 0);
      if (index > -1) {
        user.users.splice(index, 1);
      }
    }
    const usr = {
      email: user.email,
      users: user.users
    };
    this.service.updateUser(usr).subscribe();
  }

  accept(): void {
    this.dialogRef.close();
  }

  hasUsers(): boolean {
    return this.users.length === 0;
  }

  hasUser(user: any): boolean {
    return user.users && user.users.length > 0 && user.users.includes(this.user.email);
  }

}
