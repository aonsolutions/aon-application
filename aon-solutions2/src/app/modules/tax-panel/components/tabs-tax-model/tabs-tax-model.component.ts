import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
} from '@angular/core';
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
  styleUrls: ['./tabs-tax-model.component.scss'],
})
export class TabsTaxModelComponent implements OnInit {
  tabIndex: number = 0;
  tabIndexSelect: any;
  modelsList: any[] = [];
  modelsYears: any[] = [];
  models: any;

  tabs: Tabs[] = [
    { name: '1 Trimestre' },
    { name: '2 Trimestre' },
    { name: '3 Trimestre' },
    { name: '4 Trimestre' },
    { name: 'Todos' },
  ];

  @Input() trimester!: number;
  @Input() tabColor: string = '';

  @HostBinding('style.--styleTabColor') styleTabColor = '';

  @Output() inputValue = new EventEmitter<any>();
  @Output() changeTabIndex = new EventEmitter<number>();
  showModal: any;

  constructor(public taxModelService: TaxModelService) {
    taxModelService.getTaxModelList().then((response) => {
      this.models = response;
      response.forEach((element) => {
        if (!this.modelsList.some((model) => model.text === element.Name)) {
          this.modelsList.push({ value: element.Name, text: element.Name });
        }
        if (!this.modelsYears.some((model) => model.text === element.Year)) {
          this.modelsYears.push({ value: element.Year, text: element.Year });
        }
      });
    });

    this.setInitialTabIndex();
    this.tabIndex! = this.tabIndexSelect!;
    // this.deleteSelectTab();
    // this.selectTab('mat-tab-label-0-' + this.tabIndexSelect);


  }
  setInitialTabIndex() {
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear();
    const currentMonth = currentDate.getMonth() + 1;

    if (currentMonth >= 2 && currentMonth <= 4) {
      //tabIndex = 0 corresponde a trimestre 1
      this.tabIndexSelect = 0;
    } else if (currentMonth >= 5 && currentMonth <= 7) {
      //trimestre 2
      this.tabIndexSelect = 1;
    } else if (currentMonth >= 8 && currentMonth <= 10) {
      //trimestre  3
      this.tabIndexSelect = 2;
    } else if (currentMonth > 10 || currentMonth < 2) {
      //trimestre 4
      this.tabIndexSelect = 3;
    }

    // console.log (currentYear);
    // console.log (currentMonth);
    // console.log (this.tabIndex);
    // console.log (currentDate);
  }

  //id="mat-tab-label-0-0"

  selectTab(id: string){
    console.log(id)
    // Cogemos el Tab
    let element = document.getElementById(id);
    // Si no existe la agregamos, si existe la removemos
console.log(element);
      element!.classList.add('mat-tab-label-active');

  }

  deleteSelectTab(){
    // Eliminamos clase si existe un elemento ya marcado
    const elements = document.getElementsByClassName('mat-tab-label-active');
    while(elements.length > 0){
      elements[0].classList.remove('mat-tab-label-active');
    }
  }

  ngOnInit(): void {

  }
}
