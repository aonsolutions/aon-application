import { Component, Input, OnInit } from '@angular/core';
import { CollectionFactory, ICollection, ITaxModel } from 'libraries/AonSDK/aon';
import { TaxModelService } from 'src/app/core/services/tax-model.service';



 @Component({
 selector: 'app-table-tax-model',
   templateUrl: './table-tax-model.component.html',
   styleUrls: ['./table-tax-model.component.scss']
 })
 export class TableTaxModelComponent implements OnInit {
  
  @Input() dataSource: ICollection<ITaxModel> = new CollectionFactory().createTaxModelCollection();
  @Input() trimester!: number;

  // dataSource: TaxModel[] = [];
  displayedColumns: string[] = [ 'name', 'result', 'status', 'paymentMethod', 'acciones'];


  constructor( public taxModelService :TaxModelService ) {
    taxModelService.getTaxModelList().then((response) => {
     this.dataSource = response;

   });
  }


  ngOnInit(): void {
  }

}


