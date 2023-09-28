import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IDocumentNote } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { DocumentNote, ApiDocumentNote, StorableDocumentNote } from "../models/DocumentNote";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/constants";

export class DocumenNoteFactory implements ISingleObjectCrudFactory<IDocumentNote>, IMultipleObjectCrudFactory<IDocumentNote> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocumentNote> {
        return new GenericSingleObjectCrud<DocumentNote>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<DocumentNote>(new ApiDocumentNote(), DocumentNote) :
            new GenericSingleObjectCrudRepository<DocumentNote>(new StorableDocumentNote(), DocumentNote)
            ),
            DocumentNote);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocumentNote> {
        return new GenericMultipleObjectCrud<DocumentNote>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<DocumentNote>(new ApiDocumentNote(), DocumentNote) :
            new GenericMultipleObjectCrudRepository<DocumentNote>(new StorableDocumentNote(), DocumentNote)
            ),
            DocumentNote);
    }
}