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

    async uploadDocument(document: IDocument, file: File): Promise<IResponse<boolean>> {
        try {
            return new Response<boolean>(await this.SpecificMethodsRepository.uploadDocument(document, file));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('301');
        }
    }

    async getRawFile(document: IDocument): Promise<IResponse<string>> {
        try {
            return new Response<string>(await this.SpecificMethodsRepository.getRawFile(document));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0304');
        }
    }
}
