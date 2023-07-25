import { Injectable } from '@angular/core';
import { EnterpriseFactory, ICollection, IEnterprise, IFilter } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class EnterpriseService {

  enterpriseCollectionCrud = new EnterpriseFactory().createMultipleObjectCrud();

  constructor() {}

  async getEnterpriseList(filter?: IFilter): Promise<ICollection<IEnterprise>> {
    return (await this.enterpriseCollectionCrud.getCollection(filter)).result;
  }

}
