import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AonSDK } from 'libraries/AonSDK/AonSDK';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  
  aonSDK: AonSDK = new AonSDK();

  constructor(private router: Router) {}

  login(email: string, password: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.aonSDK
        .model('auth')
        .login(email, password)
        .then((response: any) => {
          if (response.result == true) {
            this.router.navigate(['/selectEnterprise']);
          }
          resolve(true);
        });
    });
  }

  logout(): void {
    this.aonSDK
      .model('auth')
      .logout()
      .then((response: any) => {
        if (response.result == true) this.router.navigate(['/']);
      });
  }

  isLoggedIn(): boolean {
    if (this.aonSDK.model('auth').getSession().result.token) {
      return true;
    } else {
      return false;
    }
  }

  magicLogin(token: string): Promise<boolean> {
    return new Promise((resolve) => {
      this.aonSDK
        .model('auth')
        .magicLogin(token)
        .then((response: any) => {
          if (response.result == true) {
            this.router.navigate(['/selectEnterprise']);
          }
          resolve(true);
        });
    });
  }

}