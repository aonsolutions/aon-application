import { ICollection, IFilter } from "../aon";
import { Bank, ApiBank } from "../models/Bank";
import { Collection } from "../utils/Collection";
import { BASE_URL } from "../utils/Environment";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository } from "./GenericRepository";


export class APIBankMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Bank> {
    constructor(){
        super(new ApiBank(), Bank);
    }

    async get(filter?: IFilter): Promise<ICollection<Bank>> {
        let response = await ApiHttpRequest.get(BASE_URL + '/ms/api/company/banks', {}, {});
        let collection: ICollection<Bank> = new Collection<Bank>();
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element))
        })
        return collection;
    }
}