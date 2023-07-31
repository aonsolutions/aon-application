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
  selected: string = '';

  items: string[] = [
    '1_TRIMESTER',
    '2_TRIMESTER',
    '3_TRIMESTER',
    '4_TRIMESTER',
  ];

  models: ICollection<ITaxModel> =
    new CollectionFactory().createTaxModelCollection();
  @Input() public taxModelList: Observable<ICollection<ITaxModel>> | undefined;

  constructor(private translateService: TranslateService) {}

  ngOnInit(): void {
    if (this.taxModelList) {
      this.taxModelList.subscribe((taxModel) => {
        this.models = taxModel;
      });
    }
    this.translateItems();
  }
  translateItems(): void {
    this.translateService.get('HOME').subscribe((translation) => {
      this.selected = translation['1_TRIMESTER'];
      this.items.forEach((item, index) => {
        this.items[index] = translation[item];
      });
    });
  }
}
