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

    /**
     * Recupera los datos del usuario actual.
     *
     * @return {Promise<IResponse<IUser>>} El objeto de respuesta que contiene los datos del usuario.
     */
    async getCurrentUserData(): Promise<IResponse<IUser>> {
        try {
            return new Response<IUser>(await this.SpecificMethodsRepository.getCurrentUserData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'User');
        }
    }

    /**
     * Actualiza los datos del usuario actual.
     *
     * @param {User} user - El objeto de usuario que contiene los datos actualizados.
     * @returns {Promise<IResponse<IUser>>} Una promesa que se resuelve en el objeto de respuesta que contiene los datos actualizados del usuario.
     */
    async updateCurrentUserData(user: User): Promise<IResponse<IUser>> {
        try {
            return new Response<IUser>(await this.SpecificMethodsRepository.updateCurrentUserData(user));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202', 'User');
        }
    }
}
