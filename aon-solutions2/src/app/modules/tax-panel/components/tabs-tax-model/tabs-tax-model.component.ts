import {
  Component,
  EventEmitter,
  HostBinding,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { MatFormFieldAppearance } from '@angular/material/form-field';
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
  tabIndex: number = 0;

  constructor(public translateService: TranslateService) {}

  // tabs: Tabs[] = [
  //   { name: '1_TRIMESTER' },
  //   { name: '2_TRIMESTER' },
  //   { name: '3_TRIMESTER' },
  //  { name: '4_TRIMESTER' },
  //   { name: 'ALL' },
  //  ];

  tabs: Tabs[] = [
    { name: 'TAX-PANEL.1_TRIMESTER' },
    { name: 'TAX-PANEL.2_TRIMESTER' },
    { name: 'TAX-PANEL.3_TRIMESTER' },
    { name: 'TAX-PANEL.4_TRIMESTER' },
    { name: 'TAX-PANEL.ALL' },
  ];

  public modelsList: any[] = [];

  public modelsYears: any[] = [];

  @Input() type: string = '';
  @Input() appearance: MatFormFieldAppearance = 'outline';
  @Input() width: string = '100%';
  @Input() label: string = '';
  @Input() hint: string = '';
  @Input() placeholder: string = '';
  @Input() suffixBehavior: any;
  @Input() value: any = '';
  @Input() options: any;
  @Input() disabled: string = 'false';
  @Input() classes: string = '';
  @Input() maxRow: string = '3';
  @Input() minRow: string = '10';
  @Input() appearanceDetail: string = 'mat-form-field-appearance-bold-outline';
  @Input() trimester!: number;
  @Input() paddingLeftHeader: string = '';
  @Input() tabColor: string = '';

  @HostBinding('style.--styleTabColor') styleTabColor = '';

  @Output() inputValue = new EventEmitter<any>();
  @Output() changeTabIndex = new EventEmitter<number>();
  showModal: any;

  ngOnInit(): void {
    //  this.translateTabs();
  }

  // Función para traducir desde el archivo .json
//    translateTabs(): void{
//      this.translateService.get('TAX-PANEL').subscribe((translation) => {
//       this.tabs.forEach((item, index) => {
//         const translateTab = translation[item.name];
//        if(translateTab){
//           this.tabs[index].name = translateTab;
//        }
//      })
//     });
//  }
}
