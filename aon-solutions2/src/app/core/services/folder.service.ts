import { Injectable } from '@angular/core';
import { FolderFactory, ICollection, IFilter, IFolder } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class FolderService extends CommonService {

  private singleObjectCrud = new FolderFactory().createSingleObjectCrud();
  private multipleObjectCrud = new FolderFactory().createMultipleObjectCrud();

  constructor() {
    super();
  }

  async getFolderList(filter?: IFilter): Promise<ICollection<IFolder>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

}
