import { Component, OnInit, Input } from '@angular/core';

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

  @Input() name : string = '';
  @Input() chart : string = '';
  @Input() shape : string = '';

  selected : string = "Últimos 12 meses";

  items : string[] = ["Últimos 12 meses","Últimos 6 meses","Trimestral"];

  constructor() { }

  ngOnInit(): void {
  }

}
