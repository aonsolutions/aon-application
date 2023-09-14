import { Component, OnInit } from '@angular/core';
import { RegistryEnterpriseService } from 'src/app/core/services/registry-enterprise.service';

@Component({
  selector: 'app-input-profile-registry-data',
  templateUrl: './input-profile-registry-data.component.html',
  styleUrls: ['./input-profile-registry-data.component.scss'],
})
export class InputProfileRegistryDataComponent implements OnInit {
  registryEnterprises: any[] = [];

  constructor(private registryEnterpriseService: RegistryEnterpriseService) {
    const documentEnt = localStorage.getItem('enterprise');
    this.registryEnterpriseService
      .getRegistryEnterprise(documentEnt)
      .then((registryEnterprise) => {
        this.registryEnterprises.push(registryEnterprise);
      });
  }
  getValue(newValue: any, registryEnterprise: any, propertyName: string) {
    // Actualiza el valor correspondiente en el objeto registryEnterprise
    registryEnterprise[propertyName] = newValue;
  }

  onSave() {
    // Llama a la función para actualizar los datos en el servidor
    this.registryEnterpriseService
      .updateRegistryEnterprise(this.registryEnterprises[0])
      .then((updatedRegistry) => {
        // Actualiza el valor correspondiente en el objeto registryEnterprise
        this.registryEnterprises[0] = updatedRegistry;
      });
  }

  onCancel() {
    // Obtén el objeto registryEnterprise original antes de realizar cambios
    const originalRegistryEnterprise = this.registryEnterprises[0];

    // Restaura los valores originales en el objeto registryEnterprise actual
    this.registryEnterprises[0] = { ...originalRegistryEnterprise };
  }

  ngOnInit(): void {}
}
