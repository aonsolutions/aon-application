import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable , Observer } from 'rxjs';
import { environment } from '../../environments/environment';
import { CompanyFilter, Company, AonMaker } from '../models/models';

@Injectable()
export class CompanyService {

  private readonly API_URL = environment.apiUrl;

  public isNew = false;
  public company: Company = AonMaker.createCompany();

  public companies: Company[];

  filter: CompanyFilter;
  filterObservable: Observable<any>;
  filterObserver: Observer<any>;

  constructor (private httpClient: HttpClient) {
    this.filterObservable = Observable.create((observer: Observer<any>) => this.filterObserver = observer);
  }

  setFilter(filter: any) {
    this.filter = filter;
    this.filterObserver.next(this.filter);
  }

  setCompany(data: Company): void {
    this.company = AonMaker.createCompany(data);
  }

  getPrinterConfiguration(): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');

    return this.httpClient.get<any>(this.API_URL + 'printer_configuration', {headers: headers});
  }

  updatePrinterConfiguration(requestData: string): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');
    return this.httpClient.post(this.API_URL + 'printer_configuration', requestData, {headers: headers});
  }
}
