import { IDocument, MainFolders } from "../interfaces/modelsInterfaces";
import { IDocumentSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { ApiHttpRequest } from "../utils/Http";
import { BASE_URL } from "../utils/Environment";
import { APIGenericMultipleObjectCrudRepository, APIGenericSingleObjectCrudRepository, GenericSingleObjectCrudRepository } from "./GenericRepository";
import { ApiDocument, Document, StorableDocument } from "../models/Document";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { INVOICE_URL, TAXMODEL_URL } from "../utils/ApiUrls";
import { ErrorResponse } from "../utils/Response";
import { Base64toBlob, FileToBase64 } from "../utils/FileHelper";

export class LocalDocumentSpecificMethodsRepository implements IDocumentSpecificMethodsRepository {

    async uploadDocument(document: Document, file: File): Promise<boolean> {
        let storable = new StorableDocument();
        if(document.Path && document.FileName){
            storable.getCollection().forEach(item => {
                if(item.Path == document.Path && item.FileName == document.FileName){
                    throw new ErrorResponse('0303');
                }
            })
            let base64 = await FileToBase64(file);
            if(typeof base64 == 'string'){
                new GenericSingleObjectCrudRepository(storable, Document).create(document);
                localStorage.setItem(document.Path + document.FileName, base64)
                return true;
            }
        }
        throw new ErrorResponse('0302')
    }

    async getRawFile(document: IDocument): Promise<any> {
        let base64 = localStorage.getItem(document.Path + document.FileName);
        if(base64){
            return Base64toBlob(base64, document.FileType);
        }
        throw(new ErrorResponse('0206'))
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

    async uploadDocument(document: IDocument, file: File): Promise<boolean> {
        let json = {
            file: {
                content: await FileToBase64(file),
                contentEncoding: "base64",
                contentName: document.FileName,
                contentSize: file.size,
                contentType: file.type,
                date: new Date(),
                domain: {id: localStorage.getItem("domainId"), name: localStorage.getItem("domainName")},
                name: document.FileName,
                size: file.size,
            },
            invoice: {
                category: "",
                comments: "",
                creation_user: localStorage.getItem("login"),
                date: new Date(),
                details: [],
                domain: localStorage.getItem("domainId"),
                finances: [],
                number: "",
                receiver: {document: "", name: "", address: {country: "", address: "", zip: "", city: "", province: ""}},
                address: {country: "", address: "", zip: "", city: "", province: ""},
                reference: "",
                remarks: [],
                selfconta: false,
                sender: {document: "", name: "", address: {country: "", address: "", zip: "", city: "", province: ""}},
                serie: new Date().getFullYear(),
                series: new Date().getFullYear(),
                status: "inbox",
                suplidos: {active: false, description: "", total: 0},
                taxes: [],
                tbai: false,
                tbaiUrl: "",
                total: 0,
                transaction: "NAC",
                type: "recibida",
                withholding: false,
            }
        }
        let response = await ApiHttpRequest.post(BASE_URL + INVOICE_URL.GET_INVOICE__ONE, {}, json)
        if(response?.type == 'error') return false; 
        else return true;
    }

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