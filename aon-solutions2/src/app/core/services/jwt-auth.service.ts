import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JwtAuthService {

  constructor(private router: Router) { }

  login(token: string):void {
    sessionStorage.setItem(environment.localStorageJwt.accessToken, token);
  }

  logout() {
    sessionStorage.removeItem(environment.localStorageJwt.accessToken);
    this.router.navigateByUrl("/auth");
  }

  isLoggedIn(): boolean {
    const isLoggged = this.getJwt();

    if(isLoggged) {
      return true;
    }
    else {
      return false;
    }
  }

  getJwt() {
    return (sessionStorage.getItem(environment.localStorageJwt.accessToken) !== null);
  }
}
