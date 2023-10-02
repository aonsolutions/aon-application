import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IContract } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { Contract, StorableContract } from "../models/Contract";
import { GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ContractFactory implements ISingleObjectCrudFactory<IContract>, IMultipleObjectCrudFactory<IContract> {
    createSingleObjectCrud(): ISingleObjectCrud<IContract> {
        return new GenericSingleObjectCrud<Contract>(new GenericSingleObjectCrudRepository<Contract>(new StorableContract(), Contract), Contract);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IContract> {
        return new GenericMultipleObjectCrud<Contract>(new GenericMultipleObjectCrudRepository<Contract>(new StorableContract(), Contract), Contract);
    }

}