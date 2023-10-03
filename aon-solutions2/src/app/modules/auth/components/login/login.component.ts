import { Component, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ErrorResponse } from 'libraries/AonSDK/src/aon';
import { AuthService } from 'src/app/core/services/auth.service';

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
    private router: Router, 
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
      try {
        this.auth.login(this.username, this.password);
      } catch (error) {
        throw error instanceof ErrorResponse ? error : new ErrorResponse(error);
      } finally {
        this.spinner = false;
      }
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
