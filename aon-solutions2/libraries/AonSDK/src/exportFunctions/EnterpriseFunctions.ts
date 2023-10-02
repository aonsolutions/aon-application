import { EnterpriseFactory } from "../factorys/EnterpriseFactory";
import { IEnterprise, IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class EnterpriseFunctions {
    private static singleObjectCrud = new EnterpriseFactory().createSingleObjectCrud();
    private static enterpriseCollectionCrud = new EnterpriseFactory().createMultipleObjectCrud();
    private static specificMethods = new EnterpriseFactory().createSpecificMethods();

    static async getEnterpriseList(filter?: IFilter): Promise<IResponse<ICollection<IEnterprise>>> {
        return (await this.enterpriseCollectionCrud.getCollection(filter));
    }

    static async getEnterprise(pkey: any): Promise<IResponse<IEnterprise>> {
        return (await this.singleObjectCrud.getElement(pkey));
    }

    static async createEnterprise(enterprise: IEnterprise): Promise<IResponse<IEnterprise>> {
        return (await this.singleObjectCrud.createElement(enterprise));
    }

    static async deleteEnterprise(pkey: any): Promise<IResponse<boolean>> {
        return (await this.singleObjectCrud.deleteElement(pkey));
    }

    static async updateEnterprise(enterprise: IEnterprise): Promise<IResponse<IEnterprise>> {
        return (await this.singleObjectCrud.updateElement(enterprise));
    }

    static async getCurrentEntepriseData(): Promise<IResponse<IEnterprise>> {
        return (await this.specificMethods.getCurrentEnterpriseData());
    }

    static async getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>> {
        return (await this.specificMethods.getCurrentEnterpriseRegistryData());
    }

    static async updateCurrentEnterpriseData(enterprise: IEnterprise): Promise<IResponse<IEnterprise>> {
        return (await this.specificMethods.updateCurrentEnterpriseData(enterprise));
    }

    static async updateCurrentEnterpriseRegistryData(enterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
        return (await this.specificMethods.updateCurrentEnterpriseRegistryData(enterprise));
    }
}