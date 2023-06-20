import { Injectable } from '@angular/core';
import { AonSDK, Optional } from 'libraries/AonSDK/AonSDK';
import { TaxModel } from '../models/class/tax-model';

@Injectable({
  providedIn: 'root'
})
export class TaxModelService {

  private aonSDK = new AonSDK();

  constructor() { }

  getTaxModels(optional?: Optional) {
    return new Promise(
      (resolve, reject) => {
        this.aonSDK.model('taxmodel').getElementList('taxmodel', optional)
        .then(
          (response: any) => {
            resolve(new TaxModel().deserializeArray(response.result));
          }
        )
        .catch(
          (error:any) => {
            throw new Error(error.description + ' - ' + error.result)
          }
        )
    })
  }

}
