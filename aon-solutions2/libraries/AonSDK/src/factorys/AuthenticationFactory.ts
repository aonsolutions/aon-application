import { IAuthenticationManagerFactory } from "../interfaces/factoryInterfaces";
import { IAuthenticationManager } from "../interfaces/serviceInterfaces";
import { StorableUser } from "../models/User";
import { APIAuthenticationRepository, AuthenticationRepository } from "../repositorys/AuthenticationRepository";
import { AuthenticationManager } from "../services/AuthenticationService";
import { APIEnvironment } from "../utils/constants";

export class AuthenticationFactory implements IAuthenticationManagerFactory {
    createAuthenticationManager(): IAuthenticationManager {
        return new AuthenticationManager(APIEnvironment ? new APIAuthenticationRepository() : new AuthenticationRepository(new StorableUser()));
    }
}