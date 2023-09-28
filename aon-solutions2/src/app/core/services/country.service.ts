import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICommunity, ICountry } from 'libraries/AonSDK/src/aon';
import { environment } from 'src/environments/environment';
import { ErrorService } from './error.service';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class CountryService extends CommonService {

  constructor(private http: HttpClient, private errorService: ErrorService) {
    super();
  }

  /**
  * Recupera todos los países de la API REST.
  *
  * @return {Promise<{ name: string, code: string }[]>} Una promesa que resuelve a un arreglo de objetos que contienen el nombre y código del país.
  * @throws {Error} Si no se pudo obtener ningún dato.
  */
  getAllCountries(): Promise<{ name: string, code: string }[]> {
    const url: string = environment.urlRestCountries+'/all';

    return this.http.get<ICountry[]>(url).toPromise()
      .then(data => {
        if (data.length === 0) {
          throw this.errorService.getError('0206');
        }

        return data.map(country => {
          const name = country.name.common;
          const code = country.cca2;

          return { name, code };
        }).sort((a, b) => a.name.localeCompare(b.name)); // Ordenar alfabéticamente por el campo 'name'
      });
  }

  /**
   * Obtiene datos de la API para España según el tipo especificado.
   * @param type El tipo de datos a obtener. Puede ser 'communities' para obtener comunidades autónomas, o 'provinces' para obtener provincias.
   * @returns Una promesa que se resuelve en un array de objetos con los datos solicitados.
   * @throws Un error si se proporciona un tipo no válido.
   */
  async getDataSpain(type: string): Promise<Array<{}>> {
    let typeSearch: string = '';

    switch (type) {
      case 'communities':
        typeSearch = 'Autonomous-region';
        break;
      case 'provinces':
        typeSearch = 'Province';
        break;
      default:
        throw this.errorService.getError('0206');
    }

    const url: string = environment.urlGobApi + `/${typeSearch}?_pageSize=200&_sort=label`;

    const response = await fetch(url);
    const result = await response.json();

    const data = result.result.items.map((item: ICommunity) => ({
      name: item.label
    }));

    return data;
  }




}
