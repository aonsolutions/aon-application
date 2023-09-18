import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { ChartType } from 'chart.js';
import { TranslateService } from '@ngx-translate/core';
import { ReportingService } from 'src/app/core/services/reporting.service';
import { MenuItem } from 'src/app/core/models/interface/menu-item';
import { DropdownMenuComponent } from 'src/app/shared/components/dropdown-menu/dropdown-menu.component';
import { Router } from '@angular/router';
import { BankService } from 'src/app/core/services/bank.service';

@Component({
  selector: 'app-chart-dashboard',
  templateUrl: './chart-dashboard.component.html',
  styleUrls: ['./chart-dashboard.component.scss'],
})
export class ChartDashboardComponent implements OnInit {
  @ViewChild('chart') dropdownMenuComponent: DropdownMenuComponent =
    new DropdownMenuComponent();
  @Input() name: string = '';
  @Input() shape: string = '';
  @Input() typeDate: number = 0;
  @Input() direction: string = '';

  // Parametros que enviamos al Chart
  chartType: ChartType = 'doughnut';
  chartData: any = [];

  // Parametros para la opcion de filtrado
  selected: string = '';
  subtitle: string = '';
  money: string = '';
  menuItem: MenuItem[] = [];

  constructor(
    private translateService: TranslateService,
    private reportingService: ReportingService,
    private router: Router,
    private bankService: BankService
  ) {
    this.translateService
      .get([
        'HOME.LAST_12_MONTHS',
        'HOME.LAST_6_MONTHS',
        'HOME.QUARTERLY',
        'BILLING.SUBTITLE',
      ])
      .subscribe((result) => {
        // El valor cargado de serie
        this.selected = result['HOME.LAST_12_MONTHS'];
        // Opciones de filtrado
        this.menuItem! = [
          {
            root: true,
            text: result['HOME.LAST_12_MONTHS'],
            click: () => this.filter(12, result['HOME.LAST_12_MONTHS'], event!),
          },
          {
            root: true,
            text: result['HOME.LAST_6_MONTHS'],
            click: () => this.filter(6, result['HOME.LAST_6_MONTHS'], event!),
          },
          {
            root: true,
            text: result['HOME.QUARTERLY'],
            click: () => this.filter(13, result['HOME.QUARTERLY'], event!),
          },
        ];

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
    this.router.navigate([`/billing`]);
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
        this.chartType = 'doughnut';
        this.reportingService.getVentasGastos().then((response) => {

          this.chartData = response.datasets[0].data;

          let total = 0;
          this.chartData.forEach((element: any) => {
            total += element;
          });

          this.money = total.toString();
        });
        break;
      case 2:
        this.chartType = 'doughnut';
        this.reportingService.getCobrosPagos().then((response) => {
          this.chartData = response.datasets[1].data;

          let total = 0;
          this.chartData.forEach((element: any) => {
            total += element;
          });
          this.money = total.toString();
        });
        break;
      case 3:
        this.chartType = 'pie';
        this.reportingService.getVentasGastos().then((response) => {
          this.chartData = response.datasets.map(
            (dataset: any) => dataset.data
          );
        });
        this.money = '2000';
        break;
    }
  }
}
