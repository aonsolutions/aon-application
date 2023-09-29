import { TaxModelFactory } from "../factorys/TaxModelFactory";
import { ITaxModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class TaxModelFunctions {
    private multipleObjectCrud  = new TaxModelFactory().createMultipleObjectCrud();
    private singleObjectCrud    = new TaxModelFactory().createSingleObjectCrud();
  
    async getTaxModelList(filter?: IFilter): Promise<IResponse<ICollection<ITaxModel>>> {
      return (await this.multipleObjectCrud.getCollection(filter));
    }
  
    async getTaxModel(key : string): Promise<IResponse<ITaxModel>> {
      return (await this.singleObjectCrud.getElement(key));
    }
}