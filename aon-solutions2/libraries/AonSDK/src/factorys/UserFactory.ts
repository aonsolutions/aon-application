import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IUser } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IUserSpecificMethods } from "../interfaces/serviceInterfaces";
import { LocalUserSpecificMethodsRepository } from "../repositorys/UserRepository";
import { UserSpecificMethods } from "../services/UserService";

export class UserFactory implements ISingleObjectCrudFactory<IUser>, IMultipleObjectCrudFactory<IUser> {
    createSingleObjectCrud(): ISingleObjectCrud<IUser> {
        throw new Error('Method not implemented.');
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IUser> {
        throw new Error('Method not implemented.');
    }
    createSpecificMethods(): IUserSpecificMethods {
        return new UserSpecificMethods(new LocalUserSpecificMethodsRepository());
    }
}