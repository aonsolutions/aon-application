import { Injectable } from '@angular/core';
import { ICollection, IFilter, ITaxModel, TaxModelFactory, statusTaxModel } from 'libraries/AonSDK/aon';

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

  async createTaxModel(taxModel: ITaxModel): Promise<ITaxModel> {
    return (await this.singleObjectCrud.createElement(taxModel)).result;
  }

  async updateTaxModel(taxModel: ITaxModel): Promise<ITaxModel> {
    return (await this.singleObjectCrud.updateElement(taxModel)).result;
  }

  async deleteTaxModel(key: string): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(key)).result;
  }

  async markTaxModelAsPaid(key: string, status: statusTaxModel): Promise<ITaxModel> {
    const taxModel = await this.getTax(key);
    taxModel.Status = status;

    return this.updateTaxModel(taxModel);
  }


}
