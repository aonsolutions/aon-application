import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { IRegistryEnterprise } from 'libraries/AonSDK/src/aon';

@Component({
  selector: 'app-input-profile-registry-data',
  templateUrl: './input-profile-registry-data.component.html',
  styleUrls: ['./input-profile-registry-data.component.scss'],
})
export class InputProfileRegistryDataComponent implements OnInit {
  documentEnterprise: string = '';
  @Input() registryEnterprise: IRegistryEnterprise | null = null;
  // @Output() registryEnterpriseChange: EventEmitter<any> = new EventEmitter<any>();

  constructor() {
  }
  getValue(newValue: any, registryEnterprise: any, propertyName: string) {
    // Actualiza el valor correspondiente en el objeto registryEnterprise
    registryEnterprise[propertyName] = newValue;

    // localStorage.setItem('registryEnterprise', JSON.stringify(this.registryEnterprise));
    // Emite el evento con los datos actualizados
    // this.registryEnterpriseChange.emit(this.registryEnterprise);

  }



  ngOnInit(): void {
  }

}
