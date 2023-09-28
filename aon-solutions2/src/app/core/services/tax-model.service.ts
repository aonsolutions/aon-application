import { Injectable } from '@angular/core';
import { ICollection, IFilter, ITaxModel, TaxModelFactory, statusTaxModel } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class TaxModelService extends CommonService {

  private multipleObjectCrud  = new TaxModelFactory().createMultipleObjectCrud();
  private singleObjectCrud    = new TaxModelFactory().createSingleObjectCrud();

  constructor() {
    super();
  }

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

  // En el trimestre que estamos
  // Restorna:
  //  Trimestre 1
  //  Trimestre 2
  //  Trimestre 3
  //  Trimestre 4
  async thisTrimester() {
    let trimester : number = -1;
    const currentDate   = new Date();
    const currentMonth  = currentDate.getMonth() + 1;
    const currentYear   = currentDate.getFullYear();

    if (currentMonth >= 2 && currentMonth <= 4) {
      // Trimestre 1
      trimester = 1;
    } else if (currentMonth >= 5 && currentMonth <= 7) {
      // Trimestre 2
      trimester = 2;
    } else if (currentMonth >= 8 && currentMonth <= 10) {
      // Trimestre  3
      trimester = 3;
    } else if (currentMonth > 10 || currentMonth < 2) {
      // Trimestre 4
      trimester = 4;
    }
    return trimester;
  }
  
}
