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

    async payTaxModelWithNRC(model: ITaxModel, nrc: string): Promise<IResponse<boolean>> {
        try{
            return new Response<boolean>(await this.repository.payTaxModelWithNRC(model, nrc));
        }catch(e){
            throw e instanceof ErrorResponse ?  e : new ErrorResponse('0201'); 
        }
    }

    async payTaxModelWithBank(model: ITaxModel, bank: IBank): Promise<IResponse<boolean>> {
        try{
            return new Response<boolean>(await this.repository.payTaxModelWithBank(model, bank));
        }catch(e){
            throw e instanceof ErrorResponse ?  e : new ErrorResponse('0201'); 
        }
    }

}