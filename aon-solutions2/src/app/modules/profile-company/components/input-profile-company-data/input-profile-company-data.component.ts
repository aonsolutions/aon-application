import { Component, Input, OnInit } from '@angular/core';
import { IEnterprise } from 'libraries/AonSDK/src/aon';

@Component({
  selector: 'app-input-profile-company-data',
  templateUrl: './input-profile-company-data.component.html',
  styleUrls: ['./input-profile-company-data.component.scss'],
})
export class InputProfileCompanyDataComponent implements OnInit {
  @Input() enterprise: IEnterprise | null = null;
  emailList: string[] = [];
  addForm: number = 0;
  addMail: number = 0;

  constructor() {
  }

  async ngOnInit() {
    this.initMail();
  }

  /**
   * Carga el correo en caso de que ya exista en el listado de correos
   *
   * @private
   * @memberof NombreDeLaClase
   * @return {void}
   */
  private initMail() {
    if (this.enterprise?.Email) {
      this.emailList.push(this.enterprise?.Email);
    } else {
      this.emailList.push('');
    }
  }

  addEmail() {
    this.emailList.push('');
  }

  /**
   * Actualiza el valor correspondiente en el objeto enterprise.
   *
   * @param {any} newValue - El nuevo valor que se va a asignar.
   * @param {any} enterprise - El objeto enterprise.
   * @param {string} propertyName - El nombre de la propiedad que se va a actualizar.
   */
  getValue(newValue: any, enterprise: any, propertyName: string) {
    enterprise[propertyName] = newValue;
  }

}
