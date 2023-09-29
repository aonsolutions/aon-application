import { IModel, IApiModel, IStorable } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrudRepository, IMultipleObjectCrudRepository } from "../interfaces/repositoryInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";
import { ApiHttpRequest } from "../utils/Http";
import { LocalStorage } from "../utils/LocalStorage";
import { ErrorResponse } from "../utils/Response";

export class APIGenericSingleObjectCrudRepository<T extends IModel> implements ISingleObjectCrudRepository<T> {
    protected type: { new (): T };
    protected model: IModel;
    protected apiModel: IApiModel;

    constructor(apiModel: IApiModel, type: { new (): T }){
        this.type = type;
        this.model = new this.type();
        this.apiModel = apiModel;
    }

    async get(key: string, type?: string): Promise<T> {
        console.log(this.type, '<= type from Generic method getsingle')
        throw new ErrorResponse('0199');
        // let filter = new FilterBuilder();
        // filter.addField('id',key);
        // if(type) filter.addField('type',type)
        // let url = this.apiModel.getUrl(GET_SINGLE, filter.getFilter());
        // let method = this.apiModel.getMethod(GET_SINGLE, filter.getFilter());
        // let response = await this.httpRequest.httpRequest(BASE_URL + url, method, {}, {})
        // return this.apiModel.parseDataToReceive(response, GET_SINGLE);
    }

    async create(element: T): Promise<T> {
        console.log(this.type, '<= type from Generic method createsingle')
        throw new ErrorResponse('0199');
        // let data = this.apiModel.parseDataToSend(element, CREATE_SINGLE);
        // let response = await this.httpRequest.httpRequest(BASE_URL + this.apiModel.getUrl(CREATE_SINGLE), this.apiModel.getMethod(CREATE_SINGLE), {}, data);
        // return this.apiModel.parseDataToReceive(response, GET_SINGLE);
    }

    async update(element: T): Promise<T> {
        console.log(this.type, '<= type from Generic method updatesingle')
        throw new ErrorResponse('0199');
        // let filter = new FilterBuilder();
        // filter.addField('id', element.Key);
        // let data = this.apiModel.parseDataToSend(element, UPDATE_SINGLE);
        // let url = this.apiModel.getUrl(UPDATE_SINGLE, filter.getFilter());
        // let method = this.apiModel.getMethod(UPDATE_SINGLE);
        // let response = await this.httpRequest.httpRequest(BASE_URL + url, method, element, data)
        // if(response) return element;
        // throw new ErrorResponse('0199')
    }

    async delete(key: string): Promise<void> {
        console.log(this.type, '<= type from Generic method deletesingle')
        throw new ErrorResponse('0199')
    }

}

// REPOSITORIO PARA LAS LLAMADAS GLOBALES A LA API PARA OPERACIONES CRUD SOBRE UN CONJUNTO DE OBJETOS,
// SI SE NECESITA UN COMPORTAMIENTO ESPECIFICO HEREDAR Y SOBREESCRIBIR DICHO MÉTODO
export class APIGenericMultipleObjectCrudRepository<T extends IModel> implements IMultipleObjectCrudRepository<T> {

    protected type: { new (): T };
    protected model: IModel;
    protected apiModel: IApiModel;

    constructor(apiModel: IApiModel, type: { new (): T }){
        this.type = type;
        this.model = new this.type();
        this.apiModel = apiModel;
    }

    async get(filter?: IFilter): Promise<ICollection<T>> {
        console.log(this.type, '<= type from Generic method getmultiple')
        throw new ErrorResponse('0199');
        // let urls = this.apiModel.getUrl(GET_MULTIPLE,filter);
        // let method = this.apiModel.getMethod(GET_MULTIPLE,filter);
        // let collection: ICollection<T> = new Collection<T>();
        // for(let url of urls){
        //     let response = await this.httpRequest.httpRequest(BASE_URL + url, method, {}, {})
        //     response.forEach((element: any) => {
        //         collection.add(this.apiModel.parseDataToReceive(element, GET_MULTIPLE, filter))
        //     })
        // }
        // if(this.apiModel.localFilter() && collection.size() > 0){
        //     if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
        //     if(filter?.orderBy) collection.sort(filter);
        //     if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        // }
        // return collection;
    }

