import { Injectable } from '@angular/core';
import { ICollection, IFilter, IUser, UserFactory } from 'libraries/AonSDK/src/aon';
import { CommonService } from './common.service';

@Injectable({
  providedIn: 'root'
})
export class UserService extends CommonService {

  private factory = new UserFactory();
  private specificMethods = new UserFactory().createSpecificMethods();

  constructor() {
    super();
  }

  async getUserList(filter?: IFilter): Promise<ICollection<IUser>> {
    return (await this.factory.createMultipleObjectCrud().getCollection(filter)).result;
  }

  async getUser(pkey: any): Promise<IUser> {
    return (await this.factory.createSingleObjectCrud().getElement(pkey)).result;
  }

  async createUser(user: IUser): Promise<IUser> {
    return (await this.factory.createSingleObjectCrud().createElement(user)).result;
  }

  async deleteUser(pkey: any): Promise<boolean> {
    return (await this.factory.createSingleObjectCrud().deleteElement(pkey)).result;
  }

  async updateUser(user: IUser): Promise<IUser> {
    return (await this.factory.createSingleObjectCrud().updateElement(user)).result;
  }

  async getCurrentUserData(): Promise<IUser> {
    return (await this.specificMethods.getCurrentUserData()).result;
  }

  async updateCurrentUserData(user: IUser): Promise<IUser> {
    return (await this.specificMethods.updateCurrentUserData(user)).result;
  }
}
