import { Component, OnInit } from '@angular/core';
import { TaxModelService } from '../../../../core/services/tax-model.service';
import { TaxModel } from 'src/app/core/models/class/tax-model';

export interface Models {

  name: string;
  taxType: number;
  result: number;
  status: string;
  paymentMethod?: string;
  trimester: number;
  year: number;
  acciones?: any;
}


@Component({
  selector: 'app-table-tax-model',
  templateUrl: './table-tax-model.component.html',
  styleUrls: ['./table-tax-model.component.scss']
})
export class TableTaxModelComponent implements OnInit {

  models: TaxModel[] = [];
  displayedColumns: string[] = ['modelo', 'resultado', 'estado', 'pago', 'acciones'];
  // dataSource = ELEMENT_DATA;
  // ELEMENT_DATA: TaxModel[] = [];



  constructor( public taxModelService :TaxModelService ) {
    taxModelService.getTaxModelList().then((response) => {
     this.models = response;
     console.log(response)
   });
  }


 ngOnInit(): void {
 }

}
