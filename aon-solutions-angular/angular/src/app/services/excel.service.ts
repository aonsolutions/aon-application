import { Injectable } from '@angular/core';
import * as XLSX from 'xlsx';
const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
import { Invoice, InvoiceType, PayMethod } from '../models/models';
import * as moment from 'moment';

@Injectable()
export class ExcelService {
  constructor() { }

  public exportAsExcelFile(json: Invoice[]): void {
    const worksheet1: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json.filter(f => InvoiceType.UNDEDUCTIBLE === f.type).map(r => this.parseInvoice(r)));
    const worksheet2: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json.filter(f => InvoiceType.SALES === f.type || InvoiceType.EXPENSES === f.type).map(r => this.parseInvoice(r)));
    const worksheet3: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json.filter(f => InvoiceType.PURCHASE === f.type).map(r => this.parseInvoice(r)));

    //const cell = XLSX.utils.encode_cell({c:0,r:0});

    this.setStyle(worksheet1, json);
    this.setStyle(worksheet2, json);
    this.setStyle(worksheet3, json);


    const workbook: XLSX.WorkBook = {
      Sheets: {
         'Tickets': worksheet1,
         'Facturas Recibidas': worksheet2,
         'Facturas Emitidas': worksheet3
      },
      SheetNames: ['Tickets', 'Facturas Recibidas', 'Facturas Emitidas']};
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
     this.saveAsExcelFile(excelBuffer);
  }

  private parseInvoice(invoice: Invoice){
    const isEmitida =  InvoiceType.PURCHASE === invoice.type;
    let o = new Object();
    o['FECHA'] = moment(invoice.date).format("DD/MM/YYYY");
    o['CONCEPTO'] = this.getCategoryName(invoice.category);
    o['IMPORTE'] = invoice.total;
    if(invoice.finances && invoice.finances.length > 0){
      o['FORMA DE PAGO'] = this.getPayMethodName(invoice.finances[0].pay_method);
    }
    if(isEmitida) {
      o['SERIE'] = invoice.series;
      o['NUMERO'] = invoice.number;
    } else {
      o['NUMERO FACTURA'] = invoice.reference;
    }
    o['NIF'] = isEmitida ? invoice.receiver.document : invoice.sender.document;
    o['TITULAR'] = isEmitida ? invoice.receiver.name : invoice.sender.name;
    o['DIRECCION'] = isEmitida
      ? (invoice.receiver && invoice.receiver.address ? invoice.receiver.address.address : '')
      : (invoice.sender && invoice.sender.address ? invoice.sender.address.address : '');
    o['CIUDAD'] = isEmitida
      ? (invoice.receiver && invoice.receiver.address ? invoice.receiver.address.city : '')
      : (invoice.sender && invoice.sender.address ? invoice.sender.address.city : '');
    o['CPOSTAL'] = isEmitida
      ? (invoice.receiver && invoice.receiver.address ? invoice.receiver.address.postal_code : '')
      : (invoice.sender && invoice.sender.address ? invoice.sender.address.postal_code : '');
    o['PROVINCIA'] = isEmitida
      ? (invoice.receiver && invoice.receiver.address ? invoice.receiver.address.province : '')
      : (invoice.sender && invoice.sender.address ? invoice.sender.address.province : '');
    o['PAIS'] = isEmitida
      ? (invoice.receiver && invoice.receiver.address ? invoice.receiver.address.country : '')
      : (invoice.sender && invoice.sender.address ? invoice.sender.address.country : '');

    if(invoice.taxes) {
      for(let i = 0; i < invoice.taxes.length; i++){
        o['TIPO IMPUESTO' +  (i > 0 ? i : '')] = invoice.taxes[i].tax;
        o['BASE IMPONIBLE' + (i > 0 ? i : '')] = invoice.taxes[i].base;
        o['PORCENTAJE' + (i > 0 ? i : '')] = invoice.taxes[i].percentage;
        o['CUOTA' + (i > 0 ? i : '')] = invoice.taxes[i].quota;
      }
    }

    return o;
  }

  private saveAsExcelFile(buffer: any): void {
    const data: Blob = new Blob([buffer], {type: EXCEL_TYPE});
    const url = window.URL.createObjectURL(data);
    this.download(url, 'invoices.xlsx');
  }

  private download(url: string, filename: string): void {
    const a = document.createElement('a');
    document.body.appendChild(a);
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }

  setStyle(worksheet: XLSX.WorkSheet, json: any[]) : void {
    const wscols = [
        {wch:10}, // FECHA
        {wch:20}, // CONCEPTO
        {wch:10}, // IMPORTE
        {wch:20}, // FORMA DE PAGO
        {wch:20}, // NUMERO FACTURA
        {wch:10}, // NIF
        {wch:35}, // TITULAR
        {wch:30}, // DIRECCIÓN
        {wch:20}, // CIUDAD
        {wch:10}, // CODIGO POSTAL
        {wch:20}, // PROVINCIA
        {wch:10}, // PAÍS

        {wch:15},
        {wch:15},
        {wch:15},
        {wch:15},

        {wch:15},
        {wch:15},
        {wch:15},
        {wch:15},

        {wch:15},
        {wch:15},
        {wch:15},
        {wch:15},
    ];



    worksheet['!cols'] = wscols;


    let wsrows =  [
      {hpx: 20}
    ];

    for(let i = 0; i < json.length; i++) {
      wsrows.push({hpx: 16});
    }

    worksheet['!rows'] = wsrows; //ws - worksheet
  }

  getPayMethodName(paymethod: PayMethod) : string {
    switch(paymethod) {
      case PayMethod.CASH_BASIS: return 'Efectivo';
      case PayMethod.DEBIT_CARD: return 'Tarjeta Débito';
      case PayMethod.CREDIT_CARD: return 'Tarjeta Crédito';
      case PayMethod.BANK_TRANSFER: return 'Transferencia';
      case PayMethod.CHEQUE : return 'Cheque';
      case PayMethod.NEGOTIABLE_DOCUMENT : return 'Giro Bancario';
      default : return 'Otros';
    }
  }

  getCategoryName(category: string) : string {
    switch(category) {
      case '700.0' : return 'Ventas de mercaderías';
      case '705.0' : return 'Prestación de servicios';
      case '600.0' : return 'Compras de mercaderías';
      case '607.0' : return 'Trabajos realizados por otras empresas';
      case '621.0' : return 'Arrendamiento y cánones';
      case '622.0' : return 'Reparaciones y convervación';
      case '623.0' : return 'Servicios de profesionales independientes';
      case '624.0' : return 'Transportes';
      case '625.0' : return 'Primas de seguros';
      case '626.0' : return 'Servicios bancarios y similares';
      case '627.0' : return 'Publicidad, propaganda y relaciones públicas';
      case '628.0' : return 'Suministros';
      case '629.0' : return 'Otros gastos';
      case '629.1' : return 'Alojamiento';
      case '629.2' : return 'Aparcamiento';
      case '629.3' : return 'Combustible';
      case '629.4' : return 'Desplazamientos';
      case '629.5' : return 'Dietas';
      case '629.6' : return 'Peaje';
      case '629.7' : return 'Kilometraje';
      case '629.8' : return 'Multas y sanciones';
      case '629.9' : return 'Tasas y tributos';
      default : return '';
    }
  }

}
