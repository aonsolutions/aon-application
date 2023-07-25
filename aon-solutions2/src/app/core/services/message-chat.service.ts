import { Injectable } from '@angular/core';
import { ICollection, IFilter, IMessageChat, MessageChatFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root'
})
export class MessageChatService {

  private singleObjectCrud = new MessageChatFactory().createSingleObjectCrud();
  private multipleObjectCrud = new MessageChatFactory().createMultipleObjectCrud();

  constructor() { }

  async getMessageChatList(filter: IFilter): Promise<ICollection<IMessageChat>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async createMessageChat(messageChats: IMessageChat): Promise<IMessageChat> {
    return (await this.singleObjectCrud.createElement(messageChats)).result;
  }

}
