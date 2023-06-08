import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { map } from 'rxjs/operators';
import { Enterprise } from '../models/class/enterprise';

@Injectable({
  providedIn: 'root'
})
export class ExampleServiceService {

  constructor(private apiService: ApiService, private router: Router) { }

  getInvoices(): Observable<any> {
    let token = sessionStorage.getItem(environment.localStorageJwt.accessToken);
    let domainName = environment.headerApi.domainName;
    let headers = new HttpHeaders();

    if (token !== null) {
      headers = headers.set('session_id', token);
    }
    headers = headers.set('domain_name', domainName);

    let params = {"status": "inbox", "per_page": 25, "page" : 2}
    let paramsAux = {"id": "2133"}

    return this.apiService.get('invoice', params);
  }
}
