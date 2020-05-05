import { Injectable } from '@angular/core';
import { Observable, Observer } from 'rxjs';
import { Request } from '../utils/request';
import { Company, Invoice } from '../models/AonModel';

@Injectable()
export class AonService {

  companies: Company[];
  company: Company;

  getToken(): string {
    return localStorage.getItem('aon_session_id');
  }

  login(username: string, password: string) : Observable<any> {
  	const data = {
  			username: username,
  			password: password
  	}
    return Request.request('POST', '/ms/api/login', undefined, data);
  }

  // Company

  getCompanies() : Observable<Company[]> {
    if (this.companies) {
      return Observable.create((observer: Observer<Company[]>) => {
        observer.next(this.companies);
        observer.complete();
      });
    } else {
      return Observable.create((observer: Observer<Company[]>) => {
        Request.request('GET', '/ms/api/company', this.getToken())
        .subscribe(result => {
          this.companies = result;
          observer.next(this.companies);
          observer.complete();
        }, error => {
          observer.error(error);
        });
      });
    }
  }

  // Invoice
  getInvoice(id: number) : Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      Request.request('GET', '/ms/api/invoice', this.getToken(), {id})
      .subscribe(result => {
        observer.next(result);
        observer.complete();
      }, error => {
        observer.error(error);
      });
    });
  }

  getInvoices(data) : Observable<Invoice[]> {
    return Observable.create((observer: Observer<Invoice[]>) => {
      Request.request('GET', '/ms/api/invoice' + this.getInvoiceQuery(data), this.getToken(), data)
      .subscribe((result: Invoice[]) => {
        observer.next(result);
        observer.complete();
      }, error => {
        observer.error(error);
      });
    });
  }

  createInvoice(invoice: Invoice) : Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      Request.request('POST', '/ms/api/invoice', this.getToken(), invoice)
      .subscribe((result: Invoice) => {
        observer.next(result);
        observer.complete();
      }, error => {
        observer.error(error)
      });
    });
  }

  updateInvoice(invoice: Invoice) : Observable<Invoice> {
    return this.createInvoice(invoice);
  }

  deleteInvoice(invoiceId: Number) : Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      Request.request('DELETE', '/ms/api/invoice', this.getToken(), {id: [invoiceId]})
      .subscribe((result) => {
        observer.next(result);
        observer.complete();
      }, error => {
        observer.error(error)
      });
    });
  }

  deleteInvoices(invoiceIds: Number[]) : Observable<Invoice> {
    return Observable.create((observer: Observer<Invoice>) => {
      Request.request('DELETE', '/ms/api/invoice', this.getToken(), {id: invoiceIds})
      .subscribe((result) => {
        observer.next(result);
        observer.complete();
      }, error => {
        observer.error(error)
      });
    });
  }

  getInvoiceQuery(params){
    let query = '';
    if(params.id) query = query + (query =='' ? '?id=' : '&id=') + params.id;
    if(params.document) query = query + (query =='' ? '?document=' : '&document=') + params.document;
    if(params.reference) query = query + (query =='' ? '?reference=' : '&reference=') + params.reference;
    if(params.toDate) query = query + (query =='' ? '?toDate=' : '&toDate=') + params.toDate;
    if(params.fromDate) query = query + (query =='' ? '?fromDate=' : '&fromDate=') + params.fromDate;
    if(params.type) query = query + (query =='' ? '?type=' : '&type=') + params.type;
    if(params.status) query = query + (query =='' ? '?status=' : '&status=') + params.status;
    if(params.q) query = query + (query =='' ? '?q=' : '&q=') + params.q;
    if(params.category) query = query + (query =='' ? '?category=' : '&category=') + params.category;
    if(params.last) query = query + (query =='' ? '?last=' : '&last=') + params.last;
    if(params.verified != undefined) query = query + (query =='' ? '?verified=' : '&verified=') + params.verified;

    return query
  }
}
