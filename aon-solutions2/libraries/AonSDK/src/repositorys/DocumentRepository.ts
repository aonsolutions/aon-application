import { IDocument, MainFolders } from "../interfaces/modelsInterfaces";
import { IDocumentSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { largeImage, smallImage, pdf } from "../utils/GenerateFakeData";
import { ApiHttpRequest } from "../utils/Http";
import { BASE_URL } from "../utils/Environment";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";
import { ApiDocument, Document } from "../models/Document";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { TAXMODEL_URL } from "../utils/ApiUrls";
import { ErrorResponse } from "../utils/Response";
import { FileToBase64 } from "../utils/FileHelper";

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

export class ApiDocumentSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Document> {
    constructor() {
        super(new ApiDocument(), Document);
    }
}

export class ApiDocumentMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Document> {
    constructor() {
        super(new ApiDocument(), Document);
    }

    async get(filter?: IFilter): Promise<ICollection<Document>> {
        let path: string = filter?.fields?.get('path')[0] || '';
        let collection = new Collection<Document>();
        let url;
        if(path == MainFolders.ACONTABILIZAR){
            // inbox(pendiente), rejected(rechazado), draft(eliminado en papelera) => 3 estados de facturas que estan en documentos pendientes
            url = '/ms/api/invoice?status=inbox&page=1&per_page=50'
        }else if(path == MainFolders.CONTABILIZADO){
            // accounting puede tener estado pending o scored, scored es cuando esta contabilizado pero no se pueden pedir solo las contabilizadas.
            url = '/ms/api/invoice?status=accounting&page=1&per_page=50'
        }else if(path == MainFolders.FISCAL){
            url = TAXMODEL_URL.GET_TAXMODEL_LIST
        }else if(path == MainFolders.PAPELERA){
            url = '/ms/api/invoice?status=draft&page=1&per_page=50'
        }else if(path.includes(MainFolders.LABORAL)){
            // Se deberia de llamar a la api para traer los documentos de un empleado, contrato, nominas, etc.
            throw new ErrorResponse('0199')
        }else {
            throw new ErrorResponse('0199')
        }
        let response = await ApiHttpRequest.get(BASE_URL + url, {}, {});
        response.forEach((element: any) => {
            collection.add(this.apiModel.parseDataToReceive(element,'',filter));
        })
        return collection;
    }

}

export class ApiDocumentSpecificMethodsRepository implements IDocumentSpecificMethodsRepository {

    async getRawFile(document: IDocument): Promise<File> {
        if(document.File != ''){
            let url = document.File
            if(typeof document.File == 'number'){
                let response = await ApiHttpRequest.get(BASE_URL + '/ms/api/invoice?id=' + document.File, {}, {});
                url = response?.file?.path ? response.file.path : '';
            }
            return await ApiHttpRequest.httpRequestFile(BASE_URL + url, {}, {});
        }else{
            throw new ErrorResponse('0199')
        }
    }
}