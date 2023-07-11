import { Component, OnInit, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';

interface ChartItem {
  shape: string;
  name: string;
  chartLabels: string[];
  chartData: MultiDataSet;
  chartType: ChartType;
  colors: string[];
}

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
  chartItems: ChartItem[] = [];
  @Input() name: string = '';
  @Input() shape: string = '';
  @Input() labels: any = [];
  @Input() data: any = [];
  @Input() colors: any = [];
  @Input() chartType: any = '';
  @Input() chartData: string[] = [];

  @Input() public chartItemList: Observable<ChartItem[]> | undefined;

  selected: string = 'Últimos 12 meses';

  items: string[] = ['Últimos 12 meses', 'Últimos 6 meses', 'Trimestral'];

  toggleChartType() {
    if (this.chartType === 'line') {
      this.chartType = 'bar';
    } else if (this.chartType === 'bar') {
      this.chartType = 'line';
    }
  }

  getToggleIcon(): string {
    return this.chartType === 'line' ? 'bar_chart' : 'show_chart';
  }

  constructor() {}

  ngOnInit(): void {
    if (this.chartItemList) {
      this.chartItemList.subscribe((chartItem) => {
        this.chartItems = chartItem;


      });
    }
  }
}
