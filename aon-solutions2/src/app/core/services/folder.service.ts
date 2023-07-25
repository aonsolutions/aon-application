import { Injectable } from '@angular/core';
import { FolderFactory, ICollection, IFilter, IFolder } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root',
})
export class FolderService {

  private singleObjectCrud = new FolderFactory().createSingleObjectCrud();
  private multipleObjectCrud = new FolderFactory().createMultipleObjectCrud();

  constructor() {}

  async getFolderList(filter?: IFilter): Promise<ICollection<IFolder>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getFolder(pkey: any): Promise<IFolder> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createFolder(folder: IFolder): Promise<IFolder> {
    return (await this.singleObjectCrud.createElement(folder)).result;
  }

  async deleteFolder(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }
}
