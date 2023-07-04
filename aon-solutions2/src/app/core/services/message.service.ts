import { Injectable } from '@angular/core';
import { AonSDK, Filter } from 'libraries/AonSDK/AonSDK';
import { Message } from '../models/class/message';
import { MessageChat } from '../models/class/message-chat';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private aonSDK = new AonSDK();

  constructor() {}

  getMessageList(filter?: Filter): Promise<Message[]> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .getElementList('message', filter)
        .then((response: any) => {
          resolve(new Message().deserializeArray(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  getMessageCount(filter?: Filter): Promise<number> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .getElementCount('message', filter)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  getMessage(pkey: any): Promise<Message> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .getElement('message', pkey)
        .then((response: any) => {
          resolve(new Message().deserialize(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  updateMessage(messages: Message[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .updateElement('message', messages)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  deleteMessage(pkey: any): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .deleteElement('message', pkey)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  createMessage(messages: Message[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('message')
        .createElement('message', messages)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  getMessageChatList(filter: Filter): Promise<MessageChat[]> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('messagechat')
        .getElementList('messagechat', filter)
        .then((response: any) => {
          resolve(new MessageChat().deserializeArray(response.result));
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }

  createMessageChat(messageChats: MessageChat[]): Promise<boolean> {
    return new Promise((resolve, reject) => {
      this.aonSDK
        .model('messagechat')
        .createElement('messagechat', messageChats)
        .then((response: any) => {
          resolve(response.result);
        })
        .catch((error: any) => {
          reject(error);
        });
    });
  }
}
