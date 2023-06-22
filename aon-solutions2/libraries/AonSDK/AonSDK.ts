const ERRORS = {
    '0000' : {description:'OK. OPERATION SUCCED.', result:''},
    '0101': {description:'ERROR_LOGIN_CREDENTIALS- INVALID CREDENTIALS', result:'Error al iniciar sesión'},
    '0201': {description:'ERROR_MODEL', result:'Error al intentar acceder al modelo'},
    '0299': {description:'ERROR_MODEL', result:'Método en construcción'},
}

interface IResponse {
    code: string;
    description: string;
    result: any;
}

class Response implements IResponse {

    code: string = '';
    description: string = '';
    result: any = '';

    constructor(code: string, result?:any){
        this.code = code;
        this.description = ERRORS[code as keyof typeof ERRORS].description;
        this.result = code.slice(0,2) == '00' ? result : ERRORS[code as keyof typeof ERRORS].result
    }
}

export interface Optional {
    fields?: any;
    pageNum?: number;
    pageItems?: number;
    filters?: Filters;
    orderBy?: string;
}

export interface Filters {
    date?: string;
    endDate?: string;
    filterFields?: {};
}

interface Collection {
    //TODO    
}

class Enterprise implements Collection {

    name: string;
    document: string;
    
    constructor(name: string, document: string){
        this.name = name;
        this.document = document;
    }

}

class Folder implements Collection {

    name: string;
    path: string;
    
    constructor(name: string, path: string){
        this.name = name;
        this.path = path;
    }

}

class Document implements Collection {
    file: string;
    fileName: string;
    fileSize: number;
    fileType: string;
    folder: string;
    path: string;

    constructor(file: string,fileName: string,fileSize: number,fileType: string,folder: string,path: string,){
        this.file = file;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.folder = folder;
        this.path = path;
    }
}

class DocumentNote implements Collection {
    text: string;
    path: string;

    constructor(text: string,path: string){
        this.text = text;
        this.path = path;
    }
}

class Banks implements Collection {
    name: string;
    total: number;
    logo: string;
    constructor(name: string,total: number,logo: string){
        this.name = name;
        this.total = total;
        this.logo = logo;
    }
}

class TaxModel implements Collection {
    name: number;
    taxType: string;
    status: string;
    paymentMethod: string;
    result: string;
    trimester: number;
    year: number;
    constructor(name: number,taxType: string,status: string,paymentMethod: string,result: string,trimester: number,year: number){
        this.name = name;
        this.taxType = taxType;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.result = result;
        this.trimester = trimester;
        this.year = year;
    }
}

class Message implements Collection {
    id: number;
    name: string;
    title: string;
    description: string;
    date: Date;
    type: string;
    status: string;
    endDate: Date;
    constructor(id:number, name: string, title: string,description: string,date: Date,type: string,status: string,endDate: Date){
        this.id = id;
        this.name = name;
        this.title = title;
        this.description = description;
        this.date = date;
        this.type = type;
        this.status = status;
        this.endDate = endDate;
    }
}

class MessageChat implements Collection {
    id: number;
    idMessage: number;
    name: string;
    description: string;
    date: Date;
    type: string;
    constructor(id:number, idMessage:number, name: string,description: string,date: Date,type: string){
        this.id = id;
        this.idMessage = idMessage;
        this.name = name;
        this.description = description;
        this.date = date;
        this.type = type;
    }
}


class Employee implements Collection {
    name: string;
    lastname: string;
    document: string;
    email: string;
    phone: string;
    NAF: string;
    active: boolean;
    constructor(name: string,lastname: string,document: string,email: string,phone: string,NAF: string,active: boolean){
        this.name = name;
        this.lastname = lastname;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.NAF = NAF;
        this.active = active;
    }
}

interface IFactory {
    // buildModel(objType: string) : any;
    buildService(obj:string): any;
}

class Factory implements IFactory {

    constructor(){}

    // ¿?¿?¿?¿?¿?¿?¿?
    // buildModel(objType: string): any {
    //     switch(objType){
    //         case 'enterprise':
    //             return new Enterprise('','');
    //         case 'folder':
    //             return new Folder('','');
    //         case 'document':
    //             return new Document('','',0,'','');
    //         default:
    //             throw new Response('0201');
    //     }
    // }

    buildService(objType: string): IService | any {
        switch(objType){
            case 'enterprise':
                return new ServiceEnterprise();
            case 'folder':
                return new ServiceFolder();
            case 'document':
                return new ServiceDocument();
            case 'auth':
                return new ServiceAuth();
            case 'taxmodel':
                return new ServiceTaxModel();
            case 'bank':
                return new ServiceBanks();
            case 'message':
                return new ServiceMessage();
            case 'reporting':
                return new ServiceReporting();
            case 'documentnote':
                return new ServiceDocumentNote();
            default:
                throw new Response('0201');
        }
    }
}

