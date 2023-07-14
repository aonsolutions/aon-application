import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { TaxModel } from 'src/app/core/models/class/tax-model';
import { TaxModelService } from 'src/app/core/services/tax-model.service';



export interface Tabs {
  name: string; // Nombre de la tab
}

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
  selector: 'app-tabs-tax-model',
  templateUrl: './tabs-tax-model.component.html',
  styleUrls: ['./tabs-tax-model.component.scss']
})
export class TabsTaxModelComponent implements OnInit {

  models: TaxModel[] = [];


  tabIndex:number = 0

  tabs: Tabs[] = [
    { name: '1 Trimestre'},
    { name: '2 Trimestre' },
    { name: '3 Trimestre'},
    { name: '4 Trimestre'},
    { name: 'Todos'},
  ];

  @Input() paddingLeftHeader: string = '';
  @Input() tabColor: string = ''
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @Output() changeTabIndex = new EventEmitter<number>();




  constructor( public taxModelService :TaxModelService ) {
    taxModelService.getTaxModelList().then((response) => {
     this.models = response;
     console.log(response)
   });
  }

  ngOnInit(): void {
  }

}
