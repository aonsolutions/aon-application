import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JwtAuthService {

  private headers: any = {
    domain_name: environment.headerApi.domainName,
    domain_id: environment.headerApi.domainId,
    domain_login: environment.headerApi.domainLogin,
  };

  constructor(private router: Router) { }

  login(token: string):void {
    sessionStorage.setItem(environment.localStorageJwt.accessToken, token);
    this.setHeaderSessionId(token);
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

  setHeaderSessionId(sessionId: string) {
    this.headers['session_id'] = sessionId;
  }

  getHeaders() {
    return this.headers;
  }
}
