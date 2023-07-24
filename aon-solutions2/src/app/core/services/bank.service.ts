import { Injectable } from '@angular/core';
import { BankFactory, IBank, ICollection, IFilter } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class BankService {

  bankCollectionCrud = new BankFactory().createMultipleObjectCrud();

  constructor() {}

  async getBankList(filter?: IFilter): Promise<ICollection<IBank>> {
    return (await this.bankCollectionCrud.getCollection(filter)).result;
  }
  
}
