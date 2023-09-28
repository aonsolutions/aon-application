import { Injectable } from '@angular/core';
import { DocumenNoteFactory, IDocumentNote } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class DocumentNoteService extends CommonService {

  singleObjectCrud = new DocumenNoteFactory().createSingleObjectCrud();

  constructor() {
    super();
  }

  async getDocumentNote(pkey: any): Promise<IDocumentNote> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async deleteDocumentNote(pkey: any): Promise<boolean> {
    await this.singleObjectCrud.deleteElement(pkey)
    return true;
  }

  async createDocumentNote(documentnotes: IDocumentNote): Promise<IDocumentNote> {
    return (await this.singleObjectCrud.createElement(documentnotes)).result;
  }
}
