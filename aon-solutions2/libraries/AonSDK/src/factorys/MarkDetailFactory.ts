import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IMarkDetail } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { MarkDetail, ApiMarkDetail, StorableMarkDetail } from "../models/MarkDetail";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/constants";

export class MarkDetailFactory implements ISingleObjectCrudFactory<IMarkDetail>, IMultipleObjectCrudFactory<IMarkDetail> {
    createSingleObjectCrud(): ISingleObjectCrud<IMarkDetail> {
        return new GenericSingleObjectCrud<MarkDetail>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<MarkDetail>(new ApiMarkDetail(), MarkDetail) :
            new GenericSingleObjectCrudRepository<MarkDetail>(new StorableMarkDetail(), MarkDetail)
            ),
            MarkDetail);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMarkDetail> {
        return new GenericMultipleObjectCrud<MarkDetail>(
            (APIEnvironment ?
            new APIGenericMultipleObjectCrudRepository<MarkDetail>(new ApiMarkDetail(), MarkDetail) :
            new GenericMultipleObjectCrudRepository<MarkDetail>(new StorableMarkDetail(), MarkDetail)
            ),
            MarkDetail);
    }
}