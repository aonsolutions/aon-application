import { UserFactory } from "../factorys/UserFactory";
import { IUser } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class UserFunctions {
    private static factory = new UserFactory();
    private static specificMethods = new UserFactory().createSpecificMethods();

    static async getUserList(filter?: IFilter): Promise<IResponse<ICollection<IUser>>> {
        return (await this.factory.createMultipleObjectCrud().getCollection(filter));
    }

    static async getUser(pkey: any): Promise<IResponse<IUser>> {
        return (await this.factory.createSingleObjectCrud().getElement(pkey));
    }

    static async createUser(user: IUser): Promise<IResponse<IUser>> {
        return (await this.factory.createSingleObjectCrud().createElement(user));
    }

    static async deleteUser(pkey: any): Promise<IResponse<boolean>> {
        return (await this.factory.createSingleObjectCrud().deleteElement(pkey));
    }

    static async updateUser(user: IUser): Promise<IResponse<IUser>> {
        return (await this.factory.createSingleObjectCrud().updateElement(user));
    }

    static async getCurrentUserData(): Promise<IResponse<IUser>> {
        return (await this.specificMethods.getCurrentUserData());
    }

    static async updateCurrentUserData(user: IUser): Promise<IResponse<IUser>> {
        return (await this.specificMethods.updateCurrentUserData(user));
    }
}