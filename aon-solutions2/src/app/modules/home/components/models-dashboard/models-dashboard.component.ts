import { Component, OnInit } from '@angular/core';
import { TaxModelService } from '../../../../core/services/tax-model.service';
import { TaxModel } from 'src/app/core/models/class/tax-model';

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

   selected : string = "1 trimestre";

   items : string[] = ["1 trimestre","2 trimestre","3 trimestre", "4 trimestre"];

   models: TaxModel[] = [];


   constructor( public taxModelService :TaxModelService ) {
      taxModelService.getTaxModelList().then((response) => {
       this.models = response;
     });
    }


   ngOnInit(): void {
   }

 }




