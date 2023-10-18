import { Component, Input, OnInit } from '@angular/core';
import { IUser } from 'libraries/AonSDK/src/aon';

@Component({
  selector: 'app-input-profile-personal-data',
  templateUrl: './input-profile-personal-data.component.html',
  styleUrls: ['./input-profile-personal-data.component.scss'],
})
export class InputProfilePersonalDataComponent implements OnInit {
  showPasswordFields: boolean = false;
  @Input() user: IUser | null = null;

  constructor() {
  }

  togglePasswordFields() {
    this.showPasswordFields = !this.showPasswordFields;
  }

  getValue(newValue: any, user: any, propertyName: string) {
    // Actualiza el valor correspondiente en el objeto user
    user[propertyName] = newValue;
     // Emite el evento con el valor actualizado

  }

  onSave() {
  }

  onCancel() {
  }

  ngOnInit() {
  }

}
