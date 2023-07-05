import { Component, OnInit, Input } from '@angular/core';
import { ReportingService } from '../../../../core/services/reporting.service';

@Component({
  selector: 'app-chart-dashboard',
  templateUrl: './chart-dashboard.component.html',
  styleUrls: ['./chart-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class ChartDashboardComponent implements OnInit {
  @Input() name: string = '';
  @Input() chart: string = '';
  @Input() shape: string = '';
  @Input() labels: any = [];
  @Input() data: any = [];
  @Input() colors: any = [];
  @Input() chartType: any = '';
  @Input() chartData: string[] = [];

  selected: string = 'Últimos 12 meses';

  items: string[] = ['Últimos 12 meses', 'Últimos 6 meses', 'Trimestral'];

  constructor(public reportingService: ReportingService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.reportingService.getVentasGastos().then((response) => {
      console.log(response);

      
      this.labels = response.label;
      this.data = [response.ventas, response.gastos];
      this.colors = ['', ''];
      this.chartType = 'bar';


      this.reportingService.getCobrosPagos().then((response) => {
        console.log(response);


        this.chartData = [response.cobros, response.pagos];
      }).catch((error) => {
        console.error(error);
      });
    }).catch((error) => {
      console.error(error);
    });
  }
}
