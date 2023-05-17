import { AuthService } from 'src/app/core/services/auth.service';
import { Component, OnInit } from '@angular/core';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  user :string       ="";
  password :string;
  hidePassword :boolean = true;


  constructor(public auth: AuthService) {
    this.user     = "";
    this.password = "";
  }

  ngOnInit(): void {
  }

  clean(){
    this.user ="";
  }

  getUser(){
    console.log('User: ', this.user);
    console.log('password: ', this.password);
  }

  loginUser() {
    this.auth.login({
      username: this.user,
      password: this.password
    });
  }

}
