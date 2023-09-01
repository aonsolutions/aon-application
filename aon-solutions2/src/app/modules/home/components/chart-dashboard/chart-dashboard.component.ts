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
  @Input() name     : string = '';
  @Input() shape    : string = '';
  @Input() typeDate : number = 0;
  // Parametros que enviamos al Chart
  chartType   : ChartType = 'line';
  chartLabels : any = [];
  chartData   : any = [];
  chartColors : any = [];
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
        {root: true, text: result['HOME.LAST_12_MONTHS'], click:() => this.filterReporting(12, result['HOME.LAST_12_MONTHS'])},
        {root: true, text: result['HOME.LAST_6_MONTHS'] , click:() => this.filterReporting(6,  result["HOME.LAST_6_MONTHS"])},
        {root: true, text: result['HOME.QUARTERLY']     , click:() => this.filterReporting(13, result["HOME.QUARTERLY"])},
      ];
    });
  }
   
  ngOnInit(): void {
    this.filterReporting(12);
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
        this.reportingService.getVentasGastos().then((response) => {
          this.chartData = response.datasets.map(
            (dataset: any) => dataset.data
          );
          this.chartLabels = response.label;
        });
      break
    // Cobros/Pagos
      case 2:
        this.chartType = 'bar';
        this.getToggleIcon();
        this.reportingService.getCobrosPagos().then((response) => {
          this.chartData = response.datasets.map(
            (dataset: any) => dataset.data
          );
          this.chartLabels = response.label;
        });
      break
    }
  }

}
