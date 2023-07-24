import { Injectable } from '@angular/core';
import { ICollection, IFilter, ITaxModel, TaxModelFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class TaxModelService {

  private multipleObjectCrud = new TaxModelFactory().createMultipleObjectCrud();

  constructor() {}

  async getTaxModelList(filter?: IFilter): Promise<ICollection<ITaxModel>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

}