import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IDocument } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IDocumentSpecificMethods } from "../interfaces/serviceInterfaces";
import { ApiDocument, StorableDocument } from "../models/Document";
import { ApiDocumentSpecificMethodsRepository, LocalDocumentSpecificMethodsRepository } from "../repositorys/DocumentRepository";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { DocumentSpecificMethods } from "../services/DocumentService";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";
import { Document } from "../models/Document";

export class DocumentFactory implements ISingleObjectCrudFactory<IDocument>, IMultipleObjectCrudFactory<IDocument> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocument> {
        return new GenericSingleObjectCrud<Document>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Document>(new ApiDocument(), Document) :
            new GenericSingleObjectCrudRepository<Document>(new StorableDocument(), Document)
            ),
            Document);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocument> {
        return new GenericMultipleObjectCrud<Document>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<Document>(new ApiDocument(), Document) :
            new GenericMultipleObjectCrudRepository<Document>(new StorableDocument(), Document)
            ),
            Document);
    }
    createDocumentSpecificMethods(): IDocumentSpecificMethods {
        return new DocumentSpecificMethods(APIEnvironment ? new ApiDocumentSpecificMethodsRepository() : new LocalDocumentSpecificMethodsRepository());
    }
}