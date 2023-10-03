import { IEnterprise, IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { IEnterpriseSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { Enterprise, ApiEnterprise, StorableEnterprise } from "../models/Enterprise";
import { RegistryEnterprise, StorableRegistryEnterprise } from "../models/RegistryEnterprise";
import { Collection } from "../utils/Collection";
import { BASE_URL, GET_MULTIPLE, GET_SINGLE } from "../utils/Environment";
import { enterprises, registryEnterprises } from "../utils/GenerateFakeData";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository } from "./GenericRepository";

export class ApiEnterpriseMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Enterprise> {
    constructor(){
        super(new ApiEnterprise(), Enterprise);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Enterprise>> {
        let collection: ICollection<Enterprise> = new Collection<Enterprise>();
        let response = await ApiHttpRequest.get(BASE_URL + '/ms/api/company', {}, {})
        response.forEach((element: any) => {
            console.log(this.apiModel.parseDataToReceive(element, GET_MULTIPLE, filter))
            collection.add(this.apiModel.parseDataToReceive(element, GET_MULTIPLE, filter))
        })
        collection.forEach((element: Enterprise) => {
            if(element.Key == ''){
                collection.remove(element.getKey());
            }
        })
        return collection;
    }
}

export class ApiEnterpriseSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Enterprise> {
    constructor(){
        super(new ApiEnterprise(), Enterprise);
    }

    async get(key: string, type?: string | undefined): Promise<Enterprise> {
        let response: IEnterprise = await ApiHttpRequest.get('/ms/api/company/one', {}, {})    
        return this.apiModel.parseDataToReceive(response, GET_SINGLE);
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