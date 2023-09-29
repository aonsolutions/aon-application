import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IFolder } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { Folder, ApiFolder, StorableFolder } from "../models/Folder";
import { APIFolderMultipleObjectCrudRepository } from "../repositorys/FolderRepository";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";

export class FolderFactory implements ISingleObjectCrudFactory<IFolder>, IMultipleObjectCrudFactory<IFolder> {
    createSingleObjectCrud(): ISingleObjectCrud<IFolder> {
        return new GenericSingleObjectCrud<Folder>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Folder>(new ApiFolder(), Folder) :
            new GenericSingleObjectCrudRepository<Folder>(new StorableFolder(), Folder)
            ),
            Folder);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IFolder> {
        return new GenericMultipleObjectCrud<Folder>(
            (APIEnvironment ?
            new APIFolderMultipleObjectCrudRepository() :
            new GenericMultipleObjectCrudRepository<Folder>(new StorableFolder(), Folder)
            ),
            Folder);
    }
}