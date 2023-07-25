import { Injectable } from '@angular/core';
import { DocumenNoteFactory, IDocumentNote } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root'
})
export class DocumentNoteService {

  singleObjectCrud = new DocumenNoteFactory().createSingleObjectCrud();

  constructor() { }

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
