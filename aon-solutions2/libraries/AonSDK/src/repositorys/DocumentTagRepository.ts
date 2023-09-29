import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { ApiDocumentTag, DocumentTag } from "../models/DocumentTag";
import { Collection } from "../utils/Collection";
import { BASE_URL } from "../utils/Environment";
import { ApiHttpRequest } from "../utils/Http";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";

export class APIDocumentTagSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<DocumentTag> {
    constructor() {
        super(new ApiDocumentTag(), DocumentTag);
    }

    async create(element: DocumentTag): Promise<DocumentTag> {
        throw new Error("Method not implemented.");
    }

    async update(element: DocumentTag): Promise<DocumentTag> {
        throw new Error("Method not implemented.");
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