import { IEnterprise } from './../models/interface/enterprise';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, Output } from '@angular/core';
import { JwtAuthService } from './jwt-auth.service';
import { Router } from '@angular/router';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  auxEmpresas!: IEnterprise[];


  constructor(private http: HttpClient, private jwtAuth: JwtAuthService, private apiService: ApiService, private router: Router) { }


  login(email: string, password: string): void{
    let body = {
      username: email,
      password: password
    };

    this.apiService.post('login', body).subscribe(
      (response) => {
        // Si ha ido bien la petición de login
        if (response.session_id) {
          sessionStorage.setItem(environment.localStorageJwt.accessToken, response.session_id);
          this.router.navigate(['/auth/selectEnterprise']);
        }
      },
      (error) => {
        console.error(`Se ha producido un error al realizar login: ${error}`)
      }
    );

  }

  logout() {
    this.jwtAuth.logout();
  }

  isLoggedIn(): boolean {
    return this.jwtAuth.isLoggedIn();
  }

}
