import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { ITaxModel } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, ITaxModelSpecificMethods } from "../interfaces/serviceInterfaces";
import { TaxModel, StorableTaxModel } from "../models/TaxModel";
import { GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { APITaxModelMultipleObjectCrudRepository, APITaxModelSingleObjectCrudRepository, ApiTaxModelSpecificMethodsRepository, LocalTaxModelSpecificMethosdsRepository } from "../repositorys/TaxModelRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { TaxModelSpecificMethods } from "../services/TaxModelService";
import { APIEnvironment } from "../utils/Environment";

export class TaxModelFactory implements ISingleObjectCrudFactory<ITaxModel>, IMultipleObjectCrudFactory<ITaxModel> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaxModel> {
        return new GenericSingleObjectCrud<TaxModel>(
            (APIEnvironment ?
            new APITaxModelSingleObjectCrudRepository() :
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
    createSpecificMethods(): ITaxModelSpecificMethods{
        return new TaxModelSpecificMethods(APIEnvironment ?
            new ApiTaxModelSpecificMethodsRepository() : 
            new LocalTaxModelSpecificMethosdsRepository()
        )
    }
}