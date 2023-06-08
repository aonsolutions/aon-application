import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtAuthService } from './jwt-auth.service';
import { Router } from '@angular/router';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root' 
})
export class AuthService {

  constructor(private jwtAuthService: JwtAuthService, private apiService: ApiService, private router: Router) { }
  
  login(email: string, password: string): void{
    let body = {
      username: email,
      password: password
    };

    this.apiService.post('login', body).subscribe(
      (response) => {
        // Si ha ido bien la petición de login
        if (response.session_id) {
          this.jwtAuthService.setJwt(response.session_id)
          // sessionStorage.setItem(environment.localStorageJwt.accessToken, response.session_id);
          this.router.navigate(['/auth/selectEnterprise']);
        }
      },
      (error) => {
        console.error(`Se ha producido un error al realizar login: ${error}`)
      }
    );

  }

  logout() {
    this.jwtAuthService.logout();
  }

  isLoggedIn(): boolean {
    return this.jwtAuthService.isLoggedIn();
  }
}

