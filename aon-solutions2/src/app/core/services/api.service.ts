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

  get(method: string): Observable<any> {
    return this.http.get(this.urlBase+method)
  }

  getWithHeaders(method: string, headers: HttpHeaders): Observable<any> {
    return this.http.get(this.urlBase+method, { headers });
  }


  post(method: string, data: any): Observable<any> {
    return this.http.post(this.urlBase+method, data);
  }

}
