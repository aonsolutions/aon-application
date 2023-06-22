import { Injectable } from '@angular/core';
import { AonSDK, Optional } from 'libraries/AonSDK/AonSDK';
import { Message } from '../models/class/message';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private aonSDK = new AonSDK();

  constructor() {}

  getMessageList(optional?: Optional): Promise<Message[]> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .getElementList('message', optional)
        .then((response: any) => {
          resolve(new Message().deserializeArray(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }
}
