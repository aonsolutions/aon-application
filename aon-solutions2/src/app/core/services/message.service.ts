import { Injectable } from '@angular/core';
import { ICollection, IFilter, IMessage, ITaskHolder, MessageFactory, TaskHolderFactory } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root',
})
export class MessageService extends CommonService {

  private singleObjectCrud = new MessageFactory().createSingleObjectCrud();
  private multipleObjectCrud = new MessageFactory().createMultipleObjectCrud();
  private specificMethods = new MessageFactory().createMessageSpecificMethods();
  private multipleObjectCrudTaskHolders = new TaskHolderFactory().createMultipleObjectCrud();
  private singleObjectCrudTaskHolders = new TaskHolderFactory().createSingleObjectCrud();

  constructor() {
    super();
  }

  async getMessageList(filter?: IFilter): Promise<ICollection<IMessage>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getMessageCount(filter?: IFilter): Promise<number> {
    return (await this.specificMethods.getMessageCount(filter)).result;
  }

  async getMessage(pkey: any): Promise<IMessage> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createMessage(message: IMessage): Promise<IMessage> {
    return (await this.singleObjectCrud.createElement(message)).result;
  }

  async archiveMessage(message: IMessage): Promise<IMessage> {
    return (await this.specificMethods.archiveMessage(message)).result;
  }

  async reopenMessage(message: IMessage): Promise<IMessage> {
    return (await this.specificMethods.reopenMessage(message)).result;
  }

  async getTaskHoldersList(filter?: IFilter): Promise<ICollection<ITaskHolder>> {
    return (await this.multipleObjectCrudTaskHolders.getCollection(filter)).result;
  }

  async getTaskHolder(pkey: any): Promise<ITaskHolder> {
    return (await this.singleObjectCrudTaskHolders.getElement(pkey)).result;
  }

  async markAsReadNotification(message: IMessage): Promise<boolean> {
    return (await this.specificMethods.markAsReadNotification(message)).result;
  }

}
