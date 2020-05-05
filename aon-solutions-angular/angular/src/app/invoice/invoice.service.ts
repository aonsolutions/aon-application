import { Injectable } from '@angular/core';
import { Subject ,  Observable, Observer } from 'rxjs';
import { Invoice, InvoiceType, AonMaker } from '../models/models';
import { SharedService, AonService } from '../services/services';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AngularFireStorage } from '@angular/fire/storage';
import { environment } from '../../environments/environment';

@Injectable()
export class InvoiceService {

  public sidenav: any;
  public isCompany = false;
  public isDeleting = false;
  public isFileOpen: Subject<boolean> = new Subject<boolean>();

// Temporarily stores data from dialogs
  dialogData: any;

  public invoice: Invoice = AonMaker.createInvoice();
  public invoiceFile: any;
  public invoiceSelection:  Subject<Invoice[]> = new Subject<Invoice[]>();

  public changeInvoice: Subject<boolean> = new Subject<boolean>();
  public invoiceList: Invoice[] = [];
  public invoiceIndex: number;
  public isNew: boolean;
  public newType: InvoiceType;

  isFileExpanded = false;
  fileExpanded: Observable<boolean>;
  fileExpandedObserver: Observer<boolean>;

  isMenuExpanded = true;
  menuExpanded: Observable<boolean>;
  menuExpandedObserver: Observer<boolean>;

  filter: any;
  filterObservable: Observable<any>;
  filterObserver: Observer<any>;

  constructor (private httpClient: HttpClient, private storage: AngularFireStorage,
      public service:  SharedService, public aonService: AonService ) {
    this.menuExpanded = Observable.create((observer: Observer<boolean>) => this.menuExpandedObserver = observer);
    this.fileExpanded = Observable.create((observer: Observer<boolean>) => this.fileExpandedObserver = observer);
    this.filterObservable = Observable.create((observer: Observer<any>) => this.filterObserver = observer);
  }

  setInvoice(data: Invoice): void {
    this.invoice = AonMaker.createInvoice(data);
  }

  getInvoice(id: number): Observable<Invoice> {
    return this.aonService.getInvoice(id);
  }

  getInvoices(status: string, type: InvoiceType): Observable<Invoice[]> {
     return this.aonService.getInvoices({status, type});
  }

  isSnapshot(): boolean {
    return !environment.production;
  }

  saveInvoice(invoice?: Invoice): void {
    this.aonService.createInvoice(invoice ? invoice : this.invoice)
    .subscribe((r: Invoice) => {
      this.setInvoice(r);
    });
  }

  createInvoice(invoice?: Invoice): Observable<Invoice> {
    return this.aonService.createInvoice(invoice ? invoice : this.invoice);
  }

  updateInvoice(updateData): void {
    if (this.invoice.id) {
      updateData.id = this.invoice.id;
      updateData.domain = this.invoice.domain;
      this.aonService.updateInvoice(updateData).subscribe();
    } else {
      this.saveInvoice();
    }
  }

  deleteInvoice(invoice?: Invoice): Observable<any> {
    invoice = invoice || this.invoice;
    return this.aonService.deleteInvoice(invoice.id);
  }

  deleteInvoices(ids: number[]): Observable<any> {
    return this.aonService.deleteInvoices(ids);
  }

  expandMenu(value?: boolean) {
    this.isMenuExpanded = value !== undefined ? value : !this.isMenuExpanded;
    this.menuExpandedObserver.next(this.isMenuExpanded);
  }

  expandFile(value?: boolean) {
    this.isFileExpanded = value !== undefined ? value : !this.isFileExpanded;
    this.fileExpandedObserver.next(this.isFileExpanded);
  }

  setFilter(filter: any) {
    this.filter = filter;
    this.filterObserver.next(this.filter);
  }

  getDialogData() {
    return this.dialogData;
  }

