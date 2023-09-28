import { IUser } from "../interfaces/modelsInterfaces";
import { IUserSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IUserSpecificMethods } from "../interfaces/serviceInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";
import { User } from "../models/User";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class UserSpecificMethods implements IUserSpecificMethods {
    protected SpecificMethodsRepository: IUserSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: IUserSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    async getCurrentUserData(): Promise<IResponse<IUser>> {
        try {
            return new Response<IUser>(await this.SpecificMethodsRepository.getCurrentUserData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async updateCurrentUserData(user: User): Promise<IResponse<IUser>> {
        try {
            return new Response<IUser>(await this.SpecificMethodsRepository.updateCurrentUserData(user));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}