import { Injectable } from '@angular/core';
import { BankFactory, Factory, IBank, ICollection, IFilter } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class BankService extends CommonService{

  private bankCollectionCrud = new BankFactory().createMultipleObjectCrud();
  private bankCrud = new BankFactory().createSingleObjectCrud();

  constructor() {
    super();
  }

  async getBankList(filter?: IFilter): Promise<ICollection<IBank>> {
    return (await this.bankCollectionCrud.getCollection(filter)).result;
  }

  async createBank(bank: IBank): Promise<IBank> {
    return (await this.bankCrud.createElement(bank)).result;
  }

  async updateBank(bank: IBank): Promise<IBank> {
    return (await this.bankCrud.updateElement(bank)).result;
  }

  async deleteBank(pkey: any): Promise<boolean> {
    return (await this.bankCrud.deleteElement(pkey)).result;
  }

  async getBank(pkey: any): Promise<IBank> {
    return (await this.bankCrud.getElement(pkey)).result;
  }
  
}
