import { IDocument, MainFolders } from "../interfaces/modelsInterfaces";
import { IDocumentSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { largeImage, smallImage, pdf } from "../utils/GenerateFakeData";
import { ApiHttpRequest } from "../utils/Http";
import { BASE_URL } from "../utils/Environment";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository } from "./GenericRepository";
import { ApiDocument, Document } from "../models/Document";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { INVOICE_URL, TAXMODEL_URL } from "../utils/ApiUrls";
import { ErrorResponse } from "../utils/Response";
import { Base64toBlob } from "../utils/FileHelper";

export class LocalDocumentSpecificMethodsRepository implements IDocumentSpecificMethodsRepository {
    async getRawFile(document: IDocument): Promise<any> {
        switch(document.File){
            case '/largeImage':
                return Base64toBlob(largeImage, document.FileType);
            case '/smallImage':
                return Base64toBlob(smallImage, document.FileType);
            case '/pdf':
                return Base64toBlob(pdf, document.FileType);
            default:
                return Base64toBlob(smallImage, document.FileType);
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
        let page = filter?.pageNum || 1;
        let perPage = filter?.pageItems || 30;
        if(path == MainFolders.ACONTABILIZAR){
            // inbox(pendiente), rejected(rechazado), draft(eliminado en papelera) => 3 estados de facturas que estan en documentos pendientes
            url = INVOICE_URL.GET_INVOICE_LIST + '?status=inbox&page=' + page + '&per_page=' + perPage
        }else if(path == MainFolders.CONTABILIZADO){
            // accounting puede tener estado pending o scored, scored es cuando esta contabilizado pero no se pueden pedir solo las contabilizadas.
            url = INVOICE_URL.GET_INVOICE_LIST + '?status=accounting&page=' + page + '&per_page=' + perPage
        }else if(path == MainFolders.FISCAL){
            url = TAXMODEL_URL.GET_TAXMODEL_LIST
        }else if(path == MainFolders.PAPELERA){
            url = INVOICE_URL.GET_INVOICE_LIST + '?status=draft&page=' + page + '&per_page=' + perPage
        }else if(path.includes(MainFolders.LABORAL)){
            // Se deberia de llamar a la api para traer los documentos de un empleado, contrato, nominas, etc.
            throw new ErrorResponse('0199')
        }else{
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

    async getRawFile(document: IDocument): Promise<any> {
        if(document.File != ''){
            let url = document.File
            if(typeof document.File == 'number'){
                let response = await ApiHttpRequest.get(BASE_URL + INVOICE_URL.GET_INVOICE__ONE + '?id=' + document.File, {}, {});
                url = response?.file?.path ? response.file.path : '';
            }
            return await ApiHttpRequest.httpRequestFile(BASE_URL + url, {}, {});
        }else{
            throw new ErrorResponse('0199')
        }
    }
}