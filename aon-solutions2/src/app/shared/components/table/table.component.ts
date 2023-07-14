import { Component, Input } from '@angular/core';
import { TaxModel } from 'src/app/core/models/class/tax-model';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.scss']
})
export class TableComponent {
  @Input() dataSource: TaxModel[] = [];
  displayedColumns: string[] = ['name', 'result', 'status', 'paymentMethod', 'acciones'];
}







// import { Component, OnInit } from '@angular/core';
// import { TaxModel } from 'src/app/core/models/class/tax-model';
// import { TaxModelService } from 'src/app/core/services/tax-model.service';

// export interface Models {
//   name: string;
//   taxType: number;
//   result: number;
//   status: string;
//   paymentMethod?: string;
//   trimester?: number;
//   year?: number;
//   acciones?: any;
// }

// // const ELEMENT_DATA: Models[] = [
// //   {taxType: 1, name: 'Modelo 000', result: 1.0079, status: 'En proceso'},
// //   {taxType: 2, name: 'Modelo 000', result: 4.0026, status: 'Pendiente'},
// //   {taxType: 3, name: 'Modelo 000', result: 6.941, status: 'Rectificado'},
// //   {taxType: 4, name: 'Modelo 000', result: 9.0122, status: 'Confirmado', paymentMethod: 'aplazamiento'},
// //   {taxType: 5, name: 'Modelo 000', result: 10.811, status: 'Presentado', paymentMethod: 'nrc'},
// //   {taxType: 9, name: 'Modelo 000', result: 12.0107, status: 'Presentado', paymentMethod: 'domiciliacion bancaria'},

// // ];

// @Component({
//   selector: 'app-table',
//   templateUrl: './table.component.html',
//   styleUrls: ['./table.component.scss']
// })
// export class TableComponent implements OnInit {

//   dataSource: TaxModel[] = [];
//   displayedColumns: string[] = [ 'name', 'result', 'status', 'paymentMethod', 'acciones'];


//   constructor( public taxModelService :TaxModelService ) {
//     taxModelService.getTaxModelList().then((response) => {
//      this.dataSource = response;
//      console.log(response)
//    });
//   }


//   ngOnInit(): void {
//   }

// }


