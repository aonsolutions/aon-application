import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { SharedService } from '../../services/shared.service';
import { Observable } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { MatDialog } from '@angular/material';
import { Location } from '@angular/common';
import { TediUtils } from '../../utils/tedi-utils';

@Component({
  selector: 'app-user-change-password',
  templateUrl: './user-change-password.component.html',
  styleUrls: ['./user-change-password.component.css']
})
export class UserChangePasswordComponent implements OnInit {
  user$: Observable<string>;
  email = '';
  constructor(private route: ActivatedRoute, public service: SharedService,
     public dialog: MatDialog, private _location: Location) {}

  ngOnInit() {
    this.user$ =  this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        return params.get('email');
      })
    );
    this.user$.subscribe(
      result => this.email = this.email + result,
      error => TediUtils.showError(this.dialog, error.error)
    );
  }

  public changePassword(oldPassword: string, newPassword: string, repeatNewPassword: string): void {
    if (newPassword === repeatNewPassword) {
      const user = {
        email: this.email,
        password: newPassword
      };
      this.service.updateUser(user).subscribe(
        () => this._location.back(),
        (e: any) => TediUtils.showError(this.dialog, e.error)
      );
    } else {
      TediUtils.showError(this.dialog, 'Las contraseñas no coinciden.');
    }
  }
}
