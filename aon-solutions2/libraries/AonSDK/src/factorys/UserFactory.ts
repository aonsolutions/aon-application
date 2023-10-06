import { GenericMultipleObjectCrud, GenericSingleObjectCrud } from "../services/GenericCrudService";
import { GenericMultipleObjectCrudRepository, GenericSingleObjectCrudRepository } from "../repositorys/GenericRepository";
import { ISingleObjectCrud, IMultipleObjectCrud, IUserSpecificMethods } from "../interfaces/serviceInterfaces";
import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IUser } from "../interfaces/modelsInterfaces";
import { LocalUserSpecificMethodsRepository } from "../repositorys/UserRepository";
import { StorableUser, User } from "../models/User";
import { UserSpecificMethods } from "../services/UserService";

export class UserFactory implements ISingleObjectCrudFactory<IUser>, IMultipleObjectCrudFactory<IUser> {
    createSingleObjectCrud(): ISingleObjectCrud<IUser> {
      return new GenericSingleObjectCrud<User>(new GenericSingleObjectCrudRepository<User>(new StorableUser(), User), User);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IUser> {
      return new GenericMultipleObjectCrud<User>(new GenericMultipleObjectCrudRepository<User>(new StorableUser(), User), User);
    }
    createSpecificMethods(): IUserSpecificMethods {
        return new UserSpecificMethods(new LocalUserSpecificMethodsRepository());
    }
}
