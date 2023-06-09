import { Injectable } from '@angular/core';
import { IInvoiceService } from '../models/interface/iinvoice-service';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService implements IInvoiceService {

  constructor(public apiService: ApiService) { }

  getInvoices(): Observable<any> {
    let params = {"status": "inbox", "per_page": 25, "page" : 2}
    return this.apiService.get('invoice', params);
  }

}
