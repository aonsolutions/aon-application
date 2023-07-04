import { Injectable } from '@angular/core';
import { AonSDK, Filter } from 'libraries/AonSDK/AonSDK';
import { Folder } from '../models/class/folder';

@Injectable({
  providedIn: 'root'
})
export class FolderService {

  private aonSDK: AonSDK = new AonSDK();

  constructor(){}

  getFolderList(filter?: Filter): Promise<Folder[]> {
    return new Promise((resolve, reject) => {
      this.aonSDK.model('folder').getElementList('folder', filter)
      .then(
        (response:any) => {
          resolve(new Folder().deserializeArray(response.result));
        }
      )
      .catch(
        (error:any) => {
          reject(error);
        }
      )
    })
  }

  getFolder(pkey: any): Promise<Folder> {
    return new Promise((resolve, reject) => {
      this.aonSDK.model('folder').getElement('folder', pkey)
      .then(
        (response:any) => {
          resolve(new Folder().deserialize(response.result));
        }
      )
      .catch(
        (error:any) => {
          reject(error);
        }
      )
    })
  }

  createFolder(folder: Folder[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK.model('folder').createElement('folder', folder)
      .then(
        (response:any) => {
          resolve(response.result);
        }
      )
      .catch(
        (error:any) => {
          reject(error);
        }
      )
    })
  }

  deleteFolder(pkey: any): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK.model('folder').deleteElement('folder', pkey)
      .then(
        (response:any) => {
          resolve(response.result);
        }
      )
      .catch(
        (error:any) => {
          reject(error);
        }
      )
    })
  }

}
