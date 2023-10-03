import { IResponse, IBank } from "../aon";
import { IModel } from "../interfaces/modelsInterfaces";
import { ITaxModelSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { ITaxModelSpecificMethods } from "../interfaces/serviceInterfaces";

export class TaxModelSpecificMethods implements ITaxModelSpecificMethods {

    repository;

    constructor(repository: ITaxModelSpecificMethodsRepository){
        this.repository = repository;
    }

    payTaxModelWithNRC(model: IModel, nrc: string): Promise<IResponse<boolean>> {
        throw new Error("Method not implemented.");
    }
    payTaxModelWithBank(model: IModel, bank: IBank): Promise<IResponse<boolean>> {
        throw new Error("Method not implemented.");
    }

}