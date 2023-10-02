import { TaxModelFactory } from "../factorys/TaxModelFactory";
import { ITaxModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class TaxModelFunctions {
    private static multipleObjectCrud  = new TaxModelFactory().createMultipleObjectCrud();
    private static singleObjectCrud    = new TaxModelFactory().createSingleObjectCrud();
  
    static async getTaxModelList(filter?: IFilter): Promise<IResponse<ICollection<ITaxModel>>> {
      return (await this.multipleObjectCrud.getCollection(filter));
    }
  
    static async getTaxModel(key : string): Promise<IResponse<ITaxModel>> {
      return (await this.singleObjectCrud.getElement(key));
    }
}