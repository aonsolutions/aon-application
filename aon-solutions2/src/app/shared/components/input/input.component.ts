import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { DateAdapter } from '@angular/material/core';
import { MatFormFieldAppearance } from '@angular/material/form-field';

@Component({
  selector    : 'app-input',
  templateUrl : './input.component.html',
  styleUrls   : ['./input.component.scss'],
})

export class InputComponent implements OnInit {
  @Input() type             : string = '';
  @Input() appearance       : MatFormFieldAppearance = 'outline';
  @Input() width            : string = '100%';
  @Input() label            : string = '';
  @Input() hint             : string = '';
  @Input() placeholder      : string = '';
  @Input() value            : any    = '';
  @Output() inputValue = new EventEmitter<any>();
  @Input() options          : any;
  @Input() disabled         : string = 'false';
  @Input() required         : string = 'false';
  @Input() classes          : string = '';
  @Input() maxRow           : string = '3';
  @Input() minRow           : string = '10';
  @Input() appearanceDetail : string = 'mat-form-field-appearance-bold-outline';
  @Input() suffixBehavior   : string = '';
  suffixIcon       : string = '';
  classSuffix      : string = '';
  // Type of input
  intputType: string[] = [
    "button",
    "checkbox",
    "color",
    "email",
    "file",
    "hidden",
    "image",
    "month",
    "number",
    "password",
    "radio",
    "range",
    "reset",
    "submit",
    "tel",
    "text",
    "time",
    "url",
    "week"
  ];
  constructor(
    private translateService: TranslateService,
    private dateAdapter: DateAdapter<any>
  ) {
    // Idioma para los DATEPICKER
    this.dateAdapter.setLocale(this.translateService.getDefaultLang());    
  }

  ngOnInit(): void {
    // Type es password - Mostrar y ocultar pass
    if(this.type == 'password' && this.suffixBehavior === 'showPass'){
      this.suffixIcon     = 'eye';
      this.suffixBehavior = 'showPass'; 
    }
  }

  selectFunction() {
    switch(this.suffixBehavior){
      case 'clear':
        this.clear();
        this.returnValue(this.value);
      break;
      case 'showPass':
        this.showPass();
      break;
      default:
      break;
    }
  }

  clear() {
    this.value = ''
  }

  showPass() {
    this.suffixIcon = this.suffixIcon === 'visibility_off' ? 'eye' : 'visibility_off';
    this.type       = this.type == 'text' ? 'password' : 'text';
  }

  returnValue(value: any) {
    this.inputValue.emit(value);
  }

}
