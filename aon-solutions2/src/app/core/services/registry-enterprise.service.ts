import { Injectable } from '@angular/core';
import { ICollection, IFilter, IRegistryEnterprise, RegistryEnterpriseFactory } from 'libraries/AonSDK/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class RegistryEnterpriseService extends CommonService {

  private singleObjectCrud = new RegistryEnterpriseFactory().createSingleObjectCrud();
  private multipleObjectCrud = new RegistryEnterpriseFactory().createMultipleObjectCrud();

  constructor() {
    super();
  }

  async getRegistryEnterpriseList(filter?: IFilter): Promise<ICollection<IRegistryEnterprise>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getRegistryEnterprise(pkey: any): Promise<IRegistryEnterprise> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createRegistryEnterprise(registryEnterprise: IRegistryEnterprise): Promise<IRegistryEnterprise> {
    return (await this.singleObjectCrud.createElement(registryEnterprise)).result;
  }

  async deleteRegistryEnterprise(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async updateRegistryEnterprise(registryEnterprise: IRegistryEnterprise): Promise<IRegistryEnterprise> {
    return (await this.singleObjectCrud.updateElement(registryEnterprise)).result;
  }
}
