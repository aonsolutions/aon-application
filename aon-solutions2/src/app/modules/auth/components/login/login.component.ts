import { Component, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  private sub : any = null
  username :string = "";
  password :string = "";

  constructor(public auth: AuthService, private router: Router, private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.sub = this.activatedRoute.params.subscribe(params => {
      if(params['token'] != undefined)
        this.auth.magicLogin(params['token'])
   });
  }

  loginUser() {
    this.auth.login(this.username, this.password);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
