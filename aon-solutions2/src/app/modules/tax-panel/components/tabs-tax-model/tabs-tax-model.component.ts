import { Component, EventEmitter, HostBinding, Input, OnInit, Output } from '@angular/core';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { TranslateService } from '@ngx-translate/core';

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
  tabIndex            : number  = 0;
  modelsList          : any[]   = [];
  modelsYears         : any[]   = [];
  models              : any;
  tabs                : Tabs [] = [];
  @Input() trimester! : number;
  @Input() tabColor   : string  = '';
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @Output() inputValue = new EventEmitter<any>();
  @Output() changeTabIndex = new EventEmitter<number>();
  showModal: any;

  constructor(
    public taxModelService: TaxModelService,
    private translateService: TranslateService
  ) {
    this.translateService.get(
      ['TAX-PANEL.1_TRIMESTER', 'TAX-PANEL.2_TRIMESTER', 'TAX-PANEL.3_TRIMESTER','TAX-PANEL.4_TRIMESTER', 'TAX-PANEL.ALL']
    ).subscribe( result => {
      // Marcar el trimestre en el que estamos
      this.trimesterThis();
      // Cabecera de los tags
      this.tabs = [
        { name: result['TAX-PANEL.1_TRIMESTER']},
        { name: result['TAX-PANEL.2_TRIMESTER']},
        { name: result['TAX-PANEL.3_TRIMESTER']},
        { name: result['TAX-PANEL.4_TRIMESTER']},
        { name: result['TAX-PANEL.ALL']},
      ]
    })
    // Datos del modelo
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

  }

  ngOnInit(): void {
  }
  
  // En el trimestre que estamos
  trimesterThis() {
    const currentDate   = new Date();
    const currentMonth  = currentDate.getMonth() + 1;
    const currentYear   = currentDate.getFullYear();

    if (currentMonth >= 2 && currentMonth <= 4) {
      //tabIndex = 0 corresponde a trimestre 1
      this.tabIndex = 0;
    } else if (currentMonth >= 5 && currentMonth <= 7) {
      //trimestre 2
      this.tabIndex = 1;
    } else if (currentMonth >= 8 && currentMonth <= 10) {
      //trimestre  3
      this.tabIndex = 2;
    } else if (currentMonth > 10 || currentMonth < 2) {
      //trimestre 4
      this.tabIndex = 3;
    }
  }
}
