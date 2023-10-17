import { Component, Input, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { IEnterprise, IRegistryEnterprise } from 'libraries/AonSDK/src/aon';
import { UserService } from 'src/app/core/services/user.service';

export interface Tabs {
  name: string;
}

@Component({
  selector: 'app-tabs-profile-company',
  templateUrl: './tabs-profile-company.component.html',
  styleUrls: ['./tabs-profile-company.component.scss'],
})
export class TabsProfileCompanyComponent implements OnInit {
  tabIndex: number = 0;
  tabs: Tabs[] = [];
  users: any[] = [];
  @Input() enterprise: IEnterprise | null = null;
  @Input() registryEnterprise: IRegistryEnterprise | null = null;

  constructor(
    private translateService: TranslateService,
    private userService: UserService,
    ) {
      this.translateService
      .get([
        'PROFILE.PERSONAL_INFORMATION',
        'PROFILE.COMPANY_INFORMATION',
        'PROFILE.REGISTRATION_INFORMATION',
        'PROFILE.CERTIFICATES',
      ])
      .subscribe((result) => {
        this.tabs = [
          { name: result['PROFILE.PERSONAL_INFORMATION'] },
          { name: result['PROFILE.COMPANY_INFORMATION'] },
          { name: result['PROFILE.REGISTRATION_INFORMATION'] },
          { name: result['PROFILE.CERTIFICATES'] },
        ];
      });
    }

    updateRegistryEnterprise(updatedRegistry: any) {
      // Actualiza el objeto registryEnterprises con el valor recibido del hijo
      // this.registryEnterprises[0] = updatedRegistry;
    }

    onSave() {
      // Llama a la función para actualizar los datos en el servidor
      console.log('guardar', this.users);

      // this.registryEnterpriseService
      // .updateRegistryEnterprise(this.registryEnterprises[0])
      // .then((updatedRegistry) => {
      //   // Actualiza el valor correspondiente en el objeto registryEnterprise
      //   this.registryEnterprises[0] = updatedRegistry;
      // });

      this.userService
      .updateUser(this.users[0])
      .then((updatedUser) => {
        // Actualiza el valor correspondiente en el objeto user
        this.users[0] = updatedUser;
      });
    }



    onCancel() {
      // Obtén el objeto registryEnterprise original antes de realizar cambios
      // const originalRegistryEnterprise = this.registryEnterprises[0];

      // Restaura los valores originales en el objeto registryEnterprise actual
      // this.registryEnterprises[0] = { ...originalRegistryEnterprise };
    }

    async ngOnInit() {
    }

  }
