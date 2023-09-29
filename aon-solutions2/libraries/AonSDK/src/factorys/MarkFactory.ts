import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IMark } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IMarkSpecificMethods } from "../interfaces/serviceInterfaces";
import { Mark, ApiMark, StorableMark } from "../models/Mark";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { MarkSpecificMethods } from "../services/MarkService";
import { APIEnvironment } from "../utils/Environment";

export class MarkFactory implements ISingleObjectCrudFactory<IMark>, IMultipleObjectCrudFactory<IMark> {
    createSingleObjectCrud(): ISingleObjectCrud<IMark> {
        return new GenericSingleObjectCrud<Mark>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Mark>(new ApiMark(), Mark) :
            new GenericSingleObjectCrudRepository<Mark>(new StorableMark(), Mark)
            ),
            Mark);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMark> {
        return new GenericMultipleObjectCrud<Mark>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<Mark>(new ApiMark(), Mark) :
            new GenericMultipleObjectCrudRepository<Mark>(new StorableMark(), Mark)
            ),
            Mark);
    }
    createSpecificMethods(): IMarkSpecificMethods {
        return new MarkSpecificMethods();
    }
}