  getInvoiceType(type: InvoiceType): Observable<any> {
    const account700 = {
      category: '700.0',
      name: 'Ventas de mercaderías',
      description: '700. Ventas de mercaderías'
    };

    const account705 = {
      category: '705.0',
      name: 'Prestación de servicios',
      description: '705. Prestación de servicios'
    };

    const account600 = {
      category: '600.0',
      name: 'Compras de mercaderías',
      description: '600. Compras de mercaderías'
    };

    const account607 = {
      category: '607.0',
      name: 'Trabajos realizados por otras empresas',
      description: '607. Trabajos realizados por otras empresas'
    };

    const account621 = {
      category: '621.0',
      name: 'Arrendamiento y cánones',
      description: '621. Arrendamiento y cánones'
    };

    const account622 = {
      category: '622.0',
      name: 'Reparaciones y convervación',
      description: '622. Reparaciones y conservación'
    };

    const account623 = {
      category: '623.0',
      name: 'Servicios de profesionales independientes',
      description: '623. Servicios de profesionales independientes'
    };

    const account624 = {
      category: '624.0',
      name: 'Transportes',
      description: '624. Transportes'
    };

    const account625 = {
      category: '625.0',
      name: 'Primas de seguros',
      description: '625. Primas de seguros'
    };

    const account626 = {
      category: '626.0',
      name: 'Servicios bancarios y similares',
      description: '626. Servicios bancarios y similares'
    };

    const account627 = {
      category: '627.0',
      name: 'Publicidad, propaganda y relaciones públicas',
      description: '627. Publicidad, propaganda y relaciones públicas'
    };

    const account628 = {
      category: '628.0',
      name: 'Suministros',
      description: '628. Suministros'
    };

    const account629 = {
      category: '629.0',
      name: 'Otros gastos',
      description: '629. Otros gastos',
      icon: 'receipt'
    };

    const account6291 = {
      category: '629.1',
      name: 'Alojamiento',
      description: '629.1 Alojamiento',
      icon: 'local_hotel'
    };

    const account6292 = {
      category: '629.2',
      name: 'Aparcamiento',
      description: '629.2. Aparcamiento',
      icon: 'local_parking'
    };

    const account6293 = {
      category: '629.3',
      name: 'Combustible',
      description: '629.3. Combustible',
      icon: 'ev_station'
    };

    const account6294 = {
      category: '629.4',
      name: 'Desplazamientos',
      description: '629.4 Desplazamientos',
      icon: 'commute'
    };

    const account6295 = {
      category: '629.5',
      name: 'Dietas',
      description: '629.5 Dietas',
      icon: 'local_dining'
    };

    const account6296 = {
      category: '629.6',
      name: 'Peaje',
      description: '629.6 Peaje',
      icon: 'settings_remote'
    };

    const account6297 = {
      category: '629.7',
      name: 'Kilometraje',
      description: '629.7. Kilometraje',
      icon: ''
    };

    const account6298 = {
      category: '629.8',
      name: 'Multas y sanciones',
      description: '629.8. Multas y sanciones',
      icon: ''
    };

    const account6299 = {
      category: '629.9',
      name: 'Tasas y tributos',
      description: '629.9. Tasas y tributos',
      icon: ''
    };

    return Observable.create((observer: Observer<any>) => {
      if (type === InvoiceType.PURCHASE) {
        observer.next([account700, account705]);
      } else if (type === InvoiceType.EXPENSES || type === InvoiceType.SALES) {
        observer.next([account600, account607, account621, account622, account623,
           account624, account625, account626, account627, account628, account629,
           account6291, account6292, account6293, account6294, account6295,
           account6296]);
      } else if (type === InvoiceType.UNDEDUCTIBLE) {
        observer.next([account629, account6291, account6292, account6293, account6294,
           account6295, account6296, account6297, account6298, account6299]);
      }
      observer.complete();
    });
  }

  // getInvoiceType(type:string) : Observable<any> {
  //   let headers = new HttpHeaders();
  //   headers = headers.append('session_id',localStorage.getItem("tedi_session_id"));
  //   headers = headers.append('Content-Type','application/json');
  //   let params = new HttpParams();
  //   params = params.set('type', type);
  //   return this.httpClient.get<any>(this.TEDI_URL + 'type', {headers: headers, params: params});
  // }

  // getInvoice(company:string, number:number): Observable<any> {
  //   let headers = new HttpHeaders();
  //   headers = headers.append('session_id',localStorage.getItem("tedi_session_id"));
  //   headers = headers.append('Content-Type','application/json');
  //   return this.httpClient.get<any>(this.API_URL + company + '/' + number, {headers: headers});
  // }

  downloadInvoice(company: string, uuid: string, status: string): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');
    return this.httpClient.get<any>(`${/*this.TEDI_URL*/''}/invoice/d/${status}/${company}/${uuid}`, {headers: headers});
  }

  downloadPreview(company: string): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');
    return this.httpClient.get<any>(/*this.TEDI_URL +*/ '/invoice/d/preview/' + company, {headers: headers});
  }

  downloadRecord(uuid: string): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');
    return this.httpClient.get<any>(/*this.TEDI_URL +*/ '/invoice/d/record/' + uuid, {headers: headers});
  }

  sendInvoice(data: any): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');
    return this.httpClient.post<any>(/*this.TEDI_URL +*/ '/invoice/s', data,  {headers: headers});
  }

  /** AON **/

  exportAon(aon: any, invoice: any): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('session_id', localStorage.getItem('tedi_session_id'));
    headers = headers.append('Content-Type', 'application/json');
    invoice.login = aon.user;
    return this.httpClient.post('https://' + aon.domain + '/tedi2aon', invoice, {headers: headers});
  }

  getFile(): Observable<any> {
    return Observable.create((observer: Observer<any>) => {
      this.storage.ref('invoices/' + this.invoice.id).getDownloadURL()
        .subscribe(r => {
          this.invoiceFile = {
            url: r,
            contentType: this.invoice.file.content_type
          };
          observer.next(this.invoiceFile);
          observer.complete();
        }, e => observer.error(e));
    });
  }
}
