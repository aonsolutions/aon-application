import { Component, Input, OnInit } from '@angular/core';

import { CountryService } from '../../../../core/services/country.service';
import { IEnterprise } from 'libraries/AonSDK/src/aon';

export interface OptionsCountry {
  name: string;
  code: string;
}

export interface OptionsProvince {
  value: number;
  text: string;
}

@Component({
  selector: 'app-input-profile-company-information',
  templateUrl: './input-profile-company-information.component.html',
  styleUrls: ['./input-profile-company-information.component.scss'],
})
export class InputProfileCompanyInformationComponent implements OnInit {
  @Input() enterprise: IEnterprise | null = null;
  selectedCountry: OptionsCountry | null = null;
  optionsCountry: any[] = [];
  optionsProvince: any[] = [];

  constructor(
    private countryService: CountryService
  ) {
  }

  async ngOnInit() {
    this.loadCountriesProvinces();
  }

/**
 * Carga los datos de paises y provincias.
 *
 * @private
 * @async
 * @return {Promise<void>} - Se resuelve una vez que los datos se hayan cargado.
 */
  private async loadCountriesProvinces() {
    const countries = await this.countryService.getAllCountries();

    // Se prepara el array tal como lo espera el componente global
    if (countries) {
      this.optionsCountry = countries.map((country) => {
        return {
          text: country.name,
          value: country.name
        };
      });
    }

    if (this.enterprise?.Country === 'España') {
      const provinces = await this.countryService.getDataSpain('provinces');

    // Se prepara el array tal como lo espera el componente global
      if (provinces) {
        this.optionsProvince = provinces.map((province) => {
          return {
            text: province.name,
            value: province.name
          };
        });
      }

    }
  }

  onCountrySelection(event: any) {
    const countryValue = event.value;
  }

  showProvinceField(): boolean {
    return (
      this.selectedCountry !== null && this.selectedCountry.name === 'Spain'
    );
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
