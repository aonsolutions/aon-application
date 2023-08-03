import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CollectionFactory,
  ICollection,
  ITaxModel,
} from 'libraries/AonSDK/aon';
import { TranslateService } from '@ngx-translate/core';

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
  items: string[] = []
  selected: string = '';

  models: ICollection<ITaxModel> =
    new CollectionFactory().createTaxModelCollection();
  @Input() public taxModelList: Observable<ICollection<ITaxModel>> | undefined;

  constructor(private translateService: TranslateService) {
    this.translateService
    .get(['HOME.1_TRIMESTER', 'HOME.2_TRIMESTER', 'HOME.3_TRIMESTER', 'HOME.4_TRIMESTER'])
    .subscribe((result) => {
      this.items = [
        result['HOME.1_TRIMESTER'],
        result['HOME.2_TRIMESTER'],
        result['HOME.3_TRIMESTER'],
        result['HOME.4_TRIMESTER']
      ];
    });
  }

  ngOnInit(): void {
    if (this.taxModelList) {
      this.taxModelList.subscribe((taxModel) => {
        this.models = taxModel;
      });
    }
  }
}
