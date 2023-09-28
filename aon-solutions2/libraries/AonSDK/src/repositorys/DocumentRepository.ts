import { IDocument } from "../interfaces/modelsInterfaces";
import { IDocumentSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { largeImage, smallImage, pdf } from "../utils/GenerateFakeData";
import { ApiHttpRequest } from "../utils/Http";
import { BASE_URL, GET_METHOD } from "../utils/constants";

export class LocalDocumentSpecificMethodsRepository implements IDocumentSpecificMethodsRepository {
    async getRawFile(document: IDocument): Promise<string> {
        switch(document.File){
            case '/largeImage':
                return largeImage;
            case '/smallImage':
                return smallImage;
            case '/pdf':
                return pdf;
            default:
                return smallImage;
        }
    }
}

export class ApiDocumentSpecificMethodsRepository implements IDocumentSpecificMethodsRepository {
    httpRequest = new ApiHttpRequest();

    async getRawFile(document: IDocument): Promise<string> {
        return this.httpRequest.httpRequest(BASE_URL + document.Path, GET_METHOD, {}, {});
    }
}