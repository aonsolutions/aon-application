import { Injectable } from '@angular/core';
import { AonSDK, Filter } from 'libraries/AonSDK/AonSDK';
import { Bank } from '../models/class/bank';

@Injectable({
  providedIn: 'root',
})
export class BankService {
  private aonSDK = new AonSDK();

  constructor() {}

  getBankList(filter?: Filter): Promise<Bank[]> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('bank')
        .getElementList('bank')
        .then((response: any) => {
          resolve(new Bank().deserializeArray(response.result));
        })
        .catch((error: any) => {
          throw new Error(error.description + ' - ' + error.result);
        });
    });
  }
}
