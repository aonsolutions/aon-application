import { Component, OnInit } from '@angular/core';
import { EnterpriseService } from 'src/app/core/services/enterprise.service';
import { CountryService } from '../../../../core/services/country.service';

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
  enterprises: any[] = [];
  selectedCountry: OptionsCountry | null = null;
  optionsCountry = this.countryService.getAllCountries();



  // optionsCountry: OptionsCountry[] = [
  //   { text: 'Alemania' },
  //   { text: 'Austria' },
  //   { text: 'Bélgica' },
  //   { text: 'Bulgaria' },
  //   { text: 'Chipre' },
  //   { text: 'Croacia' },
  //   { text: 'Dinamarca' },
  //   { text: 'Eslovaquia' },
  //   { text: 'Eslovenia' },
  //   { text: 'España' },
  //   { text: 'Estonia' },
  //   { text: 'Finlandia' },
  //   { text: 'Francia' },
  //   { text: 'Grecia' },
  //   { text: 'Hungría' },
  //   { text: 'Irlanda' },
  //   { text: 'Italia' },
  //   { text: 'Letonia' },
  //   { text: 'Lituania' },
  //   { text: 'Luxemburgo' },
  //   { text: 'Malta' },
  //   { text: 'Países Bajos' },
  //   { text: 'Polonia' },
  //   { text: 'Portugal' },
  //   { text: 'República Checa' },
  //   { text: 'Rumania' },
  //   { text: 'Suecia' },
  // ];

  // optionsProvince: OptionsProvince[] = [
  //   { value: 1, text: 'Álava' },
  //   { value: 2, text: 'Albacete' },
  //   { value: 3, text: 'Alicante' },
  //   { value: 4, text: 'Almería' },
  //   { value: 5, text: 'Asturias' },
  //   { value: 6, text: 'Ávila' },
  //   { value: 7, text: 'Badajoz' },
  //   { value: 8, text: 'Barcelona' },
  //   { value: 9, text: 'Burgos' },
  //   { value: 10, text: 'Cáceres' },
  //   { value: 11, text: 'Cádiz' },
  //   { value: 12, text: 'Cantabria' },
  //   { value: 13, text: 'Castellón' },
  //   { value: 14, text: 'Ceuta' },
  //   { value: 15, text: 'Ciudad Real' },
  //   { value: 16, text: 'Córdoba' },
  //   { value: 17, text: 'Cuenca' },
  //   { value: 18, text: 'Gerona' },
  //   { value: 19, text: 'Granada' },
  //   { value: 20, text: 'Guadalajara' },
  //   { value: 21, text: 'Guipúzcoa' },
  //   { value: 22, text: 'Huelva' },
  //   { value: 23, text: 'Huesca' },
  //   { value: 24, text: 'Islas Baleares' },
  //   { value: 25, text: 'Jaén' },
  //   { value: 26, text: 'La Coruña' },
  //   { value: 27, text: 'La Rioja' },
  //   { value: 28, text: 'Las Palmas' },
  //   { value: 29, text: 'León' },
  //   { value: 30, text: 'Lérida' },
  //   { value: 31, text: 'Lugo' },
  //   { value: 32, text: 'Madrid' },
  //   { value: 33, text: 'Málaga' },
  //   { value: 34, text: 'Melilla' },
  //   { value: 35, text: 'Murcia' },
  //   { value: 36, text: 'Navarra' },
  //   { value: 37, text: 'Orense' },
  //   { value: 38, text: 'Palencia' },
  //   { value: 39, text: 'Pontevedra' },
  //   { value: 40, text: 'Salamanca' },
  //   { value: 41, text: 'Santa Cruz de Tenerife' },
  //   { value: 42, text: 'Segovia' },
  //   { value: 43, text: 'Sevilla' },
  //   { value: 44, text: 'Soria' },
  //   { value: 45, text: 'Tarragona' },
  //   { value: 46, text: 'Teruel' },
  //   { value: 47, text: 'Toledo' },
  //   { value: 48, text: 'Valencia' },
  //   { value: 49, text: 'Valladolid' },
  //   { value: 50, text: 'Vizcaya' },
  //   { value: 51, text: 'Zamora' },
  //   { value: 52, text: 'Zaragoza' },
  // ];

  constructor(
    private enterpriseService: EnterpriseService,
    private countryService: CountryService
  ) {
    this.enterpriseService.getEnterprise('B16880148').then((enterprise) => {

      this.enterprises.push(enterprise);
      console.log("prueba", this.enterprises);
    });
    console.log('texto2' + this.enterprises);
    console.log("optionsCountry", this.optionsCountry);
  }

  async ngOnInit() {
  }

  onCountrySelection(event: any) {
    const countryValue = event.value;
  }

  showProvinceField(): boolean {
    return (
      this.selectedCountry !== null && this.selectedCountry.name === 'España'
    );
  }
}
