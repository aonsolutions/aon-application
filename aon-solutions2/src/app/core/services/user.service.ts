import { Injectable } from '@angular/core';
import { ICollection, IFilter, IUser, UserFactory } from 'libraries/AonSDK/aon';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private singleObjectCrud = new UserFactory().createSingleObjectCrud();
  private multipleObjectCrud = new UserFactory().createMultipleObjectCrud();

  constructor() { }

  async getUserList(filter?: IFilter): Promise<ICollection<IUser>> {
    return (await this.multipleObjectCrud.getCollection(filter)).result;
  }

  async getUser(pkey: any): Promise<IUser> {
    return (await this.singleObjectCrud.getElement(pkey)).result;
  }

  async createUser(user: IUser): Promise<IUser> {
    return (await this.singleObjectCrud.createElement(user)).result;
  }

  async deleteUser(pkey: any): Promise<boolean> {
    return (await this.singleObjectCrud.deleteElement(pkey)).result;
  }

  async updateUser(user: IUser): Promise<IUser> {
    return (await this.singleObjectCrud.updateElement(user)).result;
  }

}
