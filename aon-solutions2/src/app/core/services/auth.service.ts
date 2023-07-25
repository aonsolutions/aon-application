import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  
  authManager = new AuthenticationFactory().createAuthenticationManager()

  constructor(private router: Router) {}

  async login(email: string, password: string): Promise<boolean> {
    let login = (await this.authManager.login(email, password)).result;
    if(login) this.router.navigate(['/selectEnterprise'])
    return login;
  }

  async logout(): Promise<void> {
    await this.authManager.logout();
    this.router.navigate(['/'])
  }

  isLoggedIn(): boolean {
    return this.authManager.isAuthenticated().result;
  }

  async magicLogin(token: string): Promise<boolean> {
    return (await this.authManager.tokenLogin(token)).result;
  }

  setEnterprise(cif: string): boolean {
    return this.authManager.setEnterprise(cif).result;
  }

  isEnterpriseSelected(): boolean {
    return this.authManager.isEnterpriseSelected().result;
  }

  getEnterpriseSelected(): string {
    return this.authManager.getEnterpriseSelected().result;
  }

}