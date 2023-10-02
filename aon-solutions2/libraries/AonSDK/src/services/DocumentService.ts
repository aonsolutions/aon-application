import { IDocument } from "../interfaces/modelsInterfaces";
import { IDocumentSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IDocumentSpecificMethods } from "../interfaces/serviceInterfaces";
import { IResponse } from "../interfaces/utilitiesInterfaces";
import { ErrorResponse } from "../utils/Response";
import { Response } from "../utils/Response";

export class DocumentSpecificMethods implements IDocumentSpecificMethods {
    protected SpecificMethodsRepository: IDocumentSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: IDocumentSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    async getRawFile(document: IDocument): Promise<IResponse<string>> {
        try {
            return new Response<string>(await this.SpecificMethodsRepository.getRawFile(document));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}