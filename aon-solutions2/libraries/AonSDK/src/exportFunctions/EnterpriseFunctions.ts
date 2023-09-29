import { EnterpriseFactory } from "../factorys/EnterpriseFactory";
import { IEnterprise, IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class EnterpriseFunctions {
    private singleObjectCrud = new EnterpriseFactory().createSingleObjectCrud();
    private enterpriseCollectionCrud = new EnterpriseFactory().createMultipleObjectCrud();
    private specificMethods = new EnterpriseFactory().createSpecificMethods();

    async getEnterpriseList(filter?: IFilter): Promise<IResponse<ICollection<IEnterprise>>> {
        return (await this.enterpriseCollectionCrud.getCollection(filter));
    }

    async getEnterprise(pkey: any): Promise<IResponse<IEnterprise>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    async createEnterprise(enterprise: IEnterprise): Promise<IResponse<IEnterprise>> {
        return (await this.singleObjectCrud.createElement(enterprise));
    }

    async deleteEnterprise(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }

    async updateEnterprise(enterprise: IEnterprise): Promise<IResponse<IEnterprise>> {
        return (await this.singleObjectCrud.updateElement(enterprise));
    }

    async getCurrentEntepriseData(): Promise<IResponse<IEnterprise>> {
        return (await this.specificMethods.getCurrentEnterpriseData());
    }

    async getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>> {
        return (await this.specificMethods.getCurrentEnterpriseRegistryData());
    }

    async updateCurrentEnterpriseData(enterprise: IEnterprise): Promise<IResponse<IEnterprise>> {
        return (await this.specificMethods.updateCurrentEnterpriseData(enterprise));
    }

    async updateCurrentEnterpriseRegistryData(enterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
        return (await this.specificMethods.updateCurrentEnterpriseRegistryData(enterprise));
    }
}