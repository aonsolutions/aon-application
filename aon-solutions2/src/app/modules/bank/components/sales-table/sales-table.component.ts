import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-sales-table',
  templateUrl: './sales-table.component.html',
  styleUrls: ['./sales-table.component.scss']
})

export class SalesTableComponent implements OnInit {

  displayedColumns: string[] = ['check', 'date', 'number', 'contact', 'total', 'status'];

  headerTable: any = {};

  dataBody: any[] = [
    {
      date: '30/05/2023',
      number: '0001',
      contact: 'Juan Pérez',
      total: '1.000,00',
      status: true,
    },
    {
      date: '30/05/2023',
      number: '0002',
      contact: 'Juan Pérez',
      total: '1.000,00',
      status: false,
    },
    {
      date: '30/05/2023',
      number: '0003',
      contact: 'Juan Pérez',
      total: '1.000,00',
      status: true,
    },
  ];

  bodyTable: any[] = [];

  constructor(private translateService: TranslateService) {

    this.translateService.get([
      'BILLING.DATE',
      'BILLING.NUMBER',
      'BILLING.CONTACT',
      'BILLING.ACCOUNTED',
    ]).subscribe((result: any) => {

      this.headerTable = {
        check: '',
        date: result['BILLING.DATE'],
        number: result['BILLING.NUMBER'],
        contact: result['BILLING.CONTACT'],
        total: 'Total',
        status: result['BILLING.ACCOUNTED'],
      };

    });

    let tableRow: any[] = [];
    let column: any = {};

    this.dataBody.forEach((element: any) => {
      column = Object.assign({}, element);

      column.check   = "<span>Checkbox</span>"
      column.date    = element.date;
      column.number  = element.number;
      column.contact = element.contact;
      column.total   = element.total + ' &euro;';
      column.status  =
        element.status === true ? { icon: [{ check_circle: 'griss' }] } : '';

      tableRow.push(column);

    });

    this.bodyTable = tableRow;
  }

  ngOnInit(): void {
  }

}
