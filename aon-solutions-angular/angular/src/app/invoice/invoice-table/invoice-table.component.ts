import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SelectionModel } from '@angular/cdk/collections';
import { Router } from '@angular/router';
import { Location } from '@angular/common';
import { InvoiceService } from '../invoice.service';
import { SharedService, AonService } from '../../services/services';
import { Invoice, AonMaker, InvoiceType, PayMethod } from '../../models/models';
import { MatDialog, MatTable } from '@angular/material';
import { Subscription } from 'rxjs';
import { RootLoader } from '../../utils/loader';

@Component({
  selector: 'app-invoice-table',
  templateUrl: './invoice-table.component.html',
  styleUrls: ['./invoice-table.component.css'],
})
export class InvoiceTableComponent implements OnInit, OnDestroy {
  displayedColumns: string[] = ['select', 'date', 'reference', 'contraparte', 'amount', 'paymethod', 'status'];
  selection = new SelectionModel<any>(true, []);

  emitidas: boolean;

  subscription: Subscription;
  status: string;

  invoiceList: Invoice[];

  @ViewChild(MatTable, {static: false}) public matTable: MatTable<any>;
  constructor(public httpClient: HttpClient,  private router: Router,
    public dialog: MatDialog, public aonService: AonService,
    public service: SharedService, public invoiceService: InvoiceService,
    private location: Location) {
      this.subscription = this.invoiceService.filterObservable
      .subscribe(
         value => {
           this.invoiceList = [];
           value.company = this.service.getActualCompany();
           if(!this.invoiceService.invoiceList || this.invoiceService.invoiceList.length === 0 || this.invoiceService.invoiceList[0].domain !== this.service.getActualCompany() ||
                this.invoiceService.invoiceList[0].status !== value.status || (value.type && value.type !== this.invoiceService.invoiceList[0].type)){
              this.service.loading = true;

              this.invoiceService.getInvoices(value.status, value.type)
              .subscribe(
                (result: Invoice[]) => {
                  this.emitidas = value.type === 'EMITIDA';
                  this.invoiceService.invoiceList = result
                  .map((i: Invoice) => AonMaker.createInvoice(i))
                  .sort((a,b) => {
                      if(a.date === undefined) return 1;
                      else if(b.date === undefined) return -1;
                      else return (new Date(a.date).getTime()> new Date(b.date).getTime()) ? -1 : a<b ? 1 : 0 ;
                  });

                  this.invoiceList = this.invoiceService.invoiceList;
                  this.selection = new SelectionModel<any>(true, []);
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
              this.selection = new SelectionModel<any>(true, []);
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
    const pending = q.pending ? !f.verified : true;
    const category = q.category ? f.category && f.category === q.category : true;
    const rdoc = q.rdocument ? document && document.toUpperCase().includes(q.rdocument.toUpperCase()) : true;
    const rname = q.rname ? name && name.toUpperCase().includes(q.rname.toUpperCase()) : true;
    return fromDate && toDate && reference && type && verified && pending && category && rdoc && rname && val;
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
    this.service.isMultipleSelection.next(false);
    this.subscription.unsubscribe();
  }

  public viewInvoice(invoice: Invoice, index: number): void {
    this.invoiceService.invoiceIndex = index;
    this.invoiceService.isNew = false;
    this.invoiceService.expandMenu(false);
    this.invoiceService.setInvoice(invoice);
    RootLoader.angularPanel(this.router, this.location, '/invoice/sheet');
  }

  public onScroll() {

  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.invoiceService.invoiceList ? this.invoiceService.invoiceList.length : 0;
    return numRows !== 0 && numSelected === numRows;
  }

  isEmitida(type: InvoiceType) {
    return type === InvoiceType.SALES;
  }

  toggle(element) {
    this.selection.toggle(element);
    this.service.isMultipleSelection.next(this.selection.selected.length > 0);
    const array = [];
    for (let i = 0; i < this.selection.selected.length; i++) {
      array.push(this.selection.selected[i]);
    }
    this.invoiceService.invoiceSelection.next(array);
  }

  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.invoiceService.invoiceList.forEach(row => this.selection.select(row));
    if (this.isAllSelected()) { this.service.isMultipleSelection.next(true); } else { this.service.isMultipleSelection.next(false); }
    const array = [];
    for (let i = 0; i < this.selection.selected.length; i++) {
      array.push(this.selection.selected[i]);
    }
    this.invoiceService.invoiceSelection.next(array);
  }

  formatDate(date: string) {
    if (date) {
      const d = new Date(date);
      return d.getDate() + '/' + (d.getMonth() + 1) + '/' + d.getFullYear();
    } else { return ''; }
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

  getSourceText(source: string): string {
    if (source === 'web') {
      return 'Aplicación Web';
    } else if (source === 'mobile') {
      return 'Dispositivo Móvil';
    } else if (source === 'mail') {
      return 'Correo Electrónico';
    } else {
      return source;
    }
  }

  getComments(comments) : string {
    let com = '';
    for(let i = 0; i < comments.length; i++){
      com = com + ' ' + comments[i].comment;
    }
    return com;
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

}
