import { ICollection, IFilter, ITaxModel } from "../aon";
import { ApiTaxModel, TaxModel } from "../models/TaxModel";
import { Collection } from "../utils/Collection";
import { BASE_URL } from "../utils/Environment";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository } from "./GenericRepository";


export class APITaxModelMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<TaxModel> {

    constructor(){
        super(new ApiTaxModel(), TaxModel);
    }

    async get(filter?: IFilter): Promise<ICollection<TaxModel>> {
        let response = await ApiHttpRequest.get(BASE_URL + '/ms/api/fiscal/models', {}, {});
        let collection: ICollection<TaxModel> = new Collection<TaxModel>();
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element))
        })
        if(collection.size() > 0){
            if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
            if(filter?.orderBy) collection.sort(filter);
            if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        }
        return collection;
    }

}