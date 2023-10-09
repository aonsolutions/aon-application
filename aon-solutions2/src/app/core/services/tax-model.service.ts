import { Injectable } from '@angular/core';
import { IBank, ICollection, IFilter, ITaxModel, TaxModelFactory, statusTaxModel } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class TaxModelService extends CommonService {

  private multipleObjectCrud  = new TaxModelFactory().createMultipleObjectCrud();
  private singleObjectCrud    = new TaxModelFactory().createSingleObjectCrud();
  private specificMethods     = new TaxModelFactory().createSpecificMethods();

  constructor() {
    super();
  }

  async getTaxModelList(filter?: IFilter): Promise<ICollection<ITaxModel>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getTax(key : string): Promise<ITaxModel> {
    return (await this.singleObjectCrud.getElement(key)).result;
  }

  /**
   * Function to pay a tax model
   * @param model The model to pay
   * @param data Can be an object of type IBank or a string (nrc case)
   * @returns True if model was paid and false if something goes wrong
   */
  async payTaxModel(model: ITaxModel, data: any): Promise<boolean> {
    if(typeof data == 'string'){
      return (await this.specificMethods.payTaxModelWithNRC(model, data)).result;
    }else{
      return (await this.specificMethods.payTaxModelWithBank(model, data)).result;
    }
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
