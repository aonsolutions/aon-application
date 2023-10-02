import { Injectable } from '@angular/core';
import { ContactFactory, Factory, ICollection, IContact, IFilter } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class ContactService extends CommonService {

  private contactFactory = new ContactFactory();

  constructor() {
    super();
  }

  async getContact(key: string): Promise<IContact>{
    return (await this.contactFactory.createSingleObjectCrud().getElement(key)).result;
  }

  async getContactList(filter?: IFilter): Promise<ICollection<IContact>> {
    return (await this.contactFactory.createMultipleObjectCrud().getCollection(filter)).result;
  }

  async createContact(contact: IContact): Promise<IContact> {
    return (await this.contactFactory.createSingleObjectCrud().createElement(contact)).result;
  }

  async updateContact(contact: IContact): Promise<IContact> {
    return (await this.contactFactory.createSingleObjectCrud().updateElement(contact)).result;
  }

  async deleteContact(key: string): Promise<boolean> {
    return (await this.contactFactory.createSingleObjectCrud().deleteElement(key)).result;
  }

}
