import { IUser } from "../interfaces/modelsInterfaces";
import { IUserSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { User, StorableUser } from "../models/User";
import { GenericSingleObjectCrudRepository } from "./GenericRepository";
import { users } from "../utils/GenerateFakeData"

export class LocalUserSpecificMethodsRepository implements IUserSpecificMethodsRepository {
    async getCurrentUserData(): Promise<IUser> {
        return users.get(localStorage.getItem('user') || '');
    }

    async updateCurrentUserData(user: User): Promise<IUser> {
        return new GenericSingleObjectCrudRepository<User>(new StorableUser(), User).update(user);
    }
}