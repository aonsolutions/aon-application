import { IBank, ICollection, IFilter, ITaxModel } from "../aon";
import { IModel } from "../interfaces/modelsInterfaces";
import { ITaxModelSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
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
export class ApiTaxModelSpecificMethodsRepository implements ITaxModelSpecificMethodsRepository {
    payTaxModelWithNRC(model: IModel, nrc: string): Promise<boolean> {
        throw new Error("Method not implemented.");
    }
    payTaxModelWithBank(model: IModel, bank: IBank): Promise<boolean> {
        throw new Error("Method not implemented.");
    }
} 
export class LocalTaxModelSpecificMethosdsRepository implements ITaxModelSpecificMethodsRepository{
    payTaxModelWithNRC(model: IModel, nrc: string): Promise<boolean> {
        throw new Error("Method not implemented.");
    }
    payTaxModelWithBank(model: IModel, bank: IBank): Promise<boolean> {
        throw new Error("Method not implemented.");
    }
}