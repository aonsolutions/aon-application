import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ReportingService } from 'src/app/core/services/reporting.service';

@Component({
  selector: 'app-bank',
  templateUrl: './bank.component.html',
  styleUrls: ['./bank.component.scss'],
})
export class BankComponent implements OnInit {
  selectedTab: number = 1;
  selectedMenu: number = 1;
  chartItems: any[] = [];
  buttonsVentas: any[] = [];
  buttonsGastos: any[] = [];
  banks: any[] = [];
  sales: any[] = [];
  addBank: boolean = true;
  editBank: { [key: number]: boolean } = {};

  constructor(private reportingService: ReportingService) {
    // Sacamos el parametro de la url
    const url = window.location.href;
    const urlSplit = url.split('=');

    if (urlSplit[1] === undefined) {
      this.selectedTab = 1;
    } else if (parseInt(urlSplit[1]) === 4) {
      this.selectedTab = 4;
      this.selectedMenu = 2;
    } else {
      this.selectedTab = parseInt(urlSplit[1]);
    }

    this.selectedTab = parseInt(urlSplit[1]);

    this.chartItems = [
      {
        name: 'Ventas',
        typeDate: 1,
      },
    ];

    this.buttonsVentas = [
      {
        shape: 'excel',
      },
      {
        shape: 'send',
      },
      {
        shape: 'file_copy',
      },
      {
        shape: 'cloud_download',
      },
      {
        shape: 'delete',
      },
    ];

    this.buttonsGastos = [
      {
        shape: 'picture_as_pdf',
      },
      {
        shape: 'excel',
      },
    ];

    this.banks = [
      {
        id: 1,
        name: 'Caixabank',
        balance: '5.487,55',
        iban: 'ES12 3456 7891 2345 6789',
        date: '30/05/2023',
        swift: 'CAIXESBBXXX',
        sync: 'sync',
      },
      {
        id: 2,
        name: 'Santander',
        balance: '8.887,02',
        iban: 'ES12 3456 7891 2345 6789',
        date: '12/01/2023',
        swift: 'BSCHESMMXXX',
        sync: 'syncProblem',
      },
      {
        id: 3,
        name: 'Cajamar',
        balance: '1.125,54',
        iban: 'ES12 3456 7891 2345 6789',
        date: '30/05/2023',
        swift: 'CCRIES2AXXX',
        sync: 'syncLost',
      },
    ];
  }

  editar(idBank: number) {
    this.editBank[idBank] = true;
  }

  guardar(idBank: number) {
    this.editBank[idBank] = false;
  }

  ngOnInit(): void {}
}
