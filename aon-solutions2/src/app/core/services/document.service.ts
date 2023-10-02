import { Injectable } from '@angular/core';
import { Base64toBlob } from '../utilities/file';
import { DocumentFactory, ICollection, IDocument, IFilter } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class DocumentService extends CommonService {

  private singleObjectCrud = new DocumentFactory().createSingleObjectCrud();
  private multipleObjectCrud = new DocumentFactory().createMultipleObjectCrud();

  constructor() {
    super();
  }

  async getDocumentList(filter?: IFilter): Promise<ICollection<IDocument>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getDocument(pkey: any): Promise<IDocument> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async updateDocument(documents: IDocument | ICollection<IDocument>): Promise<IDocument | ICollection<IDocument>> {
    return (await this.singleObjectCrud.updateElement(documents as IDocument)).result;
  }

  async deleteDocument(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async createDocument(documents: IDocument): Promise<IDocument> {
    return (await this.singleObjectCrud.createElement(documents)).result;
  }

  async downloadDocument(path: string): Promise<void> {
    let response: IDocument = (await this.singleObjectCrud.getElement(path)).result
    const blob = Base64toBlob(response.File, response.FileType);
    const blobUrl = URL.createObjectURL(blob);
    const a = document.createElement('a')
    a.href = blobUrl
    a.download = response.FileName;
    a.click();
    URL.revokeObjectURL(blobUrl);
  }

}
