import { Injectable } from '@angular/core';
import { EnterpriseFactory, ICollection, IEnterprise, IFilter } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class EnterpriseService {

  private singleObjectCrud = new EnterpriseFactory().createSingleObjectCrud();
  private enterpriseCollectionCrud = new EnterpriseFactory().createMultipleObjectCrud();

  constructor() {}

  async getEnterpriseList(filter?: IFilter): Promise<ICollection<IEnterprise>> {
    return (await this.enterpriseCollectionCrud.getCollection(filter)).result;
  }

  async getEnterprise(pkey: any): Promise<IEnterprise> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createEnterprise(enterprise: IEnterprise): Promise<IEnterprise> {
    return (await this.singleObjectCrud.createElement(enterprise)).result;
  }

  async deleteEnterprise(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async updateEnterprise(enterprise: IEnterprise): Promise<IEnterprise> {
    return (await this.singleObjectCrud.updateElement(enterprise)).result;
  }

}
