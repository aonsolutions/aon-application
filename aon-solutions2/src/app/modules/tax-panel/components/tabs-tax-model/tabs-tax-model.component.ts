import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { TaxModel } from 'src/app/core/models/class/tax-model';
import { TaxModelService } from 'src/app/core/services/tax-model.service';



export interface Tabs {
  name: string;
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

  @Input() type : string = '';
  @Input() appearance : MatFormFieldAppearance = 'outline';
  @Input() width : string = '100%';
  @Input() label : string = '';
  @Input() hint : string = '';
  @Input() placeholder : string = '';
  @Input() suffixBehavior : any;
  @Input() value : any = '';
  @Output() inputValue = new EventEmitter<any>();
  @Input() options : any ;
  @Input() disabled : string = 'false';
  @Input() classes : string = '';
  @Input() maxRow : string = '3';
  @Input() minRow : string = '10';
  @Input() appearanceDetail: string = 'mat-form-field-appearance-bold-outline';

  @Input()
  trimester!: number;


  @Input() paddingLeftHeader: string = '';
  @Input() tabColor: string = ''
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @Output() changeTabIndex = new EventEmitter<number>();
showModal: any;




  constructor( public taxModelService :TaxModelService ) {
    taxModelService.getTaxModelList().then((response) => {
     this.models = response;
     console.log(response)
   });
  }

  ngOnInit(): void {
  }

}
