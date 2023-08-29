import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationFactory, IEnterprise } from 'libraries/AonSDK/aon';
import { TaxModelService } from './tax-model.service';

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

  setEnterprise(enterprise: IEnterprise): boolean {
    return this.authManager.setEnterprise(enterprise).result;
  }

  isEnterpriseSelected(): boolean {
    return this.authManager.isEnterpriseSelected().result;
  }

  getEnterpriseSelected(): string {
    return this.authManager.getEnterpriseSelected().result;
  }

  getUserPermissions(): string[] {
    // TODO: Definir los permisos del usuario
    return ['permiso1', 'permiso2', 'permiso3'];
  }

  getUserRoles(): string[] {
    // TODO: Definir los roles del usuario
    return ['rol1', 'rol2', 'rol3'];
  }


}
