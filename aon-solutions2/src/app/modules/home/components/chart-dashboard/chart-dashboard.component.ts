import { Component, OnInit, Input } from '@angular/core';
import { Observable } from 'rxjs';
import { MultiDataSet } from 'ng2-charts';
import { ChartType } from 'chart.js';
import { TranslateService } from '@ngx-translate/core';

export interface ChartItem {
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

  selected: string = '';
  items: string[] = [
    this.translateService.instant('HOME.LAST_12_MONTHS'),
    this.translateService.instant('HOME.LAST_6_MONTHS'),
    this.translateService.instant('HOME.QUARTERLY'),
  ];

  constructor(private translateService: TranslateService) {}

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

  ngOnInit(): void {
    if (this.chartItemList) {
      this.chartItemList.subscribe((chartItem) => {
        this.chartItems = chartItem;
      });
    }
  }
}
