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
    tabs: Tabs[] = [
    { name: this.translateService.instant('TAX-PANEL.1-TRIMESTER')},
    { name: this.translateService.instant('TAX-PANEL.2-TRIMESTER')},
    { name: this.translateService.instant('TAX-PANEL.3-TRIMESTER')},
    { name: this.translateService.instant('TAX-PANEL.4-TRIMESTER')},
    { name: this.translateService.instant('TAX-PANEL.ALL')},
  ];

  public modelsList: any[] = [];

  public modelsYears: any[] = [];
  constructor(public translateService: TranslateService) {}

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
  }
}
