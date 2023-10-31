import { IResponse, IBank } from "../aon";
import { ITaxModel } from "../interfaces/modelsInterfaces";
import { ITaxModelSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { ITaxModelSpecificMethods } from "../interfaces/serviceInterfaces";
import { ErrorResponse, Response } from "../utils/Response";

export class TaxModelSpecificMethods implements ITaxModelSpecificMethods {

    repository;

    constructor(repository: ITaxModelSpecificMethodsRepository){
        this.repository = repository;
    }

    /**
    * Realiza el pago del modelo mediante el NRC.
    *
    * @param {ITaxModel} model - El modelo de impuestos a utilizar para el pago.
    * @param {string} nrc - El NRC a utilizar para el pago.
    * @return {Promise<IResponse<boolean>>} - Una promesa que se resuelve en una respuesta que indica si el pago tuvo éxito o no.
    */
    async payTaxModelWithNRC(model: ITaxModel, nrc: string): Promise<IResponse<boolean>> {
        try{
            return new Response<boolean>(await this.repository.payTaxModelWithNRC(model, nrc));
        }catch(e){
            throw e instanceof ErrorResponse ?  e : new ErrorResponse('0601');
        }
    }

    /**
     * Realiza el pago del modelo de impuestos con un banco.
     *
     * @param {ITaxModel} model - El modelo de impuestos a pagar.
     * @param {IBank} bank - El banco para realizar el pago.
     * @return {Promise<IResponse<boolean>>} Una promesa que se resuelve en un objeto de respuesta que indica el éxito o el fracaso del pago.
     */
    async payTaxModelWithBank(model: ITaxModel, bank: IBank): Promise<IResponse<boolean>> {
        try{
            return new Response<boolean>(await this.repository.payTaxModelWithBank(model, bank));
        }catch(e){
            throw e instanceof ErrorResponse ?  e : new ErrorResponse('0602');
        }
    }

}
