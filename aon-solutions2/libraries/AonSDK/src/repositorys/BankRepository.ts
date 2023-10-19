import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { ErrorResponse } from "../utils/Response";
import { Bank, ApiBank } from "../models/Bank";
import { Collection } from "../utils/Collection";
import { BASE_URL } from "../utils/Environment";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";
import { BANK_URL, REGISTRY_URL } from "../utils/ApiUrls";
import { FilterBuilder } from "../utils/FilterBuilder";


export class APIBankMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Bank> {
    constructor(){
        super(new ApiBank(), Bank);
    }

    async get(filter?: IFilter): Promise<ICollection<Bank>> {
        let response = await ApiHttpRequest.get(BASE_URL + BANK_URL.GET_BANK_LIST, {}, {});
        let collection: ICollection<Bank> = new Collection<Bank>();
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element))
        })
        let active = new FilterBuilder();
        active.addField('active', 'true');
        return collection.filter(active.getFilter());
    }
}

export class APIBankSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Bank> {
    constructor(){
        super(new ApiBank(), Bank);
    }

    async create(element: Bank): Promise<Bank> {
        let fullRegistry = await ApiHttpRequest.get(BASE_URL + REGISTRY_URL.GET_FULL_REGISTRY, {}, {})
        fullRegistry.banks = []
        delete fullRegistry.addresses;
        delete fullRegistry.media;
        delete fullRegistry.paymethod;
        delete fullRegistry.record_data;
        let jsonBank = {
            account: {
                alias: "",
                code: "",
                description: "",
                domain: +localStorage.getItem('domainId')!
            },
            active: true,
            alias: element.Name,
            bank: "",
            bank_account: element.Iban,
            bic: element.SwiftBic,
            dirty: true,
            domain: +localStorage.getItem('domainId')!,
            fullName: element.Iban + " - " + "",
            iban: element.Iban,
            registry: fullRegistry.id,
            removed: false,
            sufix: ""
        }
        fullRegistry.banks.push(jsonBank);
        let response = await ApiHttpRequest.put(BASE_URL + REGISTRY_URL.SAVE_FULL_REGISTRY, {}, fullRegistry);
        if(!response.error)
            return element;
        else
            throw new ErrorResponse('0201');
    }
}