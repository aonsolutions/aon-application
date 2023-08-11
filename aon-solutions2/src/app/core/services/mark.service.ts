import { Injectable } from '@angular/core';
import { b64toBlob } from '../utilities/file';
import { ICollection, IFilter, IMark, MarkFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root'
})
export class MarkService {

  private singleObjectCrud = new MarkFactory().createSingleObjectCrud();
  private multipleObjectCrud = new MarkFactory().createMultipleObjectCrud();

  constructor() { }

  async getMarkList(filter?: IFilter): Promise<ICollection<IMark>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getMark(pkey: any): Promise<IMark> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createMark(mark: IMark): Promise<IMark> {
    return (await this.singleObjectCrud.createElement(mark)).result;
  }

  async updateMark(mark: IMark): Promise<IMark> {
    return (await this.singleObjectCrud.updateElement(mark)).result;
  }

  async deleteMark(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  // Obtener lista de marcajes y después generar un documento
  async downloadMark(): Promise<void> {
    // TODO: Implementar
  }

}