    async create(collection: ICollection<T>): Promise<ICollection<T>> {
        console.log(this.type, '<= type from Generic method createmultiple')
        throw new ErrorResponse('0199')
    }

    async update(collection: ICollection<T>): Promise<ICollection<T>> {
        console.log(this.type, '<= type from Generic method updatemultiple')
        throw new ErrorResponse('0199')
    }

    async delete(keys: string[]): Promise<void> {
        console.log(this.type, '<= type from Generic method deletemultiple')
        throw new ErrorResponse('0199')
    }

}

export class GenericMultipleObjectCrudRepository<T extends IModel> implements IMultipleObjectCrudRepository<T> {
    private instance: IStorable<T>;
    private localStorageManager: LocalStorage<T>;
    private type: { new (): T };

    constructor(instance: IStorable<T>, type: { new (): T }) {
        this.instance = instance;
        this.localStorageManager = new LocalStorage<T>(type);
        this.type = type;
    }

    async get(filter?: IFilter): Promise<ICollection<T>> {
        let collection = this.instance.getCollection()
        if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
        if(filter?.orderBy) collection.sort(filter);
        if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        return collection;
    }

    async create(collection: ICollection<T>): Promise<ICollection<T>> {
        collection.toArray().forEach(element => {
            if(this.instance.getCollection().exists(element.getKey())){
                throw new ErrorResponse('0201');
            }
        })
        collection.toArray().forEach(element => {
            element.Key = element.getKey();
            this.instance.getCollection().add(element);
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        return collection;
    }

    async update(collection: ICollection<T>): Promise<ICollection<T>> {
        collection.toArray().forEach(element => {
            if(!(element.getKey() != element.Key && !this.instance.getCollection().exists(element.getKey()) && this.instance.getCollection().exists(element.Key)
            || element.getKey() == element.Key && this.instance.getCollection().exists(element.Key)))
                throw new ErrorResponse('0205');
        })
        collection.toArray().forEach(element => {
            this.instance.getCollection().remove(element.Key);
            element.Key = element.getKey();
            this.instance.getCollection().add(element)
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        return collection;
    }

    async delete(keys: string []): Promise<void> {
        keys.forEach(key => {
            if(!this.instance.getCollection().exists(key))
                throw new ErrorResponse('0204');
        })
        keys.forEach(key => {
            this.instance.getCollection().remove(key);
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
    }
}

export class GenericSingleObjectCrudRepository<T extends IModel> implements ISingleObjectCrudRepository<T> {
    private instance: IStorable<T>;
    private localStorageManager: LocalStorage<T>;
    private type: { new (): T };

    constructor(instance: IStorable<T>, type: { new (): T }) {
        this.instance = instance;
        this.localStorageManager = new LocalStorage<T>(type);
        this.type = type;
    }

    async get(key: string): Promise<T> {

        if(this.instance.getCollection().exists(key))
            return this.instance.getCollection().get(key);
        else
            throw new ErrorResponse('0206');
    }

    async create(element: T): Promise<T> {
        if(!this.instance.getCollection().exists(element.getKey())){
            element.Key = element.getKey()
            this.instance.getCollection().add(element);
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
            return element;
        }else{
            throw new ErrorResponse('0201');
        }
    }

    async update(element: T): Promise<T> {
        if(element.getKey() != element.Key && !this.instance.getCollection().exists(element.getKey()) && this.instance.getCollection().exists(element.Key)
        || element.getKey() == element.Key && this.instance.getCollection().exists(element.Key)){
            this.instance.getCollection().remove(element.Key);
            element.Key = element.getKey();
            this.instance.getCollection().add(element)
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
            return element;
        }else{
            throw new ErrorResponse('0202');
        }
    }

    async delete(key: string): Promise<void> {
        if(this.instance.getCollection().exists(key)){
            this.instance.getCollection().remove(key);
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        }else throw new ErrorResponse('0203');
    }
}


