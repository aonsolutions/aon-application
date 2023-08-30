import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ICountry } from 'libraries/AonSDK/aon';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CountryService {

  private baseUrl = environment.urlRestCounties;

  constructor(private http: HttpClient) { }

  getAllCountries(url: string = `${this.baseUrl}/all`): Promise<{ name: string, code: string }[]> {

    return this.http.get<ICountry[]>(url).toPromise()
      .then(data => {
        return data.map(country => {
          const name = country.name.common;
          const code = country.cca2;
          return { name, code };
        });
      });
  }
}
