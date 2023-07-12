import { Component, OnInit } from '@angular/core';


export interface PeriodicElement {

  modelo: string;
  resultado: number;
  estado: string;
  pago?: string;
  acciones?: any;
}

const ELEMENT_DATA: PeriodicElement[] = [
  {modelo: 'Modelo 000', resultado: 0.00, estado: 'En proceso'},
  {modelo: 'Modelo 000', resultado: 21.00, estado: 'Pendiente', acciones: 'icono, icono, icono'},
  {modelo: 'Modelo 000', resultado: 150.00, estado: 'Rectificado' },
  {modelo: 'Modelo 000', resultado: 150.00, estado: 'Confirmado', pago: 'aplazamiento', acciones: 'icono'},
  {modelo: 'Modelo 000', resultado: 150.00, estado: 'Presentado', pago: 'nrc',acciones: 'icono'},
  {modelo: 'Modelo 000', resultado: 150.00, estado: 'Presentado', pago: 'domiciliación bancaria',acciones: 'icono'},

];


@Component({
  selector: 'app-table-tax-model',
  templateUrl: './table-tax-model.component.html',
  styleUrls: ['./table-tax-model.component.scss']
})
export class TableTaxModelComponent implements OnInit {


  displayedColumns: string[] = ['modelo', 'resultado', 'estado', 'pago', 'acciones'];
  dataSource = ELEMENT_DATA;



  constructor() { }

  ngOnInit() {
  }

}
