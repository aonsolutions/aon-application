import { IApi } from './../models/interface/iapi';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService implements IApi {

  private urlBase = environment.urlApiAon;

  constructor(private http: HttpClient) { }

  get(method: string, params?: any): Observable<any> {
    let headers = this.setHeaders();
    return this.http.get(this.urlBase+method, { headers, params });
  }

  post(method: string, data: any): Observable<any> {
    let headers = this.setHeaders();
    return this.http.post(this.urlBase+method, data, { headers });
  }

  setHeaders(): HttpHeaders{
    let token = sessionStorage.getItem(environment.localStorageJwt.accessToken);
    let domainName = environment.headerApi.domainName;
    let headers = new HttpHeaders();
    if (token !== null) {
      headers = headers.set('session_id', token);
    }
    headers = headers.set('domain_name', domainName);
    return headers;
  }

}
