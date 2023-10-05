import { IBank, ICollection, IFilter, ITaxModel } from "../aon";
import { IModel, statusTaxModel } from "../interfaces/modelsInterfaces";
import { ITaxModelSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { ApiTaxModel, StorableTaxModel, TaxModel } from "../models/TaxModel";
import { TAXMODEL_URL } from "../utils/ApiUrls";
import { Collection } from "../utils/Collection";
import { BASE_URL } from "../utils/Environment";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";


export class APITaxModelSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<TaxModel> {

    constructor(){
        super(new ApiTaxModel(), TaxModel);
    }

    async get(key: string): Promise<TaxModel> {
        let response = await ApiHttpRequest.get(BASE_URL + TAXMODEL_URL.GET_TAXMODEL_LIST, {}, {});
        let collection: ICollection<TaxModel> = new Collection<TaxModel>();
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element))
        })
        return collection.get(key);
    }

}

export class APITaxModelMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<TaxModel> {

    constructor(){
        super(new ApiTaxModel(), TaxModel);
    }

    async get(filter?: IFilter): Promise<ICollection<TaxModel>> {
        let response = await ApiHttpRequest.get(BASE_URL + TAXMODEL_URL.GET_TAXMODEL_LIST, {}, {});
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

    async payTaxModelWithNRC(model: TaxModel, nrc: string): Promise<boolean> {
        let json = model.ApiObject;
        Object.defineProperty(json, 'nrc', {
            value: nrc,
            writable: false
        })
        let response = await ApiHttpRequest.post(BASE_URL + TAXMODEL_URL.PAY_TAXMODEL, {}, json);
        if(response) return true;
        else throw new Error('Error al pagar el modelo fiscal')
    }

    async payTaxModelWithBank(model: TaxModel, bank: IBank): Promise<boolean> {
        let json = model.ApiObject;
        Object.defineProperty(json, 'iban', {
            value: bank.Iban,
            enumerable: true,
            writable: false
        })
        let response = await ApiHttpRequest.post(BASE_URL + TAXMODEL_URL.PAY_TAXMODEL, {}, json);
        if(response) return true;
        else throw new Error('Error al pagar el modelo fiscal')
    }

} 
export class LocalTaxModelSpecificMethosdsRepository implements ITaxModelSpecificMethodsRepository{

    private storable = new StorableTaxModel();

    async payTaxModelWithNRC(model: ITaxModel, nrc: string): Promise<boolean> {
        this.storable.getCollection().remove(model.getKey())
        model.Status = statusTaxModel.CONFIRMADO
        this.storable.getCollection().add(model as TaxModel);
        return true;
    }
    async payTaxModelWithBank(model: ITaxModel, bank: IBank): Promise<boolean> {
        this.storable.getCollection().remove(model.getKey())
        model.Status = statusTaxModel.CONFIRMADO
        this.storable.getCollection().add(model as TaxModel);
        return true;
    }
}