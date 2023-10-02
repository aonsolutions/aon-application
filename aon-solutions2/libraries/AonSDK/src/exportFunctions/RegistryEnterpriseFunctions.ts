import { RegistryEnterpriseFactory } from "../factorys/RegistryEnterpriseFactory";
import { IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class RegistryEnterpriseFunctions {
    private static singleObjectCrud = new RegistryEnterpriseFactory().createSingleObjectCrud();
    private static multipleObjectCrud = new RegistryEnterpriseFactory().createMultipleObjectCrud();
  
    static async getRegistryEnterpriseList(filter?: IFilter): Promise<IResponse<ICollection<IRegistryEnterprise>>> {
      return (await this.multipleObjectCrud.getCollection(filter));
    }
  
    static async getRegistryEnterprise(pkey: any): Promise<IResponse<IRegistryEnterprise>> {
      return (await this.singleObjectCrud.getElement(pkey));
    }
  
    static async createRegistryEnterprise(registryEnterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
      return (await this.singleObjectCrud.createElement(registryEnterprise));
    }
  
    static async deleteRegistryEnterprise(pkey: any): Promise<IResponse<boolean>> {
      return (await this.singleObjectCrud.deleteElement(pkey));
    }
  
    static async updateRegistryEnterprise(registryEnterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
      return (await this.singleObjectCrud.updateElement(registryEnterprise));
    }
}