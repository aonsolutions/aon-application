import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IRegistryEnterprise } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";

export class RegistryEnterpriseFactory implements ISingleObjectCrudFactory<IRegistryEnterprise>, IMultipleObjectCrudFactory<IRegistryEnterprise> {
    createSingleObjectCrud(): ISingleObjectCrud<IRegistryEnterprise> {
        throw new Error('Method not implemented.');
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IRegistryEnterprise> {
        throw new Error('Method not implemented.');
    }
  }