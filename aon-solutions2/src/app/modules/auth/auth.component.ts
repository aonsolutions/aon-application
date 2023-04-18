import { Component, OnInit } from '@angular/core';
import { ApiService } from 'src/app/shared/services/api.service';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss'],

})

export class AuthComponent implements OnInit {
  user :string       ="";
  password :string;
  hidePassword :boolean = true;

  constructor(public api: ApiService) {
    this.user     = "";
    this.password = "";
  }

  ngOnInit(): void {
  }

  clean(){
    this.user ="";
    console.log('user');
  }

  getUser(){
    console.log('User: ', this.user);
    console.log('password: ', this.password);
  }

  loginUser() {
    this.api.login({
      username: this.user,
      password: this.password
    });
  }
}
