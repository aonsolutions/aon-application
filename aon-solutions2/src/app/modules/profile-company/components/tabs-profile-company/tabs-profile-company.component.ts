import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { IEnterprise, IRegistryEnterprise, IUser } from 'libraries/AonSDK/src/aon';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
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
  enterprise: IEnterprise | null = null;
  registryEnterprise: IRegistryEnterprise | null = null;
  user: IUser | null = null;
  originalEnterprise!: IEnterprise;
  originalRegistryEnterprise!: IRegistryEnterprise;
  originalUser!: IUser;
  tabIndex: number = 0;
  tabs: Tabs[] = [];

  constructor(
    private translateService: TranslateService,
    private enterpriseService: EnterpriseService,
    private userService: UserService
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
      let isSaved: boolean = false;
      const userModified: boolean = this.checkIfDataModified(this.originalUser, this.user!);
      const enterpriseModified: boolean = this.checkIfDataModified(this.originalEnterprise, this.enterprise!);
      const registryEnterpriseModified: boolean = this.checkIfDataModified(this.originalRegistryEnterprise, this.registryEnterprise!);

      // Verificamos si existen cambios en el usuario
      if (userModified) {
        this.userService.updateCurrentUserData(this.user!);
        isSaved = true;
      }

      // Verificamos si existen cambios en el enterprise
      console.log('datos a pasar', this.enterprise);
      if (enterpriseModified) {

        this.enterpriseService.updateCurrentEnterpriseData(this.enterprise!);
        isSaved = true;
      }

      // Verificamos si existen cambios en el registryEnterprise
      if (registryEnterpriseModified) {
        this.enterpriseService.updateCurrentEnterpriseRegistryData(this.registryEnterprise!);
        isSaved = true;
      }

      return isSaved;
    }

    onCancel() {
      // Obtén el objeto registryEnterprise original antes de realizar cambios
      // const originalRegistryEnterprise = this.registryEnterprises[0];

      // Restaura los valores originales en el objeto registryEnterprise actual
      // this.registryEnterprises[0] = { ...originalRegistryEnterprise };
    }

    async ngOnInit() {
      this.enterprise = await this.enterpriseService.getCurrentEntepriseData();
      this.registryEnterprise = await this.enterpriseService.getCurrentEnterpriseRegistryData();
      this.user = await this.userService.getCurrentUserData();

      // Copia de datos originales
      this.originalUser = { ...this.user };
      this.originalEnterprise = { ...this.enterprise };
      this.originalRegistryEnterprise = { ...this.registryEnterprise };
    }

    /**
     * Comprueba si los datos han sido modificados comparando los datos originales con los datos actuales.
     *
     * @param {IUser | IEnterprise | IRegistryEnterprise} originalData - Los datos originales a comparar.
     * @param {IUser | IEnterprise | IRegistryEnterprise} actualData - Los datos actuales a comparar con los datos originales.
     * @returns {boolean} - Devuelve true si los datos han sido modificados de lo contrario, devuelve false.
     */
    private checkIfDataModified(originalData: IUser|IEnterprise|IRegistryEnterprise, actualData: IUser|IEnterprise|IRegistryEnterprise) {
      if (originalData) {
        const isModified = JSON.stringify(originalData) !== JSON.stringify(actualData);
        return isModified;
      }
      return false;
    }


  }
