import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-input',
  templateUrl: './input.component.html',
  styleUrls: ['./input.component.scss']
})
export class InputComponent implements OnInit {

  @Input() type : string = '';
  @Input() appearance : MatFormFieldAppearance = 'legacy';
  @Input() width : string = '';
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
  classSuffix :string = '';

  constructor() { }

  ngOnInit(): void {
    this.suffixBehavior != '' ? this.classSuffix = '' : this.classSuffix = 'cursor'
  }

  selectFunction() {
    switch(this.suffixBehavior){
      case 'clear':{
        this.clear();
        this.returnValue(this.value);
        break;
      }
      case 'showPass':{
        this.showPass();
        break;
      }
      default:{
        break;
      }
    }
  }

  clear() {
    this.value = ''
  }

  showPass() {
    if(this.type == 'text'){
      this.type = 'password';
    }else{
      this.type = 'text';
    }
  }

  returnValue(value: any) {
    this.inputValue.emit(value);
  }

}
