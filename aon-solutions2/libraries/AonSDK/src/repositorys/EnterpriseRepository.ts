import { IEnterprise, IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { IEnterpriseSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Enterprise, ApiEnterprise, StorableEnterprise } from "../models/Enterprise";
import { RegistryEnterprise, StorableRegistryEnterprise } from "../models/RegistryEnterprise";
import { enterprises, registryEnterprises } from "../utils/GenerateFakeData";
import { APIGenericMultipleObjectCrudRepository, GenericSingleObjectCrudRepository } from "./GenericRepository";

export class ApiEnterpriseMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Enterprise> {
    constructor(){
        super(new ApiEnterprise(), Enterprise);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Enterprise>> {
        let collection: ICollection<Enterprise> = await super.get(filter)
        collection.forEach((element: Enterprise) => {
            if(element.Key == ''){
                collection.remove(element.getKey());
            }
        })
        return collection;
    }
}

export class LocalEnterpriseSpecificMethodsRepository implements IEnterpriseSpecificMethodsRepository {
    async getCurrentEnterpriseData(): Promise<IEnterprise> {
        return enterprises.get(localStorage.getItem('enterprise') || '');
    }

    async updateCurrentEntepriseData(enterprise: Enterprise): Promise<IEnterprise> {
        return  new GenericSingleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise).update(enterprise);
    }

    async getCurrentEnterpriseRegistryData(): Promise<IRegistryEnterprise> {
        return registryEnterprises.get(localStorage.getItem('enterprise') || '');
    }

    async updateCurrentEnterpriseRegistryData(registryEnterprise: RegistryEnterprise): Promise<IRegistryEnterprise> {
        return  new GenericSingleObjectCrudRepository<RegistryEnterprise>(new StorableRegistryEnterprise(), RegistryEnterprise).update(registryEnterprise);
    }
}