import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { ITaxModel } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { TaxModel, ApiTaxModel, StorableTaxModel } from "../models/TaxModel";
import { APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository, APIGenericMultipleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { APITaxModelMultipleObjectCrudRepository } from "../repositorys/TaxModelRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { APIEnvironment } from "../utils/Environment";

export class TaxModelFactory implements ISingleObjectCrudFactory<ITaxModel>, IMultipleObjectCrudFactory<ITaxModel> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaxModel> {
        return new GenericSingleObjectCrud<TaxModel>(
            (APIEnvironment ?
            new APIGenericSingleObjectCrudRepository<TaxModel>(new ApiTaxModel(), TaxModel) :
            new GenericSingleObjectCrudRepository<TaxModel>(new StorableTaxModel(), TaxModel)
            ),
            TaxModel);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ITaxModel> {
        return new GenericMultipleObjectCrud<TaxModel>(
            (APIEnvironment ?
            new APITaxModelMultipleObjectCrudRepository() :
            new GenericMultipleObjectCrudRepository<TaxModel>(new StorableTaxModel(), TaxModel)
            ),
            TaxModel);
    }
}