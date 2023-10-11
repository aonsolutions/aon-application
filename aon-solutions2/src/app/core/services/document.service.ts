import { Injectable } from '@angular/core';
import { DocumentFactory, ICollection, IDocument, IFilter } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';
import { Base64ToFile, Base64toBlob, FileToBase64 } from '../utilities/file';
import { MainFolders } from 'libraries/AonSDK/src/interfaces/modelsInterfaces';

@Injectable({
  providedIn: 'root',
})
export class DocumentService extends CommonService {

  private singleObjectCrud = new DocumentFactory().createSingleObjectCrud();
  private multipleObjectCrud = new DocumentFactory().createMultipleObjectCrud();
  private specificMethods = new DocumentFactory().createDocumentSpecificMethods();

  constructor() {
    super();
  }

  async getDocumentList(filter?: IFilter): Promise<ICollection<IDocument>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getDocument(pkey: any): Promise<IDocument> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async uploadDocument(document: IDocument, file: File): Promise<boolean> {
    return (await this.specificMethods.uploadDocument(document, file)).result;
  }

  async updateDocument(documents: IDocument | ICollection<IDocument>): Promise<IDocument | ICollection<IDocument>> {
    return (await this.singleObjectCrud.updateElement(documents as IDocument)).result;
  }

  async deleteDocument(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async getDocumentFile(document: IDocument): Promise<Blob> {
    return (await this.specificMethods.getRawFile(document)).result
  }

  async downloadDocument(documentData: IDocument): Promise<void> {
    let response: Blob = (await this.getDocumentFile(documentData))
    const blobUrl = URL.createObjectURL(response);
    const a = document.createElement('a')
    a.href = blobUrl
    a.download = documentData.FileName;
    a.click();
    URL.revokeObjectURL(blobUrl);
  }

}
