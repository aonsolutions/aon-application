import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { Invoice, InvoiceType, PayMethod } from '../../models/models';
import { InvoiceService } from '../invoice.service';
import { SharedService } from '../../services/shared.service';
import { Subscription } from 'rxjs';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'app-invoice-mobile-list',
  templateUrl: './invoice-mobile-list.component.html',
  styleUrls: ['./invoice-mobile-list.component.css'],
})
export class InvoiceMobileListComponent implements OnInit, OnDestroy {
  listTitle = 'INBOX';
  subscription: Subscription;
  invoiceList: Invoice[];

  constructor(private router: Router, private location: Location,
      public invoiceService: InvoiceService, private service: SharedService) {
    this.subscription = this.invoiceService.filterObservable
    .subscribe(
      value => {
        this.listTitle = this.getTitle(value);
        value.company = this.service.getActualCompany();
        if(!this.invoiceService.invoiceList || this.invoiceService.invoiceList.length === 0 || this.invoiceService.invoiceList[0].domain !== this.service.getActualCompany() ||
             this.invoiceService.invoiceList[0].status !== value.status || (value.type && value.type !== this.invoiceService.invoiceList[0].type)){
          this.service.loading = true;
          this.invoiceService.getInvoices(value.status, value.type)
          .subscribe(
            result => {
              this.invoiceService.invoiceList = result
              .sort((a,b) => {
                if(a.date === undefined) return 1;
                else if(b.date === undefined) return -1;
                else return (new Date(a.date).getTime()> new Date(b.date).getTime()) ? -1 : a<b ? 1 : 0 ;
              });
              this.invoiceList = this.invoiceService.invoiceList;
              this.service.loading = false;
            }
          );
        } else {
          this.invoiceList = this.invoiceService.invoiceList
           .filter(f => this.invoiceFilter(f))
           .sort((a,b) => {
               if(a.date === undefined) return 1;
               else if(b.date === undefined) return -1;
               else return (new Date(a.date).getTime()> new Date(b.date).getTime()) ? -1 : a<b ? 1 : 0 ;
           });
        }
      }
    );
    if (this.invoiceService.filter) {
      this.invoiceService.setFilter(this.invoiceService.filter);
    }
  }

  invoiceFilter(f: Invoice) : boolean {
    let q = this.invoiceService.filter;
    let val = true;

    const document = f.type === InvoiceType.PURCHASE ? f.receiver.document : f.sender.document;
    const name = f.type === InvoiceType.PURCHASE ? f.receiver.name : f.sender.name;

    if(q.value) {
      const ref = q.value ? f.reference && f.reference.toUpperCase().includes(q.value.toUpperCase()) : true;
      const rd = q.value ? document && document.toUpperCase().includes(q.value.toUpperCase()) : true;
      const rn = q.value ? name && name.toUpperCase().includes(q.value.toUpperCase()) : true;
      val = ref || rd || rn;
    }
    const fromDate = q.fromDate ? f.date && new Date(f.date).getTime() > new Date(q.fromDate).getTime() : true;
    const toDate = q.toDate ? f.date && new Date(f.date).getTime() < new Date(q.toDate).getTime() : true;
    const reference = q.reference ? f.reference && f.reference.toUpperCase().includes(q.reference.toUpperCase()) : true;
    const type = q.type ? f.type === q.type : true;
    const verified = q.verified ? f.verified : true;
    const category = q.category ? f.category && f.category === q.category : true;
    const rdoc = q.rdocument ? document && document.toUpperCase().includes(q.rdocument.toUpperCase()) : true;
    const rname = q.rname ? name && name.toUpperCase().includes(q.rname.toUpperCase()) : true;
    return fromDate && toDate && reference && type && verified && category && rdoc && rname && val;
  }

