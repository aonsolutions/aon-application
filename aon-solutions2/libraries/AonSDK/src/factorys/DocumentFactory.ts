import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IDocument } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IDocumentSpecificMethods } from "../interfaces/serviceInterfaces";
import { StorableDocument } from "../models/Document";
import { ApiDocumentMultipleObjectCrudRepository, ApiDocumentSingleObjectCrudRepository, ApiDocumentSpecificMethodsRepository, LocalDocumentSpecificMethodsRepository } from "../repositorys/DocumentRepository";
import { GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { DocumentSpecificMethods } from "../services/DocumentService";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";
import { Document } from "../models/Document";

export class DocumentFactory implements ISingleObjectCrudFactory<IDocument>, IMultipleObjectCrudFactory<IDocument> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocument> {
        return new GenericSingleObjectCrud<Document>(
            (APIEnvironment ?
            new ApiDocumentSingleObjectCrudRepository() :
            new GenericSingleObjectCrudRepository<Document>(new StorableDocument(), Document)
            ),
            Document);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocument> {
        return new GenericMultipleObjectCrud<Document>(
            (APIEnvironment ?
            new ApiDocumentMultipleObjectCrudRepository() :
            new GenericMultipleObjectCrudRepository<Document>(new StorableDocument(), Document)
            ),
            Document);
    }
    createDocumentSpecificMethods(): IDocumentSpecificMethods {
        return new DocumentSpecificMethods(APIEnvironment ? new ApiDocumentSpecificMethodsRepository() : new LocalDocumentSpecificMethodsRepository());
    }
}