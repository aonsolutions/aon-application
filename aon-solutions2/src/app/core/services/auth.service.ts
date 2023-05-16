import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Usuario } from '../models/usuario';
import { Observable } from 'rxjs';
import { JwtAuthService } from './jwt-auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  urlBase:string = environment.urlApiAon;
  headers: any = {
    domain_name: environment.headerApi.domainName,
    domain_id: environment.headerApi.domainId,
    domain_login: environment.headerApi.domainLogin,
  };

  constructor(private http: HttpClient, private jwtAuth: JwtAuthService, private router: Router) { }

  login(user: any) {
    this.http.post<any>(this.urlBase+'login', user).subscribe(data => { // Subscribe actua como una 'promesa' de JS
      this.headers['session_id'] = data.session_id;
      this.jwtAuth.login(data.session_id);
      this.router.navigateByUrl("/home");
    });
  }

  logout() {
    this.jwtAuth.logout();
  }

  getUser(): Observable<Usuario> {
    let cabeceras: HttpHeaders = new HttpHeaders(this.headers);

    return this.http.get<Usuario>(this.urlBase+'auth', {headers: cabeceras});
  }
}
