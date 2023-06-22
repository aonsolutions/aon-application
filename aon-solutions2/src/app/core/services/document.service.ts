import { Injectable } from '@angular/core';
import { AonSDK, Optional } from 'libraries/AonSDK/AonSDK';
import { Document } from '../models/class/document';
import { DocumentNote } from '../models/class/document-note';
import { b64toBlob } from '../utilities/file';

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  aonSDK: AonSDK = new AonSDK();

  constructor() {}

  getDocumentList(optional?: Optional): Promise<Document[]> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('document')
        .getElementList('document', optional)
        .then((response: any) => {
          resolve(new Document().deserializeArray(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  getDocument(pkey: any): Promise<Document> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('document')
        .getElement('document', pkey)
        .then((response: any) => {
          resolve(new Document().deserialize(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  updateDocument(documents: Document[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('document')
        .updateElement('document', documents)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  deleteDocument(pkey: any): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('document')
        .deleteElement('document', pkey)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  createDocument(documents: Document[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('document')
        .createElement('document', documents)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  getDocumentNote(pkey: any): Promise<DocumentNote> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('documentnote')
        .getElement('documentnote', pkey)
        .then((response: any) => {
          resolve(new DocumentNote().deserialize(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  deleteDocumentNote(pkey: any): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('documentnote')
        .deleteElement('documentnote', pkey)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  createDocumentNote(documentnotes: DocumentNote[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('documentnote')
        .createElement('documentnote', documentnotes)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  downloadDocument(path: string): Promise<boolean> {
    return new Promise((resolve,reject) => {
      this.aonSDK
        .model('document')
        .getElement('document', path)
        .then((response: any) => {
          const blob = b64toBlob(response.result.file, response.result.fileType);
          const blobUrl = URL.createObjectURL(blob);
          const a = document.createElement('a')
          a.href = blobUrl
          a.download = response.result.fileName;
          a.click();
          URL.revokeObjectURL(blobUrl);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

}
