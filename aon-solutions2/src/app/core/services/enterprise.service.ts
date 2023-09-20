import { Injectable } from '@angular/core';
import { EnterpriseFactory, ICollection, IEnterprise, IFilter, IRegistryEnterprise } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class EnterpriseService {

  private singleObjectCrud = new EnterpriseFactory().createSingleObjectCrud();
  private enterpriseCollectionCrud = new EnterpriseFactory().createMultipleObjectCrud();
  private specificMethods = new EnterpriseFactory().createSpecificMethods();

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

  async getCurrentEntepriseData(): Promise<IEnterprise> {
    return (await this.specificMethods.getCurrentEnterpriseData()).result;
  }

  async getCurrentEnterpriseRegistryData(): Promise<IRegistryEnterprise> {
    return (await this.specificMethods.getCurrentEnterpriseRegistryData()).result;
  }

  async updateCurrentEnterpriseData(enterprise: IEnterprise): Promise<IEnterprise> {
    return (await this.specificMethods.updateCurrentEnterpriseData(enterprise)).result;
  }

  async updateCurrentEnterpriseRegistryData(enterprise: IRegistryEnterprise): Promise<IRegistryEnterprise> {
    return (await this.specificMethods.updateCurrentEnterpriseRegistryData(enterprise)).result;
  }

}
