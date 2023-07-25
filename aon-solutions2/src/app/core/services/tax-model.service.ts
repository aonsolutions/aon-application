import { Injectable } from '@angular/core';
import { ICollection, IFilter, ITaxModel, TaxModelFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class TaxModelService {

  private multipleObjectCrud  = new TaxModelFactory().createMultipleObjectCrud();
  private singleObjectCrud    = new TaxModelFactory().createSingleObjectCrud();

  constructor() {}

  async getTaxModelList(filter?: IFilter): Promise<ICollection<ITaxModel>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }
  
  async getTax(key : string): Promise<ITaxModel> {
    return (await this.singleObjectCrud.getElement(key)).result;
  }

}