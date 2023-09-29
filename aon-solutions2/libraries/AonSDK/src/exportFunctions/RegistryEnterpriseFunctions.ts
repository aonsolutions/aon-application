import { RegistryEnterpriseFactory } from "../factorys/RegistryEnterpriseFactory";
import { IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class RegistryEnterpriseFunctions {
    private singleObjectCrud = new RegistryEnterpriseFactory().createSingleObjectCrud();
    private multipleObjectCrud = new RegistryEnterpriseFactory().createMultipleObjectCrud();
  
    async getRegistryEnterpriseList(filter?: IFilter): Promise<IResponse<ICollection<IRegistryEnterprise>>> {
      return (await this.multipleObjectCrud.getCollection(filter));
    }
  
    async getRegistryEnterprise(pkey: any): Promise<IResponse<IRegistryEnterprise>> {
      return (await this.singleObjectCrud.getElement(pkey));
    }
  
    async createRegistryEnterprise(registryEnterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
      return (await this.singleObjectCrud.createElement(registryEnterprise));
    }
  
    async deleteRegistryEnterprise(pkey: any): Promise<IResponse<boolean>> {
      return (await this.singleObjectCrud.deleteElement(pkey));
    }
  
    async updateRegistryEnterprise(registryEnterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
      return (await this.singleObjectCrud.updateElement(registryEnterprise));
    }
}