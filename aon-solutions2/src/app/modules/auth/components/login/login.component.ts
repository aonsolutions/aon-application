import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { AuthService } from 'src/app/core/services/auth.service';
import { ErrorResponse } from 'libraries/AonSDK/src/aon';

@Component({
  selector    : 'app-login',
  templateUrl : './login.component.html',
  styleUrls   : ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  private sub : any = null
  username    : string  = "";
  password    : string  = "";
  spinner     : boolean = false;

  constructor(
    public auth: AuthService,
    private activatedRoute: ActivatedRoute,
  ) {

  }

  ngOnInit(): void {
    this.sub = this.activatedRoute.queryParams.subscribe(params => {
      if(params['token'] != undefined)
        this.auth.magicLogin(params['token'])
    });
  }

  loginUser() {
    this.spinner = true;

    this.auth.login(this.username, this.password).then(() => {
      this.spinner = false;
    });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
