import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IDocumentTag } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { DocumentTag, ApiDocumentTag, StorableDocumentTag } from "../models/DocumentTag";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/constants";

export class DocumentTagFactory implements ISingleObjectCrudFactory<IDocumentTag>, IMultipleObjectCrudFactory<IDocumentTag> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocumentTag> {
        return new GenericSingleObjectCrud<DocumentTag>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<DocumentTag>(new ApiDocumentTag(), DocumentTag) :
            new GenericSingleObjectCrudRepository<DocumentTag>(new StorableDocumentTag(), DocumentTag)
            ),
            DocumentTag);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocumentTag> {
        return new GenericMultipleObjectCrud<DocumentTag>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<DocumentTag>(new ApiDocumentTag(), DocumentTag) :
            new GenericMultipleObjectCrudRepository<DocumentTag>(new StorableDocumentTag(), DocumentTag)
            ),
            DocumentTag);
    }
}