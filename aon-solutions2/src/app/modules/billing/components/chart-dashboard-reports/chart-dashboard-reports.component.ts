import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { ChartType } from 'chart.js';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';

@Component({
  selector: 'app-chart-dashboard-reports',
  templateUrl: './chart-dashboard-reports.component.html',
  styleUrls: ['./chart-dashboard-reports.component.scss'],
})
export class ChartDashboardReportsComponent implements OnInit {
  @ViewChild('chart') dropdownMenuComponent: DropdownMenuComponent =
    new DropdownMenuComponent();
  @Input() name: string = '';
  @Input() shape: string = '';
  @Input() typeDate: number = 0;

  // Parametros que enviamos al Chart
  chartType: ChartType = 'doughnut';
  chartData: any = [];
  chartColors: any = [];
  chartLabels: any = [];

  // Parametros para la opcion de filtrado
  selected: string = '';
  subtitle: string = '';
  menuItem: MenuItem[] = [];
  direction: string = '';

  constructor(
    private translateService: TranslateService,
    private reportingService: ReportingService,
    private router: Router
  ) {
    this.translateService
      .get(['HOME.LAST_12_MONTHS', 'BILLING.SUBTITLE'])
      .subscribe((result) => {
        // El valor cargado de serie
        this.selected = result['HOME.LAST_12_MONTHS'];
        // Opciones de filtrado

        this.subtitle = result['BILLING.SUBTITLE'];

        this.direction = '';
      });
  }

  ngOnInit(): void {
    this.filterReporting(12);
  }

  filter(number: number, name: string = '', event: Event) {
    event.stopPropagation();
    this.filterReporting(number, name);
  }

  redirect(direction: string) {
    this.router.navigate([direction]);
  }

  // Filtrar por
  //  ? Ventas/Gastos
  //  ? Cobros/Pagos
  // monthFilter = mes - Los meses que se permiten de filtrado
  //  ? monthFilter = 13 filtramos por trimestre

  filterReporting(monthFilter: number, name: string = '') {
    // El tipo de filtrado
    this.selected = name === '' ? this.selected : name;
    // Los datos
    switch (this.typeDate) {
      // Ventas/Gastos
      case 1:
        this.chartType = 'bar';
        this.reportingService.getVentasGastos().then((response) => {
          this.chartData = response.datasets.map(
            (dataset: any) => dataset.data
          );
          this.chartLabels = response.label;
        });
        break;
    }
  }
}
