import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

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

  constructor(private http: HttpClient) { }

  // List companies of a user
  getCompanies() {

  }
}
