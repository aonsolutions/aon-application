import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-auth',
  templateUrl: './auth.component.html',
  styleUrls: ['./auth.component.scss'],

})

export class AuthComponent implements OnInit {

  title :string      = "Inicia Sesión";
  subtitle :string   ="Entra en tu cuenta de AON";
  login :string      ="INICIAR SESIÓN";
  orLogin :string    = "O INICIA SESIÓN";
  noPassword :string ="SIN CONTRASEÑA";
  user :string       ="";
  password :string;
  hidePassword :boolean = true;

  constructor() {
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
}
