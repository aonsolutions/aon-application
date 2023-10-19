import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { ChartType } from 'chart.js';
import { TranslateService } from '@ngx-translate/core';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';

@Component({
  selector    : 'app-chart-dashboard',
  templateUrl : './chart-dashboard.component.html',
  styleUrls   : ['./chart-dashboard.component.scss']
})

export class ChartDashboardComponent implements OnInit {
  @ViewChild('chart') dropdownMenuComponent: DropdownMenuComponent = new DropdownMenuComponent;
  @Input() name     : string  = '';
  @Input() shape    : string  = '';
  @Input() typeDate : number  = 0;
  spinner           : boolean = true;
  // Parametros que enviamos al Chart
  chartType     : ChartType = 'line';
  chartDataLabel: any = [];
  chartData     : any = [];
  chartLabels   : any = [];
  chartColors   : any = [];
  // Parametros para la opcion de filtrado
  selected: string      = '';
  menuItem: MenuItem [] = []

  constructor(
    private translateService: TranslateService,
    private reportingService: ReportingService
  ) {
    this.translateService.get([
      'HOME.LAST_12_MONTHS', 'HOME.LAST_6_MONTHS', 'HOME.QUARTERLY'
    ]).subscribe((result) => {
      // El valor cargado de serie
      this.selected = result['HOME.LAST_12_MONTHS'];
      // Opciones de filtrado
      this.menuItem! = [
        {root: true, text: result['HOME.LAST_12_MONTHS'], click:() => this.filterReporting(-12, result['HOME.LAST_12_MONTHS'])},
        {root: true, text: result['HOME.LAST_6_MONTHS'] , click:() => this.filterReporting(-6,  result["HOME.LAST_6_MONTHS"])},
        {root: true, text: result['HOME.QUARTERLY']     , click:() => this.filterReporting(-3, result["HOME.QUARTERLY"])},
      ];
    });
  }
   
  ngOnInit(): void {
    this.filterReporting(0);
  }

  // Cambiar el estilo del char
  toggleChartType() {
    this.chartType = this.chartType === 'line' ? 'bar' : 'line';
  }
  // Cambiar el estilo del icono
  getToggleIcon(): string {
    return this.chartType === 'line' ? 'bar_chart' : 'show_chart';
  }

  // Filtrar por
  //  ? Ventas/Gastos
  //  ? Cobros/Pagos
  // monthFilter = mes - Los meses que se permiten de filtrado
  //  ? monthFilter = 13 filtramos por trimestre
  filterReporting(monthFilter : number, name: string = ''){
    // El tipo de filtrado
    this.selected = name === '' ? this.selected : name;
    // Los datos
    switch(this.typeDate){
    // Ventas/Gastos
      case 1:
        this.chartType  = 'line';
        this.getToggleIcon();
        let month1 = monthFilter != 0 ? monthFilter : -12;
        this.reportingService.getVentasGastos(new Date(new Date().setMonth(new Date().getMonth()+month1))).then((response) => {
          this.chartData = response.datasets.map(
            (dataset: any) => dataset.data
          );
          this.translateService.get([
            'BILLING.SALES', 'BILLING.BILLS'
          ]).subscribe((result) => {
            this.chartDataLabel = response.datasets.map(
              (dataset: any) => {
                return {label: result['BILLING.' + dataset.label]};
              }
            )
          });
          this.chartLabels = response.label;
          this.spinner = false;
        });
      break
    // Cobros/Pagos
      case 2:
        this.chartType = 'bar';
        this.getToggleIcon();
        let month2 = monthFilter != 0 ? monthFilter : 12;
        this.reportingService.getCobrosPagos(new Date(), new Date(new Date().setMonth(new Date().getMonth()+month2))).then((response) => {
          this.chartData = response.datasets.map(
            (dataset: any) => dataset.data
          );
          this.translateService.get([
            'BILLING.COLLECTIONS', 'BILLING.PAYMENTS'
          ]).subscribe((result) => {
            this.chartDataLabel = response.datasets.map(
              (dataset: any) => {
                return {label: result['BILLING.' + dataset.label]};
              }
            )
          });
          this.chartLabels = response.label;
          this.spinner = false;
        });
        this.translateService.get([
          'HOME.NEXT_12_MONTHS', 'HOME.NEXT_6_MONTHS','HOME.NEXT_3_MONTHS'
        ]).subscribe((result) => {
          this.selected = name === '' ? result['HOME.NEXT_12_MONTHS'] : name;
          this.menuItem = [
            {root: true, text: result['HOME.NEXT_12_MONTHS'], click:() => this.filterReporting(12, result['HOME.NEXT_12_MONTHS'])},
            {root: true, text: result['HOME.NEXT_6_MONTHS'],  click:() => this.filterReporting(6,  result["HOME.NEXT_6_MONTHS"])},
            {root: true, text: result['HOME.NEXT_3_MONTHS'],  click:() => this.filterReporting(3,  result["HOME.NEXT_3_MONTHS"])},
          ];
        });
      break
    }
  }

}
