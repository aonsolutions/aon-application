import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { RegistryEnterpriseService } from 'src/app/core/services/registry-enterprise.service';

@Component({
  selector: 'app-input-profile-registry-data',
  templateUrl: './input-profile-registry-data.component.html',
  styleUrls: ['./input-profile-registry-data.component.scss'],
})
export class InputProfileRegistryDataComponent implements OnInit {
  registryEnterprises: any[] = [];
  documentEnterprise: string = '';
  @Input() registryEnterprise: any; // Recibe el objeto registryEnterprise desde el padre
  @Output() registryEnterpriseChange: EventEmitter<any> = new EventEmitter<any>();

  constructor(private registryEnterpriseService: RegistryEnterpriseService) {
    if (localStorage.getItem('enterprise')) {
       this.documentEnterprise = localStorage.getItem('enterprise')!;
         }
    this.registryEnterpriseService
      .getRegistryEnterprise(this.documentEnterprise)
      .then((registryEnterprise) => {
        this.registryEnterprises.push(registryEnterprise);
      });

      // const documentEnterprise = localStorage.getItem('enterprise');
      // this.registryEnterpriseService
      //   .getRegistryEnterprise(documentEnterprise)
      //   .then((registryEnterprise) => {
      //     this.registryEnterprises.push(registryEnterprise);
      //   });
  }
  getValue(newValue: any, registryEnterprise: any, propertyName: string) {
    // Actualiza el valor correspondiente en el objeto registryEnterprise
    registryEnterprise[propertyName] = newValue;

    localStorage.setItem('registryEnterprise', JSON.stringify(this.registryEnterprise));
  // Emite el evento con los datos actualizados
  this.registryEnterpriseChange.emit(this.registryEnterprises);

  }
  // onSave() {
  //   // Llama a la función para actualizar los datos en el servidor
  //   this.registryEnterpriseService
  //     .updateRegistryEnterprise(this.registryEnterprises[0])
  //     .then((updatedRegistry) => {
  //       // Actualiza el valor correspondiente en el objeto registryEnterprise
  //       this.registryEnterprises[0] = updatedRegistry;
  //     });
  // }

  // onCancel() {
  //   // Obtén el objeto registryEnterprise original antes de realizar cambios
  //   const originalRegistryEnterprise = this.registryEnterprises[0];

  //   // Restaura los valores originales en el objeto registryEnterprise actual
  //   this.registryEnterprises[0] = { ...originalRegistryEnterprise };
  // }



  ngOnInit(): void {
      // Recuperar el objeto registryEnterprise desde el LocalStorage al inicializar el componente
      const storedRegistryEnterprise = localStorage.getItem('registryEnterprise');
      if (storedRegistryEnterprise) {
        this.registryEnterprise = JSON.parse(storedRegistryEnterprise);
      }
  }
}
