import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-bank-table',
  templateUrl: './bank-table.component.html',
  styleUrls: ['./bank-table.component.scss']
})
export class BankTableComponent implements OnInit {

  displayedColumns: string[] = [
    'date',
    'description',
    'status',
    'amount',
    'balance',
  ];

  headerTable: any = {};

  dataBody: any[] = [
    {
      date: '30/05/2023',
      description: 'Pago de factura',
      status: 'Aprobado',
      amount: '1.000,00',
      balance: '1.000,00',
    },
    {
      date: '30/05/2023',
      description: 'Pago de factura',
      status: 'Pendiente',
      amount: '0.000,00',
      balance: '0.000,00',
    },
    {
      date: '30/05/2023',
      description: 'Pago de factura',
      status: 'Pendiente',
      amount: '1.000,00',
      balance: '1.000,00',
    }
  ]

  bodyTable: any[] = [];

  constructor(private translateService: TranslateService) {
    let tableRow: any[] = [];
    let column: any = {};

    this.translateService.get([
      'BILLING.DATE',
      'BILLING.DESCRIPTION',
      'BILLING.STATUS',
      'BILLING.AMOUNT',
      'BILLING.BALANCE',
    ]).subscribe((result) => {

    this.headerTable = {
      date: result['BILLING.DATE'],
      description: result['BILLING.DESCRIPTION'],
      status: result['BILLING.STATUS'],
      amount: result['BILLING.AMOUNT'],
      balance: result['BILLING.BALANCE'],
    };
  });

    this.dataBody.forEach((element: any) => {
      column = Object.assign({}, element);

      column.date = element.date;
      column.description = element.description;
      column.status = element.status;
      column.amount = element.amount + ' &euro;';
      column.balance = element.balance + ' &euro;'

      tableRow.push(column);
    });

    this.bodyTable = tableRow;
  }

  ngOnInit(): void {
  }

}
