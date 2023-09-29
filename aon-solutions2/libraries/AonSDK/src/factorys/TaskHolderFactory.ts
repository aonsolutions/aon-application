import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { ITaskHolder } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { TaskHolder, ApiTaskHolder, StorableTaskHolder } from "../models/TaskHolder";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";

export class TaskHolderFactory implements ISingleObjectCrudFactory<ITaskHolder>, IMultipleObjectCrudFactory<ITaskHolder> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaskHolder> {
        return new GenericSingleObjectCrud<TaskHolder>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<TaskHolder>(new ApiTaskHolder(), TaskHolder) :
            new GenericSingleObjectCrudRepository<TaskHolder>(new StorableTaskHolder(), TaskHolder)
            ),
            TaskHolder);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ITaskHolder> {
        return new GenericMultipleObjectCrud<TaskHolder>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<TaskHolder>(new ApiTaskHolder(), TaskHolder) :
            new GenericMultipleObjectCrudRepository<TaskHolder>(new StorableTaskHolder(), TaskHolder)
            ),
            TaskHolder);
    }
}