import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';
import { Component, Inject } from '@angular/core';
import { UserService } from '../../../services/services';

@Component({
  templateUrl: './advanced-user-search-dialog.component.html',
  styleUrls: ['./advanced-user-search-dialog.component.css']
})
export class AdvancedUserSearchDialogComponent {
  f: any;
  constructor(public dialogRef: MatDialogRef<AdvancedUserSearchDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any, private userService: UserService ) {
    this.f = {
      admin: this.userService.filter.admin || this.userService.filter.gestor,
      gestor: this.userService.filter.gestor,
      other: false
    };
  }

  updateAdmin(value: boolean): void {
    this.f.admin = value;
    if (!value) {
      this.f.gestor = value;
      this.f.other = value;
    }
  }

  updateGestor(value: boolean): void {
    this.f.gestor = value;
    if (value) {
      this.f.admin = value;
    } else {
      this.f.other = value;
    }
  }

  updateOther(value: boolean): void {
    this.f.other = value;
    if (value) {
      this.f.admin = value;
      this.f.gestor = value;
    }
  }

  search(): void {
    if (!this.f.other && this.f.admin) {
      if(this.f.gestor){
        this.userService.filter.admin = undefined;
        this.userService.filter.gestor = this.f.gestor;
      } else {
        this.userService.filter.gestor = undefined;
        this.userService.filter.admin = this.f.admin;
      }
    } else {
      this.userService.filter.gestor = undefined;
      this.userService.filter.admin = undefined;
    }
    this.userService.setFilter(this.userService.filter);
    this.dialogRef.close(true);
  }
}
