import { Component, Input, OnInit } from '@angular/core';
import { TaxModel } from 'src/app/core/models/class/tax-model';
import { Observable } from 'rxjs';

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
  selected: string = '1 trimestre';

  items: string[] = [
    '1 trimestre',
    '2 trimestre',
    '3 trimestre',
    '4 trimestre',
  ];

  models: TaxModel[] = [];
  @Input() public taxModelList: Observable<TaxModel[]> | undefined;

  constructor() {}

  ngOnInit(): void {
    if (this.taxModelList) {
      this.taxModelList.subscribe((taxModel) => {
        this.models = taxModel;
      });
    }
  }
}
