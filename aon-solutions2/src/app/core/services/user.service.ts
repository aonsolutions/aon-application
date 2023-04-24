import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Enterprise } from '../components/topbar/topbar.component';
import { JwtAuthService } from './jwt-auth.service';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  urlBase:string = environment.urlApiAon;
  headers: any = {
    domain_name: environment.headerApi.domainName,
    domain_id: environment.headerApi.domainId,
    domain_login: environment.headerApi.domainLogin,
  };

  constructor(private http: HttpClient, private jwtAuth: JwtAuthService) { }

  // List companies of a user
  getCompanies(): Observable<Enterprise> {
    let _headers: HttpHeaders = new HttpHeaders(this.jwtAuth.getHeaders());

    return this.http.get<Enterprise>(this.urlBase+'company', {headers: _headers});
  }
}
