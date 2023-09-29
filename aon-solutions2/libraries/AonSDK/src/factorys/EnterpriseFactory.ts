import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IEnterprise } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IEnterpriseSpecificMethods } from "../interfaces/serviceInterfaces";
import { Enterprise, ApiEnterprise, StorableEnterprise } from "../models/Enterprise";
import { ApiEnterpriseMultipleObjectCrudRepository, LocalEnterpriseSpecificMethodsRepository } from "../repositorys/EnterpriseRepository";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { EnterpriseSpecificMethods } from "../services/EnterpriseService";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";

export class EnterpriseFactory implements ISingleObjectCrudFactory<IEnterprise>, IMultipleObjectCrudFactory<IEnterprise> {
    createSingleObjectCrud(): ISingleObjectCrud<IEnterprise> {
        return new GenericSingleObjectCrud<Enterprise>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<Enterprise>(new ApiEnterprise(), Enterprise) :
            new GenericSingleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise)
            ),
            Enterprise);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IEnterprise> {
        return new GenericMultipleObjectCrud<Enterprise>(
            (APIEnvironment ?
            new ApiEnterpriseMultipleObjectCrudRepository() :
            new GenericMultipleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise)
            ),
            Enterprise);
    }
    createSpecificMethods(): IEnterpriseSpecificMethods {
        return new EnterpriseSpecificMethods(new LocalEnterpriseSpecificMethodsRepository());
    }
}