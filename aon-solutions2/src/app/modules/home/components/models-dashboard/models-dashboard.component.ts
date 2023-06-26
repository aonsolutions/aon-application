import { Component, OnInit } from '@angular/core';

export interface Models {
  name: string;
  amount: string;
  tax: string;
}

@Component({
  selector: 'app-models-dashboard',
  templateUrl: './models-dashboard.component.html',
  styleUrls: ['./models-dashboard.component.scss'],
  host: {
    '[style.width]': "'100%'",
    '[style.height]': "'100%'",
  },
})
export class ModelsDashboardComponent implements OnInit {

  constructor() { }

  selected : string = "1 trimestre";

  items : string[] = ["1 trimestre","2 trimestre","3 trimestre", "4 trimestre"];

  models : Models[] = [
    {name: 'Modelo 000', tax: 'Tipo impuesto', amount: '150,00 €'},
    {name: 'Modelo 000', tax: 'Tipo impuesto', amount: '150,00 €'},
    {name: 'Modelo 000', tax: 'Tipo impuesto', amount: '150,00 €'},
    {name: 'Modelo 000', tax: 'Tipo impuesto', amount: '150,00 €'},
    {name: 'Modelo 000', tax: 'Tipo impuesto', amount: '150,00 €'},
  ]

  ngOnInit(): void {
  }

}
