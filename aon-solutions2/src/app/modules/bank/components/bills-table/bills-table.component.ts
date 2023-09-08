import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-bills-table',
  templateUrl: './bills-table.component.html',
  styleUrls: ['./bills-table.component.scss'],
})
export class BillsTableComponent implements OnInit {
  displayedColumns: string[] = [
    'check',
    'date',
    'number',
    'contact',
    'baseImp',
    'imp',
    'recEq',
    'reten',
    'total',
    'status',
  ];

  headerTable: any = {};

  dataBody: any[] = [
    {
      date: '30/05/2023',
      number: '0001',
      contact: 'Juan Pérez',
      baseImp: '1.000,00',
      imp: '1.000,00',
      recEq: '1.000,00',
      reten: '21',
      total: '1.000,00',
      status: true,
    },
    {
      date: '30/05/2023',
      number: '0002',
      contact: 'Juan Pérez',
      baseImp: '1.000,00',
      imp: '1.000,00',
      recEq: '1.000,00',
      reten: '21',
      total: '1.000,00',
      status: false,
    },
    {
      date: '30/05/2023',
      number: '0003',
      contact: 'Juan Pérez',
      baseImp: '1.000,00',
      imp: '1.000,00',
      recEq: '1.000,00',
      reten: '21',
      total: '1.000,00',
      status: true,
    },
  ];

  bodyTable: any[] = [];

  constructor(private translateService: TranslateService) {
    let tableRow: any[] = [];
    let column: any = {};

    this.translateService.get([
      'BILLING.DATE',
      'BILLING.NUMBER',
      'BILLING.CONTACT',
      'BILLING.BASE_IMP',
      'BILLING.IMP',
      'BILLING.REC_EQ',
      'BILLING.RETEN',
      'BILLING.ACCOUNTED'
    ]).subscribe((result) => {

    this.headerTable = {
      check: '',
      date: result['BILLING.DATE'],
      number: result['BILLING.NUMBER'],
      contact: result['BILLING.CONTACT'],
      baseImp: result['BILLING.BASE_IMP'],
      imp: result['BILLING.IMP'],
      recEq: result['BILLING.REC_EQ'],
      reten: result['BILLING.RETEN'],
      total: 'Total',
      status: result['BILLING.ACCOUNTED']
    };
  });

    this.dataBody.forEach((element: any) => {
      column = Object.assign({}, element);

      column.check = '<span>Checkbox</span>';
      column.date = element.date;
      column.number = element.number;
      column.contact = element.contact;
      column.baseImp = element.baseImp + ' &euro;';
      column.imp = element.imp + ' &euro;';
      column.recEq = element.recEq + ' &euro;';
      column.reten = element.reten + '%';
      column.total = element.total + ' &euro;';
      column.status =
        element.status === true ? { icon: [{ check_circle: 'griss' }] } : '';

      tableRow.push(column);
    });

    this.bodyTable = tableRow;
  }

  ngOnInit(): void {}
}
