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

    /**
     * Sube un documento a la plataforma.
     *
     * @param {IDocument} document - El objeto documento que contiene la información necesaria.
     * @param {File} file - El archivo a subir.
     * @return {Promise<IResponse<boolean>>} Una promesa que se resuelve en un objeto de respuesta indicando si el documento se subió correctamente.
     */
    async uploadDocument(document: IDocument, file: File): Promise<IResponse<boolean>> {
        try {
            return new Response<boolean>(await this.SpecificMethodsRepository.uploadDocument(document, file));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('301');
        }
    }

    /**
     * Obtiene el contenido sin formato(raw) del archivo para un documento dado.
     *
     * @param {IDocument} document - El objeto documento.
     * @return {Promise<IResponse<string>>} - Una promesa que se resuelve con el contenido sin formato del archivo como una cadena.
     */
    async getRawFile(document: IDocument): Promise<IResponse<string>> {
        try {
            return new Response<string>(await this.SpecificMethodsRepository.getRawFile(document));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0304');
        }
    }
}
