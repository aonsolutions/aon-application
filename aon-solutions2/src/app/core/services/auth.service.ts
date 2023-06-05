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


  constructor(private http: HttpClient, private jwtAuthService: JwtAuthService, private apiService: ApiService, private router: Router) { }


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

  getListEnterprises(): Observable<any> {
    let token = sessionStorage.getItem(environment.localStorageJwt.accessToken);
    let domainName = environment.headerApi.domainName;
    let headers = new HttpHeaders();

    if (token !== null) {
      headers = headers.set('session_id', token);
    }
    headers = headers.set('domain_name', domainName);

    return this.apiService.getWithHeaders('company', headers);


  }

}
