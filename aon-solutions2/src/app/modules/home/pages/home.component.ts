import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component, OnInit, ViewChild } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Bank } from 'src/app/core/models/class/bank';
import { TaxModel } from 'src/app/core/models/class/tax-model';
import { BankService } from 'src/app/core/services/bank.service';
import { TaxModelService } from 'src/app/core/services/tax-model.service';

export interface ShortcutDashboard {
  shape: string;
  name: string;
}

interface ChartItem {
  shape: string;
  name: string;
  chart: string;
  chartLabels: string[];
  chartData: number[];
  chartType: string;
  colors: string[];
}

export interface MenuItems {
  shape: string;
  name: string;
  color: string;
}

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {
  //banks area
  banks: Bank[] = [];
  private banksSubject = new BehaviorSubject<any[]>([]);
  public banks$ = this.banksSubject.asObservable();

  //modelTax area
  models: TaxModel[] = [];
  private modelsSubject = new BehaviorSubject<any[]>([]);
  public models$ = this.modelsSubject.asObservable();

  shortcuts: ShortcutDashboard[] = [
    { shape: 'add_box', name: 'CREAR FACTURA' },
    { shape: 'person_add', name: 'DAR DE ALTA EMPLEADO' },
    { shape: 'add_comment', name: 'CREAR CONSULTA' },
    { shape: 'alarm', name: 'MARCAJE' },
  ];

  chartItems: ChartItem[] = [
    {
      shape: 'show_chart',
      name: 'Ventas/Gastos',
      chart: '',
      chartLabels: [
        'ene-22',
        'feb-22',
        'mar-22',
        'abr-22',
        'may-22',
        'jun-22',
        'jul-22',
        'ago-22',
        'sep-22',
        'oct-22',
        'nov-22',
        'dic-22',
      ],
      chartData: [1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000],
      chartType: 'line',
      colors: [''],
    },
    {
      shape: 'bar_chart',
      name: 'Cobros/Pagos',
      chart: '',
      chartLabels: [
        'ene-22',
        'feb-22',
        'mar-22',
        'abr-22',
        'may-22',
        'jun-22',
        'jul-22',
        'ago-22',
        'sep-22',
        'oct-22',
        'nov-22',
        'dic-22',
      ],
      chartData: [1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000],
      chartType: 'bar',
      colors: [''],
    },
  ];

  menuItems: MenuItems[] = [
    { shape: 'assessment', name: 'Gestión', color: '#4f91ff' },
    { shape: 'euro_symbol', name: 'Panel de Impuestos', color: '#fb982e' },
    { shape: 'people', name: 'Panel de empleados', color: '#33a9a9' },
    { shape: 'description', name: 'Documentación', color: '#ef6292' },
  ];

  constructor(
    public bankService: BankService,
    public taxModelService: TaxModelService
  ) {}

  ngOnInit(): void {
    //bankService
      this.bankService.getBankList().then((response) => {
      this.banks = response;
      this.banksSubject.next(this.banks);
    });
    //taxmodelService
      this.taxModelService.getTaxModelList().then((response) => {
      this.models = response;
      this.modelsSubject.next(this.models);
    });

  }
}



