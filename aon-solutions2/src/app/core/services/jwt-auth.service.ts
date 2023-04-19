import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JwtAuthService {

  constructor() { }

  login(token: string):void {
    localStorage.setItem(environment.localStorageJwt.accessToken, token);
  }

  isLoggedIn(): boolean {
    const isLoggged = localStorage.getItem(environment.localStorageJwt.accessToken);

    if(isLoggged) {
      return true;
    }
    else {
      return false;
    }
  }
}
