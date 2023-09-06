import { Component, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  private sub : any = null
  username :string = "";
  password :string = "";

  constructor(public auth: AuthService, private router: Router, private activatedRoute: ActivatedRoute, public taxModelService: TaxModelService) { }

  ngOnInit(): void {
    this.sub = this.activatedRoute.queryParams.subscribe(params => {
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
