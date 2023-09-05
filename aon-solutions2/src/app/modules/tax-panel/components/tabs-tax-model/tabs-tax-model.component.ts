import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { TaxModelService } from 'src/app/core/services/tax-model.service';
import { TranslateService } from '@ngx-translate/core';
import { FilterBuilder } from 'libraries/AonSDK/aon';

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
  modelsList: any[] = [];
  modelsListValue: string = '';
  modelsYears: any[] = [];
  models: any;
  tabs: Tabs[] = [];
  selectedModel: number = 0;
  currentDate = new Date();
  selectedYear: string = this.currentDate.getFullYear().toString();

  @Input() trimester!: number;
  @Input() tabColor: string = '';
  @HostBinding('style.--styleTabColor') styleTabColor = '';
  @Output() inputValue = new EventEmitter<any>();
  @Output() changeTabIndex = new EventEmitter<number>();
  showModal: any;

  constructor(
    private taxModelService: TaxModelService,
    private translateService: TranslateService
  ) {
    this.translateService
      .get([
        'TAX_PANEL.1_TRIMESTER',
        'TAX_PANEL.2_TRIMESTER',
        'TAX_PANEL.3_TRIMESTER',
        'TAX_PANEL.4_TRIMESTER',
        'TAX_PANEL.ALL',
      ])
      .subscribe((result) => {
        // Marcar el trimestre en el que estamos
        taxModelService.thisTrimester().then((response) => {
          // Le restamos 1 para que coincida con el valor del Tabs
          this.tabIndex = response - 1;
        });
        // Cabecera de los tags
        this.tabs = [
          { name: result['TAX_PANEL.1_TRIMESTER'] },
          { name: result['TAX_PANEL.2_TRIMESTER'] },
          { name: result['TAX_PANEL.3_TRIMESTER'] },
          { name: result['TAX_PANEL.4_TRIMESTER'] },
          { name: result['TAX_PANEL.ALL'] },
        ];
      });
    // Datos del modelo

    taxModelService.getTaxModelList().then((response) => {
      this.models = response;
      this.modelsList.push({ value: 0, text: 'Todos' });
      // this.modelsYears.push({ value: 0, text: 'Todos' });
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
    console.log(this.modelsListValue);
  }

  filterModel(value: number, type: number) {
    this.selectedModel = type === 1 ? value : this.selectedModel;
    this.selectedYear = type === 2 ? value.toString() : this.selectedYear;

    // this.inputValue.emit({ year: this.selectedYear, name: this.selectedModel });
    console.log('Pestaña actual:', this.tabIndex);
    console.log('Valor:', value);
    console.log('Type:', type);
  }

}
