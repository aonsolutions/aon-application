import { Injectable } from '@angular/core';
import { ICollection, IFilter, IMessage, MessageFactory } from 'libraries/AonSDK/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class MessageService extends CommonService {

  private singleObjectCrud = new MessageFactory().createSingleObjectCrud();
  private multipleObjectCrud = new MessageFactory().createMultipleObjectCrud();

  constructor() {
    super();
  }

  async getMessageList(filter?: IFilter): Promise<ICollection<IMessage>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getMessageCount(filter?: IFilter): Promise<number> {
    return (await this.multipleObjectCrud.getCollection(filter)).result.size();
  }

  async getMessage(pkey: any): Promise<IMessage> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async updateMessage(messages: IMessage): Promise<IMessage> {
    return (await this.singleObjectCrud.updateElement(messages)).result;
  }

  async deleteMessage(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async createMessage(messages: IMessage): Promise<IMessage> {
    return (await this.singleObjectCrud.createElement(messages)).result;
  }

}
