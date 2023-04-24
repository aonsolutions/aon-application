import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss'],

})

export class AuthComponent implements OnInit {
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