interface IService {
    [x: string]: any;
    getElement(model: string, pKey: any) : Promise<Response>;
    getElementList(model: string, optional?: Optional) : Promise<Response>;
    createElement(model: string, collection: Collection[]) : Promise<Response>;
    updateElement(model: string, collection: Collection[]) : Promise<Response>;
    deleteElement(model: string, pKey: any) : Promise<Response>;
}

class ServiceEnterprise implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < enterprises.length; i++){
                    if(enterprises[i].document == pKey){
                        resolve(new Response('0000',enterprises[i]))
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                resolve(new Response('0000',enterprises))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: Enterprise[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    enterprises.push(collection[0] as Enterprise)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: Enterprise[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < enterprises.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(enterprises[i].document == collection[j].document){
                            enterprises[i] = collection[j];
                        }
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < enterprises.length; i++){
                    if(enterprises[i].document == pKey){
                        enterprises.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    setEnterprise(model:string, pKey: any): Promise <Response> {        
        return new Promise((resolve, reject) => {
            try {
                this.getElement('enterprise',pKey).then(
                    (response) => {
                        if(response != null){
                            sessionStorage.setItem('enterprise', pKey)
                            resolve(new Response('0000',true))
                        }else{
                            reject(new Response('0201'))    
                        }
                    }
                    ).catch(
                        (error) => {
                        reject(new Response('0201'))
                    }
                )
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }
}

class ServiceDocument implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < documents.length; i++){
                    if(documents[i].path == pKey){
                        resolve(new Response('0000',documents[i]))
                    }                    
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                let data = copyObjectArray(documents);
                if(optional?.filters?.filterFields) data = applyFilters(optional,data);
                if(optional?.filters?.date && optional?.filters?.endDate) data = applyInterval(optional, data, 'date')
                resolve(new Response('0000',data))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: Document[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    documents.push(collection[0] as Document)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: Document[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < documents.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(documents[i].path == collection[j].path){
                            documents[i] = collection[j];
                            resolve(new Response('0000',true))
                        }
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < documents.length; i++){
                    if(documents[i].path == pKey){
                        documents.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    // moveElement(model: string, data: Document[], path: string): Promise <Response> {
    //     return new Promise((resolve, reject) => {
    //         try {
    //             for(let i = 0; i < documents.length; i++){
    //                 for(let j = 0; j < data.length; j++){
    //                     if(documents[i].path == data[j].path){
    //                         let aux = true;
    //                         for(let k = 0; k < documents.length; k++){
    //                             if(documents[k].path == path + '/' + data[j].fileName){
    //                                 aux = false;
    //                             }
    //                         }
    //                         if(aux) documents[i].path = path + '/' + data[j].fileName;
    //                     }
    //                 }
    //             }
    //             resolve(new Response('0000',true))
    //         } catch (error) {
    //             reject(new Response('0201'))
    //         }
    //     });
    // }

}

class ServiceDocumentNote implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < documentNotes.length; i++){
                    if(documentNotes[i].path == pKey){
                        resolve(new Response('0000',documentNotes[i]))
                    }                    
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                let data = copyObjectArray(documentNotes);
                resolve(new Response('0000',data))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: DocumentNote[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    documentNotes.push(collection[0] as DocumentNote)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: DocumentNote[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < documentNotes.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(documentNotes[i].path == collection[j].path){
                            documentNotes[i] = collection[j];
                            resolve(new Response('0000',true))
                        }
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < documentNotes.length; i++){
                    if(documentNotes[i].path == pKey){
                        documentNotes.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

}

class ServiceFolder implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < folders.length; i++){
                    if(folders[i].path == pKey){
                        resolve(new Response('0000',folders[i]))
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                let data = copyObjectArray(folders);
                if(optional?.filters?.filterFields) data = applyFilters(optional,data);
                resolve(new Response('0000',data))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: Folder[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    folders.push(collection[0] as Folder)
                }                
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: Folder[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < folders.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(folders[i].path == collection[j].path){
                            folders[i] = collection[j];
                        }
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < folders.length; i++){
                    if(folders[i].path == pKey){
                        folders.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    // downloadFolder(): Promise<Response> {
    //     return new Promise((resolve, reject) => {
    //         fetch('https://dummyjson.com/products/1')
    //         .then(res => {
    //             throw new Response('0000');
    //         })
    //         .catch((error) => {
    //             reject(error);
    //         })
    //     });
    // }

}

class ServiceBanks implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < banks.length; i++){
                    if(banks[i].name == pKey){
                        resolve(new Response('0000',banks[i]))
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                resolve(new Response('0000',banks))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: Banks[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    banks.push(collection[0] as Banks)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: Banks[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < banks.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(banks[i].name == collection[j].name){
                            banks[i] = collection[j];
                        }
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < banks.length; i++){
                    if(banks[i].name == pKey){
                        banks.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

}

class ServiceTaxModel implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < taxModels.length; i++){
                    if(taxModels[i].name == pKey.name && taxModels[i].trimester == pKey.trimester && taxModels[i].year == pKey.year){
                        resolve(new Response('0000',taxModels[i]))
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                let data = copyObjectArray(taxModels);
                if(optional?.filters?.filterFields) data = applyFilters(optional,data);
                resolve(new Response('0000', data))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: TaxModel[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    taxModels.push(collection[0] as TaxModel)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: TaxModel[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < taxModels.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(taxModels[i].name == collection[j].name && taxModels[i].trimester == collection[j].name && taxModels[i].year == collection[j].name){
                            taxModels[i] = collection[j];
                        }
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < banks.length; i++){
                    if(taxModels[i].name == pKey.name && taxModels[i].trimester == pKey.trimester && taxModels[i].year == pKey.year){
                        taxModels.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

}

class ServiceMessage implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < messages.length; i++){
                    if(messages[i].id == pKey){
                        resolve(new Response('0000',messages[i]))
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                let data = copyObjectArray(messages);
                if(optional?.filters?.filterFields) data = applyFilters(optional,data);
                if(optional?.filters?.date && optional?.filters?.endDate) data = applyInterval(optional, data, 'date')
                resolve(new Response('0000',data))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: Message[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    messages.push(collection[0] as Message)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: Message[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < messages.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(messages[i].id == collection[j].id){
                            messages[i] = collection[j];
                        }
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < messages.length; i++){
                    if(messages[i].id == pKey){
                        messages.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }
}

class ServiceMessageChat implements IService {

    getElement(model: string, pKey: any) : Promise<Response> { 
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < messageChats.length; i++){
                    if(messageChats[i].id == pKey){
                        resolve(new Response('0000',messageChats[i]))
                    }
                }
                reject(new Response('0201'))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                resolve(new Response('0000',messageChats))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    createElement(model: string, collection: MessageChat[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < collection.length; i++){
                    messageChats.push(collection[0] as MessageChat)
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    updateElement(model: string, collection: MessageChat[]) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < messageChats.length; i++){
                    for(let j = 0; j < collection.length; j++){
                        if(messageChats[i].id == collection[j].id){
                            messageChats[i] = collection[j];
                        }
                    }
                }
                resolve(new Response('0000',true))
            } catch (error) {
                reject(new Response('0201'))
            }
            
        });
    }

    deleteElement(model: string, pKey: any) : Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                for(let i = 0; i < messageChats.length; i++){
                    if(messageChats[i].id == pKey){
                        messageChats.splice(i, 1);
                    }
                }
                resolve(new Response('0000',true));
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }
}

class ServiceReporting {

    getVentasGastos(): Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                resolve(new Response('0000',{
                    label: [500,231,653,413,3364,856,2513,543,1244,543,754,1456],
                    data: [1,2,3,4,5,6,7,8,9,10,11,12]
                }
                ))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }

    getCobrosPagos(): Promise<Response> {
        return new Promise((resolve, reject) => {
            try {
                resolve(new Response('0000',{
                    label: [500,231,653,413,3364,856,2513,543,1244,543,754,1456],
                    data: [1,2,3,4,5,6,7,8,9,10,11,12]
                }
                ))
            } catch (error) {
                reject(new Response('0201'))
            }
        });
    }
}

class ServiceAuth {

    login(username : string, password : string): Promise<Response> {
        return new Promise((resolve, reject) => {
            for(let i = 0; i < users.length; i++){
                if( username == users[i].username && password == users[i].password){
                    sessionStorage.setItem('token','testToken')
                    resolve(new Response('0000',true))
                }
            }
            throw new Response('0101')
            
        });
    }

    logout() : Promise<Response> {
        return new Promise((resolve, reject) => {
            sessionStorage.clear()
            resolve(new Response('0000',true))
        });
    }

    getSession(): Response {
        return new Response('0000',sessionStorage)
    }
    
}

function applyFilters(optional: Optional, data: any): any{
    let result = []
    if(optional.filters?.filterFields){
        let filters = optional.filters?.filterFields
        for(let i = 0; i < data.length; i++){
            let aux = true;
            for(const k in filters){
                if(data[i][k] && data[i][k] != filters[k as keyof typeof filters]){
                    aux = false;
                }
            }
            if(aux) result.push(data[i])
        }
    }else{
        result = data
    }
    return result;
}

function applyInterval(optional: Optional, data: any, key:string){
    for(let i = 0; i < data.length; i++){
        if(!(new Date(optional.filters?.date || '') <= data[i][key] && data[i][key] <= new Date(optional.filters?.endDate || ''))){
            data.splice(i,1);
        }
    }
    return data;
}

function copyObjectArray(data:any): any{
    let newData = []
    for(let i = 0; i < data.length; i++){
        newData.push(new Object())
        Object.assign(newData[i],data[i])
    }
    return newData;
}

export class AonSDK {

    private factory: Factory;    

    constructor(){
        this.factory = new Factory();
    }

    model(model : any){
        return this.factory.buildService(model);
    }
    
    // login(username: string, password: string): Promise<IResponse> {
    //     return new Promise((resolve,reject) => {
    //         new ServiceAuth().login(username,password).then((response: any) => {
    //             resolve(new Response('0000', response));
    //         }).catch((error: any) => {
    //             reject(error);
    //         })
    //     });
    // }

    // logout(): Promise<IResponse> {
    //     return new Promise((resolve,reject) => {
    //         new ServiceAuth().logout().then((response: any) => {
    //             resolve(new Response('0000', response));
    //         }).catch((error: any) => {
    //             reject(error);
    //         })
    //     });
    // }

    // downloadFolder(): Promise<IResponse> {
    //     return new Promise((resolve,reject) => {
    //         this.factory.buildService('folder').downloadFolder().then((response: any) => {
    //                 resolve(new Response('0000', response));
    //             }).catch((error: any) => {
    //                 reject(error);
    //             })
    //     });
    // }

    // getElement(model: string, pKey: any): Promise<IResponse> {
    //     return new Promise((resolve,reject) => {
    //         this.factory.buildService(model).getElement(model, '/folder').then((response: any) => {
    //                 resolve(new Response('0000', response));
    //             }).catch((error: any) => {
    //                 reject(error);
    //             })
    //     });
    // }

    // getElementList(model: string, optional?: Optional): Promise<IResponse> {
    //     return new Promise((resolve,reject) => {
    //         this.factory.buildService(model).getElementList(model).then((response: any) => {
    //                 resolve(new Response('0000', response));
    //             }).catch((error: any) => {
    //                 reject(error);
    //             })
    //     });
    // }

    // createElement(model: string, collection: Collection[]) : Promise<any> {
    //     return new Promise((resolve,reject) => {
    //         this.factory.buildService(model).createElement(model, collection).then((response: any) => {
    //                 resolve(new Response('0000', response));
    //             }).catch((error: any) => {
    //                 reject(error);
    //             })
    //     });
    // }

    // updateElement(model: string, collection: Collection[]) : Promise<any> {
    //     return new Promise((resolve,reject) => {
    //         this.factory.buildService(model).updateElement(model, collection).then((response: any) => {
    //                 resolve(new Response('0000', response));
    //             }).catch((error: any) => {
    //                 reject(error);
    //             })
    //     });
    // }

    // deleteElement(model: string, pKey: any) : Promise<any> {
    //     return new Promise((resolve,reject) => {
    //         this.factory.buildService(model).deleteElement(model, pKey).then((response: any) => {
    //                 resolve(new Response('0000', response));
    //             }).catch((error: any) => {
    //                 reject(error);
    //             })
    //     });
    // }

}

let users = [
    {username: 'test@aonsolutions.test', password: 'test'}
]

let enterprises = [
    new Enterprise('Pet Estudio', 'B16880148'),
    new Enterprise('MENG SA', 'U14241855'),
    new Enterprise('PORTABAGE SL', 'U53716270')
]

let folders = [
    new Folder('A contabilizar', '/a contabilizar'),
    new Folder('Contabilizado', '/contabilizado'),
    new Folder('Papelera', '/papelera')
]

let documents = [
    new Document('iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAkFBMVEX///8iHx8AAAAfHBwbGBgdGhoaFhYYFBQWEhL8/Pz5+fnm5uYRDAz29vYhHR3w8PCMi4s0MTEOCAgrKCjr6+vb29uEg4PDwsJDQUEmIyO4t7dXVVVMSkrl5OQKAADQz8+qqamamZmhoKBmZWU9Ozuwr6/MzMy8vLxubW1gXl4xLy9JR0d1dHRSUFB9fHxcWlo9FQvaAAALMUlEQVR4nO2de3OqOhDAG0J4KCggKKLy8oWK+v2/3d2lnXNPW4KoWNIz+f1z7wzUyZLNvrLJeXuTSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQikUgkEolEIpEIiB3s9tttmuZ5mm6j/W5l9z2iLvH32bQ8LN3JnBmWpdP5xF0eymk28/seWSckWXF2iWFojFJKKuB/mGYY8825yIK+x/ckdnaajzSVfojFmArAf95lpapGJqd80PcoH8aeHRTvXToNVPO4CcPTcrFYnsKNe5wz/f2RGivr2a9clP7+Gus4U6rFNotimu+Dsfn+aDAOomwKyqsaDN8wvMPvW5J+dLBQPmrQ8yXbr+reATHLk27gTOreYf+7ZJyVlXwsdi/bpEEFx7u0GHm0msfLzvy5AT5J4GwslE9x8+TmzAx3zuRdxjCrnWoB2S8oLC8au/mqlZm0A4fgF1HJYvbqsXWBmc1VkM9imd9a68xViVaJavPslUPrhuFZAZXT5sWdGpecq4lXTuPXjKszEtWAcerL6P4/zUIdvo3hJt2PqjvM2QQ0VJ1cho/8dbLGUEfbzMS1qYOtq6JNzB8MUYbOUQMRw0jUOG6QVgIuZg8P0N5uQER9sxVTRDNycXiLZ7IFc3YEe6O5DyzjH6Bag9bpyeAriKuVvOtmTJ2SYBRtLJ/+nSFqAlPEyxuHFkMV7SANCkIdZ1G0QNxegB80rp0EljsUUSvEyhnNDAVcdOOszX2oEjrPhHKL0ZESdbPv6NcG+QR/TqQwPFh0+9HtEgyqvhYnmTIddIRdLpxhiCLmwugpumk1vPnFV5Gzvp6Xi7UT3TSUO4MSdhTFZQwLyF/1bdMr5ji5TBTFMjRV1QxLUdxp0JwmXTwIH0oxUikz8tBRNL2y2p4U66Mc/FEUtpRFY31t7IJ/VcQwNv5ZI5Q1KJQfnRX1QzCmquxDVL25TrpFxTgLsRKjmJDY4T9PLlSrpDMUY4MVYdeLqxIiMY4Of/HahSHKJJ5gCifckZr7szaqCm/HcpsESLJLi0mMtWDGDvwgYaZRop1fMuT72CswhRk3oUsxkCY0Drer8R+VM8er3I2rUjDfr/s4iYYAScYSp5C7ClMd60vGPP32CQZZVfRXNa4Me5WCl+1qnA+zglVoZTyLscXCGxsVtVPsX0lVXuMp6hAmkW16D2wcHaaQp2lJVVl0eUVQ0zliussVIqUQC+YdDPIZBmeVGLwAcrjE2CvkxwLvlR2t4Dj2BP5er5//n2PmgsHjzJE5pThD2wafZucQ8NEJZ54GpU7YqWdb44wICzlKOtvg6PlmFrGnZASOnWOp0gml8/TJIT5JoUOWUx99jS9g7bXyRsYxXmAFkvMZghMjVkMw8QMEsAz1af0zVGC2uZkeJODY1ZDz2lXrO02MXEonae0j24GAnLXYS7pAAKqk9YvVAT3vN9fPwBSE9f4sAENI3RZZsQ8eVVvUa/qMUGI05mUvxpxaMLj6zz/DBI+jwJ/B6MyqV1N7QkncZ6qPUYdWH1fZGUjYrq67w8g2rX8G5tia9pgHBwuNsPp58g+gwG6rXxkfQRWL+ok66P2aml0IlqDeXQ83MOxLq1+xq/iz3l/AMmDLHss1e/TI9aZuBRYybmcjBpVC10u4jQk99rgrHEHmNK//wgEsLm7W8AVMMZV6Y4prlPUYt20tMprX72gn/FHXv8v/UF6fEoIOTeot3QxH3dIIBnwJh/ioRwlTlLDeCO66mUNb6bUcZeYeSFj/rGFevtH0NfBRj3veecyVEKsbLT++ucV3OfFdzxJucQ7rzfwQ3LjXrodr4FgQwdb/TM9a+hYR8Bb1EYcPiY+2aPUrNqZga06G2LOlgRSQ5/Gr5Injxr+A9jLmFDLAVdJ5jx6/IWp7m2Ha1yqowZDG4xgleMZ46fFPgJG3ygk+g5Parihvugxe5JjSviNvLMVo13qHaKP9oC2MRFQpKUefXUosp8+ujEzjK9EeuxfWN0dnn7AWwFlqyRwkTJ8Y4NM01GnexiVMIu/h/zgqbs5xPkRGQfpe6zS4EA1eqQLEJ+rphiHc40shzx+sYS0f+t3NL3WwErwhTDVCrOYBJksdVitvbyIJe6+XvuVzUFOeT1iBKyde2WAKgzX4FI1TUq5+fXTss9QGBFWpiGdOsBg48kruLCYooMopR2J5QyfqU/2qXVCANeWvtRTPinhXjq2IzhY2L3CbxWagpKxdreeF7GGQBqdijfkVNpkYm6ymEBA4Rzxj4XG10ARfRI/9twtPKNEP3N58O8dtbkau+Zd8f5id8fCBqkXcem8Awbu67L/lGyNsj++yzGhijQhV5+HlL5cwKzcET9Z4Tc4uwk4FAQ7RjCHoMg4NFZngMEJhmBErYeFkmVNslBhbpqlGyoaDGcO1QaguQh8tdrU11g3NfEn1qkWIGZbnWe/HKqkxXzQ6gmrjo+x4sA+RYFPIofGVlXOYWNr/jW0jqlvHdd5YqLJxCjkbNj+Mfbk1iW/Yu+dcJwrMH2DFnntwohsnh3aWKFMIY5mPiN7Ym4jglQPZtCyKcprvd8FNE3mFcM4Q5JTXGCZxpLXJAAZjHxi3cQBYUbZuNQH8GHvI4rTmlXg32A/Ibwj7aTATHN3OBO8hxX3hPrdGv4BJnnHt8JTLGBJ/thFmCmF9XVi3HWgZ6n2v9ZmvJJBEPXcu7xPYpqMuBTGkH1QnLrhNmHdiOxgc9Jzbf8WHFIN1dUh5Bxqhnh46S/xCcgzAuzk245fYENd3X+l3sFO0m32wCGs3YRe/1C14jodqHXiMlQuGORYi5P7MYNpNqGxOMaVu1S720wRLrYvdTAxI1VC0M7IV5pZQovI2kdpi4/lRrf/yUy1+icXBJ91YVfa5iBOQfqaqbz7X8Do7Nm1j9E8V2ayfcNXjAyRNhniu8A82DJB4zd35jTjg63VBzlXWswMl4x+huckeXCETWEeRTHni4oHVGqaQCayjyADvVvCmD3Vm26ijxu198Z7BnXfafOyZB5Z7RoaA4doXUux5feTeDh9v32nXgdMvfoHV+PX9f3hF/Rbsvo96ghNe9nR3aJNV8ahoaW89UZXv3+kydgQvURDi6PZtbAdGq9/nMoZ4d8ioq0LPy1ld8fDrPfVcGw9jagJdZnKL3QRDG+7u/jfMreAB93ewLqVtWpfekhACbpK+bjwvYIG3WixbLiv/qoOD6f/k/V3YWHprOWgbb2rRji8eUeckE7x9JW3zKqo04xwuEhgzxQBVbWE88LQFJb8gWvvKeIou4/bJumCD1xIItc/UltUBL2stb8TgfoEtmsXviNa+skMfMG+OUwZV/HMWayOtPbgU2aTxnrotxrB9t5A+AXbaqG7DUpy5jIyYYDuFd3HF3kz+TW7BScc9uZ8cUdf4eI0lN60drvEetLPIxcPb7KptxXo1HEwx6RX7duvbVI5/VH/xQxbj0TdBN2HaY2f6qD7jj0bYIC3O7Y8Pgw1ThH33eHg/D4kFant6nBUWifWvUQuetiHWM7s4ApHgZaTK5VP4VpnR/s9SdAUW30j8d/g2nlqjZ7cahQJPXdC/TtnZmfVLMyYuDjoG9scx5HjDpyL4JtN9jC8x7mZ85MPbOeb/0/7PinTJal2VpiqfscML+L1CyIaSJ0iW6DMOK7xVHv0E99Di72WG1QpwgCsUVT39ewK+H5oFEdH/cy8i/OWk4CCIDjPJBP0XOp4HtwirC6HTvkfyKqqEEPyEWP/qQaf4Jd5Ec/m3HOFnVgdP4d04+48QnA//mqf/yvDfnkGJRCKRSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQieS3/AZzjrH8362VjAAAAAElFTkSuQmCC', 
    'file1',800,'image/png','/a contabilizar','mypathtofolder'),
    new Document('iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAkFBMVEX///8iHx8AAAAfHBwbGBgdGhoaFhYYFBQWEhL8/Pz5+fnm5uYRDAz29vYhHR3w8PCMi4s0MTEOCAgrKCjr6+vb29uEg4PDwsJDQUEmIyO4t7dXVVVMSkrl5OQKAADQz8+qqamamZmhoKBmZWU9Ozuwr6/MzMy8vLxubW1gXl4xLy9JR0d1dHRSUFB9fHxcWlo9FQvaAAALMUlEQVR4nO2de3OqOhDAG0J4KCggKKLy8oWK+v2/3d2lnXNPW4KoWNIz+f1z7wzUyZLNvrLJeXuTSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQikUgkEolEIpEIiB3s9tttmuZ5mm6j/W5l9z2iLvH32bQ8LN3JnBmWpdP5xF0eymk28/seWSckWXF2iWFojFJKKuB/mGYY8825yIK+x/ckdnaajzSVfojFmArAf95lpapGJqd80PcoH8aeHRTvXToNVPO4CcPTcrFYnsKNe5wz/f2RGivr2a9clP7+Gus4U6rFNotimu+Dsfn+aDAOomwKyqsaDN8wvMPvW5J+dLBQPmrQ8yXbr+reATHLk27gTOreYf+7ZJyVlXwsdi/bpEEFx7u0GHm0msfLzvy5AT5J4GwslE9x8+TmzAx3zuRdxjCrnWoB2S8oLC8au/mqlZm0A4fgF1HJYvbqsXWBmc1VkM9imd9a68xViVaJavPslUPrhuFZAZXT5sWdGpecq4lXTuPXjKszEtWAcerL6P4/zUIdvo3hJt2PqjvM2QQ0VJ1cho/8dbLGUEfbzMS1qYOtq6JNzB8MUYbOUQMRw0jUOG6QVgIuZg8P0N5uQER9sxVTRDNycXiLZ7IFc3YEe6O5DyzjH6Bag9bpyeAriKuVvOtmTJ2SYBRtLJ/+nSFqAlPEyxuHFkMV7SANCkIdZ1G0QNxegB80rp0EljsUUSvEyhnNDAVcdOOszX2oEjrPhHKL0ZESdbPv6NcG+QR/TqQwPFh0+9HtEgyqvhYnmTIddIRdLpxhiCLmwugpumk1vPnFV5Gzvp6Xi7UT3TSUO4MSdhTFZQwLyF/1bdMr5ji5TBTFMjRV1QxLUdxp0JwmXTwIH0oxUikz8tBRNL2y2p4U66Mc/FEUtpRFY31t7IJ/VcQwNv5ZI5Q1KJQfnRX1QzCmquxDVL25TrpFxTgLsRKjmJDY4T9PLlSrpDMUY4MVYdeLqxIiMY4Of/HahSHKJJ5gCifckZr7szaqCm/HcpsESLJLi0mMtWDGDvwgYaZRop1fMuT72CswhRk3oUsxkCY0Drer8R+VM8er3I2rUjDfr/s4iYYAScYSp5C7ClMd60vGPP32CQZZVfRXNa4Me5WCl+1qnA+zglVoZTyLscXCGxsVtVPsX0lVXuMp6hAmkW16D2wcHaaQp2lJVVl0eUVQ0zliussVIqUQC+YdDPIZBmeVGLwAcrjE2CvkxwLvlR2t4Dj2BP5er5//n2PmgsHjzJE5pThD2wafZucQ8NEJZ54GpU7YqWdb44wICzlKOtvg6PlmFrGnZASOnWOp0gml8/TJIT5JoUOWUx99jS9g7bXyRsYxXmAFkvMZghMjVkMw8QMEsAz1af0zVGC2uZkeJODY1ZDz2lXrO02MXEonae0j24GAnLXYS7pAAKqk9YvVAT3vN9fPwBSE9f4sAENI3RZZsQ8eVVvUa/qMUGI05mUvxpxaMLj6zz/DBI+jwJ/B6MyqV1N7QkncZ6qPUYdWH1fZGUjYrq67w8g2rX8G5tia9pgHBwuNsPp58g+gwG6rXxkfQRWL+ok66P2aml0IlqDeXQ83MOxLq1+xq/iz3l/AMmDLHss1e/TI9aZuBRYybmcjBpVC10u4jQk99rgrHEHmNK//wgEsLm7W8AVMMZV6Y4prlPUYt20tMprX72gn/FHXv8v/UF6fEoIOTeot3QxH3dIIBnwJh/ioRwlTlLDeCO66mUNb6bUcZeYeSFj/rGFevtH0NfBRj3veecyVEKsbLT++ucV3OfFdzxJucQ7rzfwQ3LjXrodr4FgQwdb/TM9a+hYR8Bb1EYcPiY+2aPUrNqZga06G2LOlgRSQ5/Gr5Injxr+A9jLmFDLAVdJ5jx6/IWp7m2Ha1yqowZDG4xgleMZ46fFPgJG3ygk+g5Parihvugxe5JjSviNvLMVo13qHaKP9oC2MRFQpKUefXUosp8+ujEzjK9EeuxfWN0dnn7AWwFlqyRwkTJ8Y4NM01GnexiVMIu/h/zgqbs5xPkRGQfpe6zS4EA1eqQLEJ+rphiHc40shzx+sYS0f+t3NL3WwErwhTDVCrOYBJksdVitvbyIJe6+XvuVzUFOeT1iBKyde2WAKgzX4FI1TUq5+fXTss9QGBFWpiGdOsBg48kruLCYooMopR2J5QyfqU/2qXVCANeWvtRTPinhXjq2IzhY2L3CbxWagpKxdreeF7GGQBqdijfkVNpkYm6ymEBA4Rzxj4XG10ARfRI/9twtPKNEP3N58O8dtbkau+Zd8f5id8fCBqkXcem8Awbu67L/lGyNsj++yzGhijQhV5+HlL5cwKzcET9Z4Tc4uwk4FAQ7RjCHoMg4NFZngMEJhmBErYeFkmVNslBhbpqlGyoaDGcO1QaguQh8tdrU11g3NfEn1qkWIGZbnWe/HKqkxXzQ6gmrjo+x4sA+RYFPIofGVlXOYWNr/jW0jqlvHdd5YqLJxCjkbNj+Mfbk1iW/Yu+dcJwrMH2DFnntwohsnh3aWKFMIY5mPiN7Ym4jglQPZtCyKcprvd8FNE3mFcM4Q5JTXGCZxpLXJAAZjHxi3cQBYUbZuNQH8GHvI4rTmlXg32A/Ibwj7aTATHN3OBO8hxX3hPrdGv4BJnnHt8JTLGBJ/thFmCmF9XVi3HWgZ6n2v9ZmvJJBEPXcu7xPYpqMuBTGkH1QnLrhNmHdiOxgc9Jzbf8WHFIN1dUh5Bxqhnh46S/xCcgzAuzk245fYENd3X+l3sFO0m32wCGs3YRe/1C14jodqHXiMlQuGORYi5P7MYNpNqGxOMaVu1S720wRLrYvdTAxI1VC0M7IV5pZQovI2kdpi4/lRrf/yUy1+icXBJ91YVfa5iBOQfqaqbz7X8Do7Nm1j9E8V2ayfcNXjAyRNhniu8A82DJB4zd35jTjg63VBzlXWswMl4x+huckeXCETWEeRTHni4oHVGqaQCayjyADvVvCmD3Vm26ijxu198Z7BnXfafOyZB5Z7RoaA4doXUux5feTeDh9v32nXgdMvfoHV+PX9f3hF/Rbsvo96ghNe9nR3aJNV8ahoaW89UZXv3+kydgQvURDi6PZtbAdGq9/nMoZ4d8ioq0LPy1ld8fDrPfVcGw9jagJdZnKL3QRDG+7u/jfMreAB93ewLqVtWpfekhACbpK+bjwvYIG3WixbLiv/qoOD6f/k/V3YWHprOWgbb2rRji8eUeckE7x9JW3zKqo04xwuEhgzxQBVbWE88LQFJb8gWvvKeIou4/bJumCD1xIItc/UltUBL2stb8TgfoEtmsXviNa+skMfMG+OUwZV/HMWayOtPbgU2aTxnrotxrB9t5A+AXbaqG7DUpy5jIyYYDuFd3HF3kz+TW7BScc9uZ8cUdf4eI0lN60drvEetLPIxcPb7KptxXo1HEwx6RX7duvbVI5/VH/xQxbj0TdBN2HaY2f6qD7jj0bYIC3O7Y8Pgw1ThH33eHg/D4kFant6nBUWifWvUQuetiHWM7s4ApHgZaTK5VP4VpnR/s9SdAUW30j8d/g2nlqjZ7cahQJPXdC/TtnZmfVLMyYuDjoG9scx5HjDpyL4JtN9jC8x7mZ85MPbOeb/0/7PinTJal2VpiqfscML+L1CyIaSJ0iW6DMOK7xVHv0E99Di72WG1QpwgCsUVT39ewK+H5oFEdH/cy8i/OWk4CCIDjPJBP0XOp4HtwirC6HTvkfyKqqEEPyEWP/qQaf4Jd5Ec/m3HOFnVgdP4d04+48QnA//mqf/yvDfnkGJRCKRSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQieS3/AZzjrH8362VjAAAAAElFTkSuQmCC',
    'file2',900,'image/png','/contabilizado','mypathtofolder2'),
    new Document('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', 
    'file3',1000,'image/png','/papelera','mypathtofolder3')
]

let documentNotes =[
    new DocumentNote('Lorem Ipsum is simply dummy text ..','mypathtofolder'),
    new DocumentNote('Vivamus pretium egestas massa feugiat ..','mypathtofolder2'),
    new DocumentNote('Maecenas turpis lacus, sodales at suscipit in.. ..','mypathtofolder3')
]

let banks = [
    new Banks('Caixa Bank',1500,'mypathtofolder3'),
    new Banks('Banco Nación',500,'mypathtofolder3'),
    new Banks('Bankinter',2500,'mypathtofolder3')
]

let taxModels = [
    new TaxModel( 180,'IVA','en proceso','domicialición bancaria','result1',4,2021),
    new TaxModel( 303,'IVA','pendiente','tranferencia','result2',2,2020),
    new TaxModel( 180,'IVA','rectificado','domicialición bancaria','result1',1,2022),
    new TaxModel( 180,'IVA','presentado','domicialición bancaria','result1',2,2022)
]

let messages = [
    new Message(1,'Maria Rico Gómez','Asunto 1','sunt in culpa qui officia deserunt',new Date("2021-01-16"),'consulta','pendiente',new Date("2021-05-16")),
    new Message(2,'Jesús Pérez Álvarez','Asunto 2','sunt in culpa qui officia deserunt',new Date("2022-01-16"),'tarea','Nueva',new Date("2022-05-16")),
    new Message(3,'Juan Carlos Aragón Pérez','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-01-16"),'notificación','Abierta',new Date("2023-05-16"))
]

let messageChats = [
    new MessageChat(1,1,'Maria Rico Gómez' ,'enim ad minim veniam, quis nostrud..',new Date,'type'),
    new MessageChat(2,1,'Yo','sunt in culpa qui officia deserunt..',new Date,'type'),
    new MessageChat(3,1,'Maria Rico Gómez','mollit anim id est laborum..',new Date,'type')
]

let employees = [
    new Employee('Maria Rico','Gómez','48150243L','exampleemail@gmail.com','690619302','390423363729',true),
    new Employee('Maria Rico','Álvarez','86638678R','exampleemail@gmail.com','656796396','650423363729',false),
    new Employee('Juan Carlos','Aragón Pérez','11556837G','exampleemail@gmail.com','619068048','490423363729',true)
]

