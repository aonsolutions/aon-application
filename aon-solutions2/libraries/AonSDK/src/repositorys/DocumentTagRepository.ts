import { ErrorResponse, FilterBuilder } from "../aon";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { ApiDocumentTag, DocumentTag } from "../models/DocumentTag";
import { Collection } from "../utils/Collection";
import { BASE_URL, CREATE_SINGLE, GET_SINGLE, UPDATE_SINGLE } from "../utils/Environment";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";

export class APIDocumentTagSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<DocumentTag> {
    constructor() {
        super(new ApiDocumentTag(), DocumentTag);
    }

    async create(element: DocumentTag): Promise<DocumentTag> {
        let data = this.apiModel.parseDataToSend(element, CREATE_SINGLE);
        let response = await ApiHttpRequest.httpRequest(BASE_URL + this.apiModel.getUrl(CREATE_SINGLE), this.apiModel.getMethod(CREATE_SINGLE), {}, data);
        return this.apiModel.parseDataToReceive(response, GET_SINGLE);
    }

    async update(element: DocumentTag): Promise<DocumentTag> {
        let filter = new FilterBuilder();
        filter.addField('id', element.Key);
        let data = this.apiModel.parseDataToSend(element, UPDATE_SINGLE);
        let url = this.apiModel.getUrl(UPDATE_SINGLE, filter.getFilter());
        let method = this.apiModel.getMethod(UPDATE_SINGLE);
        let response = await ApiHttpRequest.httpRequest(BASE_URL + url, method, element, data)
        if(response) return element;
        throw new ErrorResponse('0199')
    }
}

export class APIDocumentTagMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<DocumentTag> {
    constructor() {
        super(new ApiDocumentTag(), DocumentTag);
    }

    async get(filter?: IFilter): Promise<ICollection<DocumentTag>> {
        let response = await ApiHttpRequest.get(BASE_URL + '/ms/api/company/banks', {}, {});
        let collection: ICollection<DocumentTag> = new Collection<DocumentTag>();
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element))
        })
        return collection;
    }
}