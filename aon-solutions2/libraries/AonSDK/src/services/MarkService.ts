import { IMark } from "../interfaces/modelsInterfaces";
import { IMarkSpecificMethods } from "../interfaces/serviceInterfaces";
import { IFilter, IResponse, ICollection } from "../interfaces/utilitiesInterfaces";
import { APIMarkSpecificMethodsRepository } from "../repositorys/MarkRepository";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class MarkSpecificMethods implements IMarkSpecificMethods {

    repository = new APIMarkSpecificMethodsRepository();

    /**
     * Recupera los marcajes de un usuario específico según su ID de usuario y un filtro opcional.
     *
     * @param {string} userId - El ID del usuario.
     * @param {IFilter} [filter] - Un filtro opcional para aplicar a la consulta.
     * @return {Promise<IResponse<ICollection<IMark>>>} Una promesa que se resuelve en la respuesta que contiene la colección de marcajes.
     */
    async getMarksOfOneUser(userId: string, filter?: IFilter): Promise<IResponse<ICollection<IMark>>> {
        try{
            return new Response<ICollection<IMark>>(await this.repository.getMarksOfOneUser(userId, filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123', 'Mark');
        }
    }
}
