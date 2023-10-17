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
  @Input() type             : string  = '';
  @Input() appearance       : MatFormFieldAppearance = 'outline';
  @Input() width            : string  = '100%';
  @Input() label            : string  = '';
  @Input() hint             : string  = '';
  @Input() placeholder      : string  = '';
  @Input() value            : any     = '';
  @Output() inputValue = new EventEmitter<any>();
  @Input() options          : any;
  @Input() disabled         : string  = 'false';
  @Input() required         : string  = 'false';
  @Input() classes          : string  = '';
  @Input() maxNumber        : string  = '';
  @Input() minNumber        : string  = '';
  @Input() maxRow           : string  = '3';
  @Input() minRow           : string  = '10';
  @Input() appearanceDetail : string  = 'mat-form-field-appearance-bold-outline';
  @Input() suffixBehavior   : string  = '';
  @Input() suffixShowIcon   : boolean = false;
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
      this.suffixShowIcon = true;
      this.suffixIcon     = 'eye';
      this.suffixBehavior = 'showPass';
    }
  }

  selectFunction(event: MouseEvent) {
    // Se evita que se active el envio del formulario cuando se hace click en el span del input
    event.preventDefault();

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

  /*
    Input solo number
  */
    returnValueNumber(value: any) {
      if (this.minNumber !== '' && this.minNumber > value){
        value = this.minNumber;
      }
      if (this.maxNumber !== '' && this.maxNumber < value){
        value = this.maxNumber;
      }
      
      this.inputValue.emit(value);
    }
    validateFormatNumber(event: any) {
      let key;
      if (event.type === 'paste') {
        key = event.clipboardData.getData('text/plain');
      } else {
        key = event.keyCode;
        key = String.fromCharCode(key);
      }
      // 1 - Permitir del 0 al 9
      // 2 - Permitir .
      // 3 - Permitir ,
      // 4 - Permitir -
      const regex = /[0-9]|\.|\,|\-/;
      if (!regex.test(key)) {
        if(event.returnValue)
          event.returnValue = false;
        if (event.preventDefault) {
          event.preventDefault();
        }
      }
    }

}
