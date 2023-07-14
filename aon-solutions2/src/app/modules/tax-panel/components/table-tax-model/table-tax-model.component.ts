import { Component, Input, OnInit } from '@angular/core';
import { TaxModel } from 'src/app/core/models/class/tax-model';
import { TaxModelService } from 'src/app/core/services/tax-model.service';



 @Component({
 selector: 'app-table-tax-model',
   templateUrl: './table-tax-model.component.html',
   styleUrls: ['./table-tax-model.component.scss']
 })
 export class TableTaxModelComponent implements OnInit {
  @Input() dataSource: TaxModel[] = [];

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


