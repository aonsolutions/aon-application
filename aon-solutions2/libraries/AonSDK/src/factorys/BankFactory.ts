import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IBank } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { Bank, ApiBank, StorableBank } from "../models/Bank";
import { APIBankMultipleObjectCrudRepository } from "../repositorys/BankRepository";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";

export class BankFactory implements ISingleObjectCrudFactory<IBank>, IMultipleObjectCrudFactory<IBank> {
    createSingleObjectCrud(): ISingleObjectCrud<IBank> {
        return new GenericSingleObjectCrud<Bank>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Bank>(new ApiBank(), Bank) :
            new GenericSingleObjectCrudRepository<Bank>(new StorableBank(), Bank)
            ),
            Bank);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IBank> {
        return new GenericMultipleObjectCrud<Bank>(
            (APIEnvironment ?
            new APIBankMultipleObjectCrudRepository():
            new GenericMultipleObjectCrudRepository<Bank>(new StorableBank(), Bank)
            ),
            Bank);
    }
}