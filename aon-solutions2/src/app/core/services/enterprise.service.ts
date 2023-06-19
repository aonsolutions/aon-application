import { Injectable } from '@angular/core';
import { Enterprise } from '../models/class/enterprise';
import { AonSDK } from 'libraries/AonSDK/AonSDK';

@Injectable({
  providedIn: 'root'
})
export class EnterpriseService {

  aonSDK: AonSDK = new AonSDK();

  constructor() { }

  getEnterprises(): Promise<Enterprise[]> {
    return new Promise(
      (resolve, reject) => {
        this.aonSDK.model('enterprise').getElementList('enterprise')
        .then(
          (response: any) => {
            resolve(response.result);
          }
        )
        .catch(
          (error:any) => {
            throw new Error(error.description + error.result)
          }
        )
    })

    // return this.apiService.get('company').pipe(
    //   map((res) => {
    //     for(let i = 0; i < res.length; i++){
    //       res[i] = new Enterprise().deserialize(res[i])
    //     }
    //     return res;
    //   }
    //   ),
    //   catchError((err, caught) => {
    //     //TODO: pendiente de ver como gestionar los errores, en cada llamada, en un interceptor, de forma general en los métodos de ApiService...
    //     throw new Error(err);
    //     console.log(err, caught)
    //     return EMPTY;
    //   })
    // );
  }

  setEnterprise(cif: string): Promise<boolean>{    
    return new Promise(
      (resolve, reject) => {
        this.aonSDK.model('enterprise').setEnterprise('enteprise', cif)
        .then(
          (response: any) => {
            resolve(response.result);
          }
        )
        .catch(
          (error:any) => {
            throw new Error(error.description + error.result)
          }
        )
    })
  }

}
