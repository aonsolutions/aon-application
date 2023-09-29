import { UserFactory } from "../factorys/UserFactory";
import { IUser } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class UserFunctions {
    private factory = new UserFactory();
    private specificMethods = new UserFactory().createSpecificMethods();

    async getUserList(filter?: IFilter): Promise<IResponse<ICollection<IUser>>> {
        return (await this.factory.createMultipleObjectCrud().getCollection(filter));
    }

    async getUser(pkey: any): Promise<IResponse<IUser>> {
        return (await this.factory.createSingleObjectCrud().getElement(pkey));
    }

    async createUser(user: IUser): Promise<IResponse<IUser>> {
        return (await this.factory.createSingleObjectCrud().createElement(user));
    }

    async deleteUser(pkey: any): Promise<IResponse<boolean>> {
        return (await this.factory.createSingleObjectCrud().deleteElement(pkey));
    }

    async updateUser(user: IUser): Promise<IResponse<IUser>> {
        return (await this.factory.createSingleObjectCrud().updateElement(user));
    }

    async getCurrentUserData(): Promise<IResponse<IUser>> {
        return (await this.specificMethods.getCurrentUserData());
    }

    async updateCurrentUserData(user: IUser): Promise<IResponse<IUser>> {
        return (await this.specificMethods.updateCurrentUserData(user));
    }
}