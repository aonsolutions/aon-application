import { Injectable } from '@angular/core';
import { ICollection, IFilter, IMessageChat, MessageChatFactory } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class MessageChatService extends CommonService {

  private singleObjectCrud = new MessageChatFactory().createSingleObjectCrud();
  private multipleObjectCrud = new MessageChatFactory().createMultipleObjectCrud();

  constructor() {
    super();
  }

  async getMessageChatCount(filter?: IFilter): Promise<number> {
    return (await this.multipleObjectCrud.getCollection(filter)).result.size();
  }
  async getMessageChatList(filter: IFilter): Promise<ICollection<IMessageChat>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async createMessageChat(messageChats: IMessageChat): Promise<IMessageChat> {
    return (await this.singleObjectCrud.createElement(messageChats)).result;
  }

}
