import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { JwtAuthService } from './jwt-auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  urlBase:string = environment.urlApiAon;


  constructor(private http: HttpClient, private jwtAuth: JwtAuthService, private router: Router) { }

  login(user: any) {
    this.http.post<any>(this.urlBase+'login', user).subscribe(data => { // Subscribe actua como una 'promesa' de JS
      this.jwtAuth.login(data.session_id);
      this.router.navigateByUrl("/home");
    });
  }

  logout() {
    this.jwtAuth.logout();
    this.jwtAuth.setHeaderSessionId('');
  }

  getUser(): Observable<User> {
    let _headers: HttpHeaders = new HttpHeaders(this.jwtAuth.getHeaders());

    return this.http.get<User>(this.urlBase+'auth', {headers: _headers});
  }
}
