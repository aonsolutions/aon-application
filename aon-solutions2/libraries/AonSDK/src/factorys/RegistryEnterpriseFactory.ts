import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { RegistryEnterprise, StorableRegistryEnterprise } from "../models/RegistryEnterprise";
import { GenericMultipleObjectCrudRepository, GenericSingleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud, GenericSingleObjectCrud } from "../services/GenericCrudService";

export class RegistryEnterpriseFactory implements ISingleObjectCrudFactory<IRegistryEnterprise>, IMultipleObjectCrudFactory<IRegistryEnterprise> {
    createSingleObjectCrud(): ISingleObjectCrud<IRegistryEnterprise> {
        return new GenericSingleObjectCrud<RegistryEnterprise>(new GenericSingleObjectCrudRepository<RegistryEnterprise>(new StorableRegistryEnterprise(), RegistryEnterprise), RegistryEnterprise);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IRegistryEnterprise> {
        return new GenericMultipleObjectCrud<RegistryEnterprise>(new GenericMultipleObjectCrudRepository<RegistryEnterprise>(new StorableRegistryEnterprise(), RegistryEnterprise), RegistryEnterprise);
    }
  }