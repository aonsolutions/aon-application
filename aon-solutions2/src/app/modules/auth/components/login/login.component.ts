import { Component, OnInit, Output } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { Filter } from 'libraries/AonSDK/AonSDK';
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
  filter: Filter = {
    filterFields:{
      fields:{
        name:12903
      }
    }
  };

  constructor(public auth: AuthService, private router: Router, private activatedRoute: ActivatedRoute, public taxModelService: TaxModelService) { }

  ngOnInit(): void {
    this.sub = this.activatedRoute.queryParams.subscribe(params => {
      if(params['token'] != undefined)
        this.auth.magicLogin(params['token'])
    });
    
    this.taxModelService.getTaxModelList(this.filter).then((response) => {
      console.log(response);
    })
  }

  loginUser() {
    this.auth.login(this.username, this.password);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
