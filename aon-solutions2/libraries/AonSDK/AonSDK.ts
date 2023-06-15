const ERRORS = {
    '0000': {description:'OK. OPERATION SUCCED.', result:''},
    '0101': {description:'ERROR_LOGIN_CREDENTIALS- INVALID CREDENTIALS', result:''},
    '0201': {description:'ERROR_MODEL', result:'Error al intentar acceder al modelo'},
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
        if(code.slice(0,2) == '00'){
            this.code = code;
            this.description = ERRORS[code as keyof typeof ERRORS].description;
            this.result = result;
        }else{
            this.code = code;
            this.description = ERRORS[code as keyof typeof ERRORS].description;
            this.result = ERRORS[code as keyof typeof ERRORS].result;
        }
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
    filterFields?: {}
}

interface Collection {
    
}

class Enterprise implements Collection {

    private _name: string = '';
    private _document: string = '';
    
    constructor(name : string, document: string){
        this._name = name;
        this._document = document;
    }

    public get document(): string {
        return this._document;
    }

    public set document(value: string) {
        this._document = value;
    }

    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }
    
}

class Folder implements Collection {

    private _name: string;
    private _path: string;
    
    
    constructor(name: string, path: string){
        this._name = name;
        this._path = path;
    }

    public get path(): string {
        return this._path;
    }

    public set path(value: string) {
        this._path = value;
    }

    public get name(): string {
        return this._name;
    }

    public set name(value: string) {
        this._name = value;
    }
}

interface IFactory {
    buildModel(objType: string) : any;
    buildService(obj:string): any;
}

class Factory implements IFactory {

    constructor(){}

    buildModel(objType: string): any {
        
        switch(objType){
            case 'enterprise':
                return new Enterprise('','');
            case 'folder':
                return new Folder('','');
            default:
                throw new Response('0201');
        }
    }

    buildService(objType: string): any {
        switch(objType){
            case 'enterprise':
                return new ServiceEnterprise();
            case 'folder':
                return new ServiceFolder();
            default:
                throw new Response('0201');
        }
    }
}

interface IService {
    getElement(model: string, pKey: any) : Promise<Collection>;
    getElementList(model: string, optional?: Optional) : Promise<Collection[]>;
    createElement(model: string, collection: Collection[]) : Promise<any>;
    updateElement(model: string, collection: Collection[]) : Promise<any>;
    deleteElement(model: string, pKey: any) : Promise<any>;
}

class ServiceEnterprise implements IService {

    getElement(model: string, pKey: any) : Promise<Collection> { 
        return new Promise((resolve, reject) => {
            resolve(new Factory().buildModel(model))
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Collection[]> {
        return new Promise((resolve, reject) => {
            resolve([
                new Factory().buildModel(model),
                new Factory().buildModel(model),
            ])
        });
    }

    createElement(model: string, collection: Collection[]) : Promise<any> {throw new Error('En construccion')}

    updateElement(model: string, collection: Collection[]) : Promise<any> {throw new Error('En construccion')}

    deleteElement(model: string, pKey: any) : Promise<any> {throw new Error('En construccion')}
}

class ServiceFolder implements IService {

    getElement(model: string, pKey: any) : Promise<Collection> {
        return new Promise((resolve, reject) => {
            resolve(new Factory().buildModel(model))
        });
    }

    getElementList(model: string, optional?: Optional) : Promise<Collection[]> {
        return new Promise((resolve, reject) => {
            resolve([
                new Factory().buildModel(model),
                new Factory().buildModel(model),
            ])
        });
    }

    createElement(model:string, collection: Collection[]) : Promise<any> {throw new Error('En construccion')}

    updateElement(model:string, collection: Collection[]) : Promise<any> {throw new Error('En construccion')}

    deleteElement(model:string, pKey: any) : Promise<any> {throw new Error('En construccion')}

    downloadFolder(): Promise<any> {
        // return 'path to download'
        return new Promise((resolve, reject) => {
            fetch('https://dummyjson.com/products/1')
            .then(res => {
                throw new Response('0101');
                // return Promise.reject(new Error('232w'));
                // resolve(res.json());
            })
            .catch((error) => {
                // throw new Error(error)
                // Promise.reject(error);
                reject(error);
            })
        });
    }

}

class ServiceAuth {

    login(username : string, password : string): Promise<boolean> {
        return new Promise((resolve, reject) => {
            if( username == 'prueba' && password == 'prueba'){
                resolve(true)
            }else{
                throw new Response('0101')
            }
        });
    }

    logout() : Promise<boolean> {
        return new Promise((resolve, reject) => {
            resolve(true)
        });
    }
}



export class AonSDK {

    factory: Factory;    

    constructor(){
        this.factory = new Factory();
    }

    model(model : any){
        return this.factory.buildService(model);
    }
    
    login(username: string, password: string): Promise<IResponse> {
        return new Promise((resolve,reject) => {
            new ServiceAuth().login(username,password).then((response: any) => {
                resolve(new Response('0000', response));
            }).catch((error: any) => {
                reject(error);
            })
        });
    }

    logout(): Promise<IResponse> {
        return new Promise((resolve,reject) => {
            new ServiceAuth().logout().then((response: any) => {
                resolve(new Response('0000', response));
            }).catch((error: any) => {
                reject(error);
            })
        });
    }

    downloadFolder(): Promise<IResponse> {
        return new Promise((resolve,reject) => {
            this.factory.buildService('folder').downloadFolder().then((response: any) => {
                    resolve(new Response('0000', response));
                }).catch((error: any) => {
                    reject(error);
                })
        });
    }

    getElement(model: string, pKey: any): Promise<IResponse> {
        return new Promise((resolve,reject) => {
            this.factory.buildService(model).getElement(model, '/folder').then((response: any) => {
                    resolve(new Response('0000', response));
                }).catch((error: any) => {
                    reject(error);
                })
        });
    }

    getElementList(model: string, optional?: Optional): Promise<IResponse> {
        return new Promise((resolve,reject) => {
            this.factory.buildService(model).getElementList(model, '/folder').then((response: any) => {
                    resolve(new Response('0000', response));
                }).catch((error: any) => {
                    reject(error);
                })
        });
    }

}