  ngOnInit() {
    if (!this.invoiceService.filter) {
      const o = {
        status: 'inbox'
      };
      this.invoiceService.setFilter(o);
    }
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  onSelect(invoice: Invoice, index: number): void {
    this.invoiceService.setInvoice(invoice);
    this.invoiceService.invoiceIndex = index;
    this.invoiceService.isNew = false;
    this.invoiceService.expandMenu(false);
    RootLoader.angularPanel(this.router, this.location, '/invoice/sheet');
  }

  onScroll() {

  }

  getInvoiceIcon(invoice: Invoice): string {
    if (invoice.type === InvoiceType.PURCHASE) {
      return 'unarchive';
    } else if (invoice.type === InvoiceType.EXPENSES || invoice.type === InvoiceType.SALES) {
      return 'archive';
    } else { return this.getTicketIcon(invoice.category); }
  }

  getTicketIcon(category: string): string {
    if (category && category === '629.1') {
      return 'local_hotel';
    } else if (category && category === '629.2') {
      return 'local_parking';
         } else if (category && category === '629.3') {
      return 'ev_station';
         } else if (category && category === '629.4') {
      return 'commute';
         } else if (category && category === '629.5') {
      return 'local_dining';
         } else if (category && category === '629.6') {
      return 'settings_remote';
         } else { return 'receipt'; }
  }

  getRegistry(invoice: Invoice): string {
    if (invoice.type === InvoiceType.PURCHASE) {
      return (invoice.receiver && invoice.receiver.name)
        ? (invoice.receiver.name.length > 25
          ? invoice.receiver.name.substring(0, 24) + '...'
          : invoice.receiver.name)
        : 'ACREEDORES VARIOS';
    } else { return (invoice.sender && invoice.sender.name)
      ? (invoice.sender.name.length > 25
        ? invoice.sender.name.substring(0, 24) + '...'
        : invoice.sender.name)
      : 'PROVEEDORES VARIOS';
    }
  }

  getDate(invoice: Invoice): string {
    if (invoice.date) {
      const d = new Date(invoice.date);
      return d.getDate() + '/' + (d.getMonth() + 1) + '/' + d.getFullYear();
    } else { return ''; }
  }

  getTitle(value: any): string {
    if (value.category) {
      if (value.category === '700.0') {
        return 'VENTAS DE MERCADERÍAS';
      } else if (value.category === '705.0') {
        return 'PRESTACIÓN DE SERVICIOS';
      } else if (value.category === '600.0') {
        return 'COMPRAS DE MERCADERÍAS';
      } else if (value.category === '607.0') {
        return 'TRABAJOS REALIZADOS POR OTRAS EMPRESAS';
      } else if (value.category === '621.0') {
        return 'ARRENDAMIENTOS Y CÁNONES';
      } else if (value.category === '622.0') {
        return 'REPARACIONES Y CONSERVACIÓN';
      } else if (value.category === '623.0') {
        return 'SERVICIOS DE PROFESIONALES INDEPENDIENTES';
      } else if (value.category === '624.0') {
        return 'TRANSPORTES';
      } else if (value.category === '625.0') {
        return 'PRIMAS DE SEGUROS';
      } else if (value.category === '626.0') {
        return 'SERVICIOS BANCARIOS Y SIMILARES';
      } else if (value.category === '627.0') {
        return 'PUBLICIDAD PROPAGANDA Y RELACIONES PÚBLICAS';
      } else if (value.category === '628.0') {
        return 'SUMINISTROS';
      } else if (value.category === '629.0') {
        return 'OTROS GASTOS';
      } else if (value.category === '629.1') {
        return 'ALOJAMIENTO';
      } else if (value.category === '629.2') {
        return 'APARCAMIENTO';
      } else if (value.category === '629.3') {
        return 'COMBUSTIBLE';
      } else if (value.category === '629.4') {
        return 'DESPLAZAMIENTOS';
      } else if (value.category === '629.5') {
        return 'DIETAS';
      } else if (value.category === '629.6') {
        return 'PEAJE';
      } else if (value.category === '629.7') {
        return 'KILOMETRAJE';
      } else if (value.category === '629.8') {
        return 'MULTAS Y SANCIONES';
      } else if (value.category === '629.9') {
        return 'TASAS Y TRIBUTOS';
      }
    } else if (value.status === 'accepted') {
      return value.type + 'S';
    } else if (value.status === 'verified') {
      return 'VERIFICADAS';
    } else if (value.status === 'trash') {
      return 'PAPELERA';
    } else if (value.status === 'refused') {
      return 'RECHAZADAS';
    } else if (value.status === 'inbox') {
      return 'INBOX';
    }
  }

  paymethod(invoice: Invoice ): string {
    if (invoice.finances && invoice.finances.length > 0) {
      if (invoice.finances[0].pay_method === PayMethod.CASH_BASIS) {
        return 'Efectivo';
      } else if (invoice.finances[0].pay_method === PayMethod.CREDIT_CARD) {
        return 'Tarjeta Crédito';
      } else if (invoice.finances[0].pay_method === PayMethod.DEBIT_CARD) {
        return 'Tarjeta Débito';
      } else if (invoice.finances[0].pay_method === PayMethod.BANK_TRANSFER) {
        return 'Transferencia';
      } else if (invoice.finances[0].pay_method === PayMethod.NEGOTIABLE_DOCUMENT) {
        return 'Giro';
      } else if (invoice.finances[0].pay_method === PayMethod.CHEQUE) {
        return 'Cheque';
      } else if (invoice.finances[0].pay_method === PayMethod.OTHER) {
        return 'Otros';
      } else {
         return '';
      }
    } else {
      return '';
    }
  }

}
