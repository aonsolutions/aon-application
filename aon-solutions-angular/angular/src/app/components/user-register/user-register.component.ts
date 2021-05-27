import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SharedService, UserService } from '../../services/services';
import { User, AonMaker } from '../../models/models';
import { Observable } from 'rxjs';
import { MatDialog } from '@angular/material';

import { TediUtils } from '../../utils/tedi-utils';
import TEDI from '@translogia/tedi-sdk';
import { environment } from '../../../environments/environment';
@Component({
  selector: 'app-user-register',
  templateUrl: './user-register.component.html',
  styleUrls: ['./user-register.component.css']
})
export class UserRegisterComponent implements OnInit {
  private readonly API_URL = environment.apiUrl;
  user$: Observable<any>;
  user: User = AonMaker.createUser();
  isUserLoggedIn = false;
  hide = true;

  emailFormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  matcher = new MyErrorStateMatcher();

  constructor(public httpClient: HttpClient, private route: ActivatedRoute,
     private router: Router, public dialog: MatDialog,
     private service: SharedService, private usrService: UserService ) {}

  ngOnInit() {
    this.isUserLoggedIn = localStorage.getItem('tedi_session_id') !== undefined;
  }

  public register(email: string, password: string, name: string, surname: string, document: string, phone: string): void {
    const user = AonMaker.createUser();
    user.email = email;
    user.name = name;
    user.surname = surname;
    user.phone = phone;
    user.company = this.service.user.company;
    user.document = document;
    user.users = [this.service.getUserEmail()];
    user.password = password;
    user.actual_company = this.service.getActualCompany();

    TEDI.user.createUser(user, this.service.getToken(), this.API_URL)
    .subscribe(
        result => {
          this.service.getCompany().subscribe(
            cp => {
              if (cp.users) { cp.users.push(user.email); } else { cp.users = [user.email]; }
              const updateCompany = {
                document: cp.document,
                users: cp.users
              };
              TEDI.company.updateCompany(updateCompany, this.service.getToken(), this.API_URL).subscribe();
            }
          );
          this.usrService.user = user;
          this.router.navigate(['/myAccount/permission', {email: email}]);
        },
        error => TediUtils.showError(this.dialog, error.error.error)
    );
  }

  public registerNew(email: string, password: string, name: string, surname: string, document: string, phone: string): void {
    this.service.newUser = email;
    const user = AonMaker.createUser();
    user.email = email;
    user.name = name;
    user.surname = surname;
    user.phone = phone;
    user.document = document;
    user.password = password;

    TEDI.user.createUser(user, 'LsJgU81czVQRlPM', this.API_URL)
    .subscribe(
      () => this.router.navigate(['/registerCompany']),
      (error: any) => TediUtils.showError(this.dialog, error.error)
    );
  }
}

/** Error when invalid control is dirty, touched, or submitted. */
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}
