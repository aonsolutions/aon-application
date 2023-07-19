/**
 * 
 * ERROR DATA
 * 
 */

const ERRORS = {
    /* 00XX */
    '0000' : {description:'OK_OPERATION_SUCCEED', result:''},
    /* 01XX */
    '0101': {description:'ERROR_LOGIN_INVALID_CREDENTIALS', result:'Error al iniciar sesión'},
    '0111': {description:'SESSION_EXPIRED', result:'No existe sesión'},
    '0112': {description:'ERROR_SESSION', result:'No se ha seleccionado empresa'},
    '0113': {description:'ERROR_SESSION', result:'Error al seleccionar la empresa'},
    '0123': {description:'ERROR_MODEL', result:'Error al intentar acceder al modelo'},
    '0124': {description:'ERROR_DENIED', result:'No tiene permiso para acceder al recurso'},
    '0199': {description:'ERROR_NOT_IMPLEMENTED', result:'Paciencia amigo, paciencia'},
    /* 02XX */
    '0201': {description:'ERROR_MODEL_CREATE', result:'Error en la creación'},
    '0202': {description:'ERROR_MODEL_UPDATE', result:'Error en la edición'},
    '0203': {description:'ERROR_MODEL_DELETE', result:'Error al intentar eliminar'},
    '0204': {description:'ERROR_MODEL_FILTER', result:'Los filtros introducidos son incorrectos'},
    '0205': {description:'ERROR_MODEL_ELEMENT', result:'No se encontró ningún elemento'},
    '0206': {description:'ERROR_MODEL_ELEMENT', result:'Error al intentar obtener el elemento'},
}

/*
 * 
 * FACTORYS INTERFACES
 * 
 */

interface ISingleObjectCrudFactory<T extends ICollectable> {
    createSingleObjectCrud(): ISingleObjectCreator<T> | ISingleObjectUpdater<T> | ISingleObjectRemover<T> | ISingleObjectReader<T>;
}

interface IMultipleObjectCrudFactory<T extends ICollectable> {
    createMultipleObjectCrud(): IMultipleObjectCreator<T> | IMultipleObjectUpdater<T> | IMultipleObjectRemover<T> | IMultipleObjectReader<T>;
}

interface IAuthenticationManagerFactory {
    createAuthenticationManager(): IAuthenticationManager;
}

interface IReportingDataAccessFactory {
    createReportingDataAccess(): IReportingDataAccess;
}

/*
 * 
 * CONCRETE FACTORYS IMPLEMENTATIONS
 * 
 */

export class DocumentFactory implements ISingleObjectCrudFactory<IDocument>, IMultipleObjectCrudFactory<IDocument> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocument> {
        return new GenericSingleObjectCrud<IDocument>(new GenericSingleObjectCrudRepository<IDocument>(new StorableDocument(), Document), Document);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocument> {
        return new GenericMultipleObjectCrud<IDocument>(new GenericMultipleObjectCrudRepository<IDocument>(new StorableDocument(), Document), Document);
    }
}

export class FolderFactory implements ISingleObjectCrudFactory<IFolder>, IMultipleObjectCrudFactory<IFolder> {
    createSingleObjectCrud(): ISingleObjectReader<IFolder> {
        return new GenericSingleObjectCrud<IFolder>(new GenericSingleObjectCrudRepository<IFolder>(new StorableFolder(), Folder), Folder);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IFolder> {
        return new GenericMultipleObjectCrud<IFolder>(new GenericMultipleObjectCrudRepository<IFolder>(new StorableFolder(), Folder), Folder);
    }
}

export class DocumenNoteFactory implements ISingleObjectCrudFactory<IDocumentNote>, IMultipleObjectCrudFactory<IDocumentNote> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocumentNote> {
        return new GenericSingleObjectCrud<IDocumentNote>(new GenericSingleObjectCrudRepository<IDocumentNote>(new StorableDocumentNote(), DocumentNote), DocumentNote);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocumentNote> {
        return new GenericMultipleObjectCrud<IDocumentNote>(new GenericMultipleObjectCrudRepository<IDocumentNote>(new StorableDocumentNote(), DocumentNote), DocumentNote);
    }
}

export class MessageFactory implements ISingleObjectCrudFactory<IMessage>, IMultipleObjectCrudFactory<IMessage> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessage> {
        return new GenericSingleObjectCrud<IMessage>(new GenericSingleObjectCrudRepository<IMessage>(new StorableMessage(), Message), Message);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessage> {
        return new GenericMultipleObjectCrud<IMessage>(new GenericMultipleObjectCrudRepository<IMessage>(new StorableMessage(), Message), Message);
    }
}

export class MessageChatFactory implements ISingleObjectCrudFactory<IMessageChat>, IMultipleObjectCrudFactory<IMessageChat> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessageChat> {
        return new GenericSingleObjectCrud<IMessageChat>(new GenericSingleObjectCrudRepository<IMessageChat>(new StorableMessageChat(), MessageChat), MessageChat);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessageChat> {
        return new GenericMultipleObjectCrud<IMessageChat>(new GenericMultipleObjectCrudRepository<IMessageChat>(new StorableMessageChat(), MessageChat), MessageChat);
    }
}

export class EnterpriseFactory implements ISingleObjectCrudFactory<IEnterprise>, IMultipleObjectCrudFactory<IEnterprise> {
    createSingleObjectCrud(): ISingleObjectReader<IEnterprise> {
        return new GenericSingleObjectCrud<IEnterprise>(new GenericSingleObjectCrudRepository<IEnterprise>(new StorableEnterprise(), Enterprise), Enterprise);
    }
    createMultipleObjectCrud(): IMultipleObjectReader<IEnterprise> {
        return new GenericMultipleObjectCrud<IEnterprise>(new GenericMultipleObjectCrudRepository<IEnterprise>(new StorableEnterprise(), Enterprise), Enterprise);
    }
}

export class BankFactory implements ISingleObjectCrudFactory<IBank>, IMultipleObjectCrudFactory<IBank> {
    createSingleObjectCrud(): ISingleObjectCrud<IBank> {
        return new GenericSingleObjectCrud<IBank>(new GenericSingleObjectCrudRepository<IBank>(new StorableBank(), Bank), Bank);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IBank> {
        return new GenericMultipleObjectCrud<IBank>(new GenericMultipleObjectCrudRepository<IBank>(new StorableBank(), Bank), Bank);
    }
}

export class TaxModelFactory implements ISingleObjectCrudFactory<ITaxModel>, IMultipleObjectCrudFactory<ITaxModel> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaxModel> {
        return new GenericSingleObjectCrud<ITaxModel>(new GenericSingleObjectCrudRepository<ITaxModel>(new StorableTaxModel(), TaxModel), TaxModel);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ITaxModel> {
        return new GenericMultipleObjectCrud<ITaxModel>(new GenericMultipleObjectCrudRepository<ITaxModel>(new StorableTaxModel(), TaxModel), TaxModel);
    }
}

export class EmployeeFactory implements ISingleObjectCrudFactory<IEmployee>, IMultipleObjectCrudFactory<IEmployee> {
    createSingleObjectCrud(): ISingleObjectCrud<IEmployee> {
        return new GenericSingleObjectCrud<IEmployee>(new GenericSingleObjectCrudRepository<IEmployee>(new StorableEmployee(), Employee), Employee);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IEmployee> {
        return new GenericMultipleObjectCrud<IEmployee>(new GenericMultipleObjectCrudRepository<IEmployee>(new StorableEmployee(), Employee), Employee);
    }
}

export class AuthenticationFactory implements IAuthenticationManagerFactory {
    createAuthenticationManager(): IAuthenticationManager {
        return new AuthenticationManager(new AuthenticationRepository(new StorableAuth()));
    }
}

export class ReportingFactory implements IReportingDataAccessFactory {
    createReportingDataAccess(): IReportingDataAccess {
        return new ReportingDataAccess();
    }
}

/*
 * 
 * INTERFACES TO DEFINE METHODS FOR CLIENT
 * 
 */

interface ISingleObjectReader<T extends ICollectable> {
    getElement(key: string): Promise<IResponse<T>>;
}

interface ISingleObjectCreator<T extends ICollectable> {
    createElement(element: T): Promise<IResponse<T>>;
}

interface ISingleObjectUpdater<T extends ICollectable> {
    updateElement(element: T): Promise<IResponse<T>>;
}

interface ISingleObjectRemover<T extends ICollectable> {
    deleteElement(key: string): Promise<IResponse<boolean>>;
}

interface ISingleObjectCrud<T extends ICollectable> extends ISingleObjectReader<T>, ISingleObjectCreator<T>, ISingleObjectUpdater<T>, ISingleObjectRemover<T> {}

interface IMultipleObjectReader<T extends ICollectable> {
    getCollection(filter?: IFilter): Promise<IResponse<ICollection<T>>>;
}

interface IMultipleObjectCreator<T extends ICollectable> {
    createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>>;
}

interface IMultipleObjectUpdater<T extends ICollectable> {
    updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>>;
}

interface IMultipleObjectRemover<T extends ICollectable> {
    deleteCollection(keys: string []): Promise<IResponse<boolean>>;
}

interface IMultipleObjectCrud<T extends ICollectable> extends IMultipleObjectReader<T>, IMultipleObjectCreator<T>, IMultipleObjectUpdater<T>, IMultipleObjectRemover<T> {}

interface IAuthenticationManager {
    login(email: string, password: string): Promise<IResponse<boolean>>;
    logout(): Promise<IResponse<boolean>>;
    tokenLogin(token: string): Promise<IResponse<boolean>>;
    isAuthenticated(): Promise<IResponse<boolean>>;
    isEnterpriseSelected(): Promise<IResponse<boolean>>;
    setEnterprise(enterprise: IEnterprise): Promise<IResponse<boolean>>;
}

interface IReportingDataAccess {
    cobrosPagos(): Promise<IResponse<any>>;
    ventasGastos(): Promise<IResponse<any>>;
}

/*
 * 
 * IMPLEMENTATION OF INTERFACES FOR THE CLIENTS
 * 
 */


class GenericSingleObjectCrud<T extends ICollectable> implements ISingleObjectCrud<T> {
    private repository: ISingleObjectCrudRepository<T>;
    private type: { new (): T };

    constructor(repository: ISingleObjectCrudRepository<T>, type: { new (): T }){
        this.repository = repository;
        this.type = type;
    }

    async createElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.create(element));
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0201');
        }
    }

    async updateElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.update(element));
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0202');
        }
    }

    async deleteElement(key: string): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(key);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0203');
        }
    }

    async getElement(key: string): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.get(key));
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0206');
        }
    }
}

class GenericMultipleObjectCrud<T extends ICollectable> implements IMultipleObjectCrud<T> {
    private repository: IMultipleObjectCrudRepository<T>;
    private type: { new (): T };

    constructor(repository: IMultipleObjectCrudRepository<T>, type: { new (): T }){
        this.repository = repository;
        this.type = type;
    }

    async getCollection(filter?: IFilter): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.get(filter));
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0206');
        }
    }

    async createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.create(collection));
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0201');
        }
    }

    async updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.update(collection));
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0202');
        }
    }

    async deleteCollection(keys: string[]): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(keys);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0203');
        }
    }
}

class AuthenticationManager implements IAuthenticationManager {

    authenticationRepository: IAuthenticationRepository;

    constructor(authenticationRepository: IAuthenticationRepository) {
        this.authenticationRepository = authenticationRepository;
    }

    async login(email: string, password: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.login(email,password)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0101');
        }
    }

    async logout(): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.logout()
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0111');
        }
    }

    async tokenLogin(token: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.tokenLogin(token)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof Response ?  error : new Response<string>('0101');
        }
    }

    async isAuthenticated(): Promise<IResponse<boolean>> {
        return new Response<boolean>(localStorage.getItem('token') ? true : false);
    }

    async isEnterpriseSelected(): Promise<IResponse<boolean>> {
        return new Response<boolean>(localStorage.getItem('enterprise') ? true : false);
    }

    async setEnterprise(enterprise: IEnterprise): Promise<IResponse<boolean>> {
        if(enterprise && enterprise.getKey()) localStorage.setItem('enterprise', enterprise.getKey());
        else throw new Response('0113');
        return new Response<boolean>(true);
    }
}

class ReportingDataAccess implements IReportingDataAccess {
    async cobrosPagos(): Promise<any> {
        let datasets: any[] = [], ventas: any[] = [], gastos: any[] = [], label: any[] = [];
        for(let i = 0; i < 12; i++){
            ventas.push(Math.floor(Math.random()*2000))
            gastos.push(Math.floor(Math.random()*2000))
        }
        datasets.push({data:ventas, label:'ventas'})
        datasets.push({data:gastos, label:'gastos'})
        return { datasets: datasets, label: [1,2,3,4,5,6,7,8,9,10,11,12] }    
    }
    async ventasGastos(): Promise<any> {
        let datasets: any[] = [], cobros: any[] = [], pagos: any[] = [], label: any[] = [];
        for(let i = 0; i < 12; i++){
            cobros.push(Math.floor(Math.random()*2000))
            pagos.push(Math.floor(Math.random()*2000))
        }
        datasets.push({data:cobros, label:'cobros'})
        datasets.push({data:pagos, label:'pagos'})
        return { datasets: datasets, label: [1,2,3,4,5,6,7,8,9,10,11,12] }    
    }
}

/*
 * 
 * REPOSITORY INTERFACES
 * 
 */

interface IRepositorySingleObjectReader<T> {
    get(key: string): Promise<T>;
}

interface IRepositorySingleObjectCreator<T> {
    create(element: T): Promise<T>;
}

interface IRepositorySingleObjectUpdater<T> {
    update(element: T): Promise<T>;
}

interface IRepositorySingleObjectRemover<T> {
    delete(key: string): Promise<void>;
}

interface IRepositoryMultipleObjectReader<T extends ICollectable> {
    get(filter?: IFilter): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectCreator<T extends ICollectable> {
    create(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectUpdater<T extends ICollectable> {
    update(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectRemover<T extends ICollectable> {
    delete(keys: string []): Promise<void>;
}

interface ISingleObjectCrudRepository<T extends ICollectable> extends IRepositorySingleObjectReader<T>, IRepositorySingleObjectCreator<T>, IRepositorySingleObjectUpdater<T>, IRepositorySingleObjectRemover<T> {}

interface IMultipleObjectCrudRepository<T extends ICollectable> extends IRepositoryMultipleObjectReader<T>, IRepositoryMultipleObjectCreator<T>, IRepositoryMultipleObjectUpdater<T>, IRepositoryMultipleObjectRemover<T> {}

interface IAuthenticationRepository {
    login(email: string, password: string): Promise<void>;
    logout(): Promise<void>;
    tokenLogin(token: string): Promise<void>;
}

/*
 * 
 * CONCRETE REPOSITORY IMPLEMENTATION
 * 
 */

class GenericMultipleObjectCrudRepository<T extends ICollectable> implements IMultipleObjectCrudRepository<T> {
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
                throw new Response<string>('0201');
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
                throw new Response<string>('0205');
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
                throw new Response<string>('0204');
        })
        keys.forEach(key => {
            this.instance.getCollection().remove(key);
        })
        this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
    }
}

class GenericSingleObjectCrudRepository<T extends ICollectable> implements ISingleObjectCrudRepository<T> {
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
            throw new Response<string>('0206');
    }

    async create(element: T): Promise<T> {
        if(!this.instance.getCollection().exists(element.getKey())){
            element.Key = element.getKey()
            this.instance.getCollection().add(element);
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
            return element;
        }else{
            throw new Response<string>('0201');
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
            throw new Response<string>('0202');
        }
    }

    async delete(key: string): Promise<void> {
        if(this.instance.getCollection().exists(key)){
            this.instance.getCollection().remove(key);
            this.localStorageManager.write(this.instance.getLocalStorage(), this.instance.getCollection())
        }else throw new Response<string>('0203');
    }
}

class AuthenticationRepository implements IAuthenticationRepository {

    private storableAuth: IStorable<IAuth>;

    constructor(storableAuth: IStorable<IAuth>) {
        this.storableAuth = storableAuth;
    }

    async login(email: string, password: string): Promise<void> {
        let collection = this.storableAuth.getCollection()
        if(collection.exists(email) && collection.get(email).Password == password) localStorage.setItem('token', 'testToken')
        else throw new Response<string>('0101');
    }

    async logout(): Promise<void> {
        if(localStorage.getItem('token')) { localStorage.removeItem('token'); localStorage.removeItem('enterprise'); }
        else throw new Response<string>('0111');
    }

    async tokenLogin(token: string): Promise<void> {
        if(!localStorage.getItem('token')) localStorage.setItem('token', 'testToken');
        else throw new Response<string>('0101');
    }
}

/*
 * 
 * UTILITIES INTERFACE, FOR EXAMPLE COLLECTION TO MANAGE A LIST OF OBJECTS
 * 
 */

interface IResponse<T> {
    code: string;
    description: string;
    result: T | string;
}

interface ILocalStorage<T extends ICollectable> {
    read(model:string, instance: T): ICollection<T>;
    write(model:string, collection: ICollection<T>): void;
}

export interface ICollection<T extends ICollectable> extends Iterable<T> {
    size(): number;
    isEmpty(): boolean;
    exists(key: string): boolean;
    get(key: string): T;
    add(element: T): void;
    remove(key: string): void;
    sort(filter: IFilter): void;
    forEach(callbackfn: (value: T, index: string) => void): void;
    filter(filter: IFilter): ICollection<T>;
    slice(start: number, end: number): ICollection<T>;
    paginate(pageNum: number, totalPage:number): ICollection<T>;
    toArray(): T[];
    copyArrayToCollection(array: T[]): void;
}

export interface IFilter {
    selectedFields?: string[];
    pageNum?: number;
    pageItems?: number;
    fields?: Map<string,any>;
    intervalFields?: Map<string,any>;
    orderBy?: Map<string,string>;
}

/*
 * 
 * CONCRETE IMPLEMENTATION OF UTILITIES INTERFACES
 * 
 */

class KeyGenerator {
    characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    constructor(){}
    generate(length: number): string {
        let result = ''
        for (let i = 0; i < length; i++) {
            const randomIndex = Math.floor(Math.random() * this.characters.length);
            result += this.characters.charAt(randomIndex);
        }
        return result;
    }
}

class Response<T> implements IResponse<T> {
    code: string;
    description: string;
    result: T | string;
    constructor(data: any) {
        if(typeof data == 'string' && ERRORS[data as keyof typeof ERRORS]){
            this.code = data as string;
            this.description = ERRORS[data as keyof typeof ERRORS].description;
            this.result = ERRORS[data as keyof typeof ERRORS].result;
        }else{
            this.code = '0000';
            this.description = ERRORS['0000' as keyof typeof ERRORS].description;
            this.result = data;
        }
    }
}


class LocalStorage<T extends ICollectable> implements ILocalStorage<T> {
    private type: { new (): T };

    constructor(type: { new (): T }) {
        this.type = type;
    }

    read(model:string): ICollection<T> {
        let collection: ICollection<T> = new Collection<T>();
        if(localStorage.getItem(model)){
            let array: string[] = JSON.parse(localStorage.getItem(model) || '')
            let arrayAux: T[] = []
            array.forEach(element => {
                arrayAux.push(Object.assign(new this.type(),JSON.parse(element)))
            });
            collection.copyArrayToCollection(arrayAux);
        }
        return collection;
    }

    write(model:string, collection: ICollection<T>): void {
        let arrayAux: string[] = []
        const array: T[] = collection.toArray();
        array.forEach((element: T) => {
            arrayAux.push(JSON.stringify(element))
        })
        localStorage.setItem(model,JSON.stringify(arrayAux))
    }
}

export class FilterBuilder {
    private filter: IFilter = {};

    constructor(){
    }

    setSelectedFields(fields: string[]): void {
        this.filter.selectedFields = fields;
    }

    setPageNumAndItems(pageNum: number, pageItems: number): void {
        this.filter.pageNum = pageNum;
        this.filter.pageItems = pageItems;
    }

    addField(field: string, value:any): void {
        if(!this.filter.fields) this.filter.fields = new Map<string,any>();
        this.filter.fields?.set(field, value);
    }

    addInterval(field: string, startValue: any, endValue: any): void {
        if(!this.filter.intervalFields) this.filter.intervalFields = new Map<string,any>();
        this.filter.intervalFields?.set(field, {
            start: startValue,
            end: endValue
        });
    }

    addOrder(field:string, order: string): void {
        if(!this.filter.orderBy) this.filter.orderBy = new Map<string,string>();
        this.filter.orderBy?.set(field, order);
    }

    getFilter(): IFilter {
        return this.filter;
    }

    clearAll(): void {
        this.filter = {};
    }

    clearFields(): void {
        delete this.filter.fields;
    }

    clearInterval(): void {
        delete this.filter.intervalFields;
    }

    clearSelectedFields(): void {
        delete this.filter.selectedFields;
    }

    clearPageNumAndItems(): void {
        delete this.filter.pageNum;
        delete this.filter.pageItems;
    }
}

export class Collection<T extends ICollectable> implements ICollection<T> {
    private data: Map<string,T>;

    constructor() {
        this.data = new Map<string, T>();
    }
    
    [Symbol.iterator](): Iterator<T, any, undefined> {
        return this.data.values();
    }

    size(): number {
        return this.data.size;
    }

    isEmpty(): boolean {
        return this.data.size == 0 ? true : false;
    }

    exists(key: string): boolean {
        return this.data.has(key);
    }

    get(key: string): T {
        const element = this.data.get(key);
        if(element) return element; else throw new Error("Element not found in collection");
    }

    add(element: T): void {
        this.data.set(element.Key, element);
    }

    remove(key: string): void {
        this.data.delete(key);
    }

    sort(filter: IFilter): void {
        if(this.isEmpty()) throw new Error('SORT ERROR: No hay elementos para ordenar');
        if(filter.orderBy == undefined) throw new Error('SORT ERROR: No esta definido el filtro para ordenar');
        filter.orderBy.forEach((key, value) => {
            if(this.toArray()[0].getSortableFields().get(value.toLowerCase()) == undefined) throw new Error('SORT ERROR: No campo a ordenar no existe o no se puede ordenar');
            if(key.toLowerCase() != 'asc' && key.toLowerCase() != 'desc') throw new Error('SORT ERROR: Orden invalido');
        });
        let orders = Array.from(filter.orderBy?.entries())        
        let sortFuction = (left: T, right: T, n: number): number => {
            if(left.getSortableFields().get(orders[n][0]) < right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? 1 : -1;
            if(left.getSortableFields().get(orders[n][0]) > right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? -1 : 1;
            if(orders[n+1]) return sortFuction(left, right, n+1);
            else return 0;
        }
        let array = this.toArray().sort((left, right) => {
            let n = 0;
            return sortFuction(left, right, n);
        })
        this.data.clear();
        this.copyArrayToCollection(array);
    }

    forEach(callbackfn: (value: T, index: string) => void): void {
        this.data.forEach((element, key) => {
            callbackfn(element, key);
        })
    }

    filter(filter: IFilter): ICollection<T> {
        if(this.isEmpty()) throw new Error('FILTER ERROR: No hay elementos para filtrar');
        if(filter.fields?.size == 0 && filter.intervalFields?.size == 0) throw new Error('FILTER ERROR: No esta definido el filtro para filtrar');
        let filteredArray = this.toArray();
        if(filter.fields?.size != 0)
            filter.fields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element => 
                    typeof value != 'string' ? element.getFilterableFields().get(key.toLowerCase()) == value : element.getFilterableFields().get(key.toLowerCase()).includes(value)
                )
            })
        if(filter.intervalFields?.size != 0)
            filter.intervalFields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element => 
                    value.end >= element.getFilterableFields().get(key.toLowerCase()) >= value.start
                )
            })
        let collection = new Collection<T>();
        collection.copyArrayToCollection(filteredArray);
        return collection;
    }

    slice(start: number, end: number): ICollection<T> {
        let collection = new Collection<T>()
        collection.copyArrayToCollection(this.toArray().slice(start,end));
        return collection;
    }

    paginate(pageNum: number, pageItems: number): ICollection<T> {
        let collection = new Collection<T>();
        collection.copyArrayToCollection(this.toArray().slice((pageNum - 1) * pageItems, pageNum * pageItems));
        return collection;
    }

    toArray(): T[] {
        return Array.from(this.data.values());
    }

    copyArrayToCollection(array: T[]): void {
        array.forEach((element:T) => {
            this.add(element);
        })
    }
}

/*
 * 
 * INTERFACES FOR CONCRETE CLASSES
 * 
 */

interface IFactory {
    createDocument(): IDocument;
    createFolder(): IFolder;
    createEnterprise(): IEnterprise;
    createDocumentNote(): IDocumentNote;
    createBank(): IBank;
    createTaxModel(): ITaxModel;
    createMessage(): IMessage;
    createMessageChat(): IMessageChat;
    createEmployee(): IEmployee;
}

interface ICollectable {
    Key: string;
    getKey(): string;
    getFilterableFields(): Map<string,any>;
    getSortableFields(): Map<string,any>;
}

interface IStorable<T extends ICollectable> {
    getCollection(): ICollection<T>;
    getLocalStorage(): string;
}

export interface IDocument extends ICollectable {
    File: string;
    FileName: string;
    FileSize: number;
    FileType: string;
    Path: string;
    Date: Date;
}

export interface IFolder extends ICollectable {
    Name: string;
    Path: string;
    Parent: string;
}

export interface IEnterprise extends ICollectable {
    Name: string;
    Document: string;
}

export interface IDocumentNote extends ICollectable {
    Text: string;
    Path: string;
}

export interface IBank extends ICollectable {
    Name: string;
    Total: number;
    Logo: string;
}

export interface ITaxModel extends ICollectable {
    Name: string;
    TaxType: string;
    Status: string;
    PaymentMethod: string;
    Result: string;
    Trimester: number;
    Year: number;
}

export interface IMessage extends ICollectable {
    Id: string;
    Name: string;
    Title: string;
    Description: string;
    Date: Date;
    Type: string;
    Status: string;
    EndDate: Date;
}

export interface IMessageChat extends ICollectable {
    Id: string;
    IdMessage: number;
    Name: string;
    Description: string;
    Date: Date;
    Type: string;
}

export interface IEmployee extends ICollectable {
    Name: string;
    Lastname: string;
    Document: string;
    Email: string;
    Phone: string;
    Naf: string;
    Active: boolean;
}

interface IAuth extends ICollectable{
    Email: string;
    Password: string;
}

/*
 * 
 * IMPLEMENTATION OF INTERFACES FOR CONCRETE CLASSES
 * 
 */

export class Factory implements IFactory {
    createDocument(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string): IDocument {
        return new Document(file, fileName, fileSize, fileType, date, path);
    }
    createFolder(name?: string, parent?: string): IFolder {
        return new Folder(name, parent);
    }
    createEnterprise(name?: string, document?: string): IEnterprise {
        return new Enterprise(name,document);
    }
    createDocumentNote(text?: string, path?: string): IDocumentNote {
        return new DocumentNote(text, path);
    }
    createBank(name?: string, total?: number, logo?: string): IBank {
        return new Bank(name, total, logo);
    }
    createTaxModel(name?: string, taxType?: string, status?: string, paymentMethod?: string, result?: string, trimester?: number, year?: number): ITaxModel {
        return new TaxModel(name, taxType, status, paymentMethod, result, trimester, year);
    }
    createMessage(name?: string, title?: string, description?: string, date?: Date, type?: string, status?: string, endDate?: Date): IMessage {
        return new Message(name, title, description, date, type, status, endDate);
    }
    createMessageChat(idMessage?: number, name?: string, description?: string, date?: Date, type?: string): IMessageChat {
        return new MessageChat(idMessage, name, description, date, type);
    }
    createEmployee(name?: string, lastname?: string, document?: string, email?: string, phone?: string, naf?: string, active?: boolean): IEmployee {
        return new Employee(name, lastname, document, email, phone, naf, active);
    }
}

class Document implements IDocument {
    private file: string;
    private fileName: string;
    private fileSize: number;
    private fileType: string;
    private path: string;
    private date: Date;
    private key: string;

    constructor(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string) {
        this.file = file || '';
        this.fileName = fileName || '';
        this.fileSize = fileSize || 0;
        this.fileType = fileType || '';
        this.path =  path || '';
        this.date = date || new Date();
        this.key = path && fileName ? path + '/' + fileName : '';
    }

    public get FileName(): string {
        return this.fileName;
    }

    public get FileSize(): number {
        return this.fileSize;
    }

    public get FileType(): string {
        return this.fileType;
    }

    public get Date(): Date {
        return this.date;
    }

    public get File(): string {
        return this.file;
    }

    public get Path(): string {
        return this.path;
    }

    public set File(file: string) {
        this.file = file;
    }

    public set FileName(fileName: string) {
        this.fileName = fileName;
    }

    public set FileSize(fileSize: number) {
        this.fileSize = fileSize;
    }

    public set FileType(fileType: string) {
        this.fileType = fileType;
    }

    public set Date(date: Date) {
        this.date = date;
    }

    public set Path(path: string) {
        this.path = path;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(key: string) {
        this.key = key;
    } 

    getKey(): string {
        return this.path + '/' + this.fileName;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        return map;
    }
}

class StorableDocument extends Document implements IStorable<IDocument> {
    getCollection(): ICollection<IDocument> {
        return documents;
    }
    getLocalStorage(): string {
        return 'documents';
    }
}

class Folder implements IFolder {
    private name: string;
    private path: string;
    private parent: string;
    private key: string;

    constructor(name?: string, parent?: string, path?: string) {
        this.name = name || '';
        this.path = parent?.toLowerCase().split(' ').join('_') + '/' + name?.split(' ').join('_') || '';
        this.parent = parent || '';
        this.key = this.path || '';
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Path(): string {
        return this.path;
    }

    public set Path(value: string) {
        this.path = value;
    }

    public get Parent(): string {
        return this.parent;
    }

    public set Parent(value: string) {
        this.parent = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.path;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('parent', this.Parent);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('parent', this.Parent);
        return map;
    }
}

class StorableFolder extends Folder implements IStorable<IFolder> {
    getCollection(): ICollection<IFolder> {
        return folders;
    }
    getLocalStorage(): string {
        return 'folders';
    }
}

class Enterprise implements IEnterprise {
    private name: string;
    private document: string;
    private key: string;

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Document(): string {
        return this.document;
    }

    public set Document(value: string) {
        this.document = value;
    }

    public get Key(): string {
        return this.key;
    }

    constructor(name?: string, document?: string, key?: string) {
        this.name = name || '';
        this.document = document || '';
        this.key = document || '';
    }

    getKey(): string {
        return this.document;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('document', this.Document);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('document', this.Document);
        return map;
    }
    
}

class StorableEnterprise extends Enterprise implements IStorable<IEnterprise> {
    getCollection(): ICollection<IEnterprise> {
        return enterprises;
    }
    getLocalStorage(): string {
        return 'enterprises';
    }
}

class DocumentNote implements IDocumentNote {
    private text: string;
    private path: string;
    private key: string;

    constructor(text?: string, path?: string) {
        this.text = text || '';
        this.path = path || '';
        this.key = path || '';
    }

    public get Text(): string {
        return this.text;
    }

    public set Text(value: string) {
        this.text = value;
    }

    public get Path(): string {
        return this.path;
    }

    public set Path(value: string) {
        this.path = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.path;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('text', this.Text);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('text', this.Text);
        return map;
    }
}

class StorableDocumentNote extends DocumentNote implements IStorable<IDocumentNote> {
    getCollection(): ICollection<IDocumentNote> {
        return documentNotes;
    }
    getLocalStorage(): string {
        return 'documentNotes';
    }
}

class Bank implements IBank {
    private name: string;
    private key: string;
    private total: number;
    private logo: string;

    constructor(name?: string, total?: number, logo?: string) {
        this.name = name || '';
        this.total = total || 0;
        this.logo = logo || '';
        this.key = name || '';
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Total(): number {
        return this.total;
    }

    public set Total(value: number) {
        this.total = value;
    }

    public get Logo(): string {
        return this.logo;
    }

    public set Logo(value: string) {
        this.logo = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.name;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('total', this.Total);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('total', this.Total);
        return map;
    }
}

class StorableBank extends Bank implements IStorable<IBank> {
    getCollection(): ICollection<IBank> {
        return banks;
    }
    getLocalStorage(): string {
        return 'banks';
    }
}

class TaxModel implements ITaxModel {
    private name: string;
    private taxType: string;
    private status: string;
    private paymentMethod: string;
    private result: string;
    private trimester: number;
    private year: number;
    private key: string;

    constructor(name?: string, taxType?: string, status?: string, paymentMethod?: string, result?: string, trimester?: number, year?: number) {
        this.name = name || '';
        this.taxType = taxType || '';
        this.status = status || '';
        this.paymentMethod = paymentMethod || '';
        this.result = result || '';
        this.trimester = trimester || 0;
        this.year = year || 0;
        if(name && trimester && year) this.key = name + trimester.toString() + year.toString();
        else this.key = ''
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get TaxType(): string {
        return this.taxType;
    }

    public set TaxType(value: string) {
        this.taxType = value;
    }

    public get Status(): string {
        return this.status;
    }

    public set Status(value: string) {
        this.status = value;
    }

    public get PaymentMethod(): string {
        return this.paymentMethod;
    }

    public set PaymentMethod(value: string) {
        this.paymentMethod = value;
    }

    public get Result(): string {
        return this.result;
    }

    public set Result(value: string) {
        this.result = value;
    }

    public get Trimester(): number {
        return this.trimester;
    }

    public set Trimester(value: number) {
        this.trimester = value;
    }

    public get Year(): number {
        return this.year;
    }

    public set Year(value: number) {
        this.year = value;
    }

    public get Key(){
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        if(this.name && this.trimester && this.year) return this.name + this.trimester.toString() + this.year.toString();
        else return '';
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('taxType', this.TaxType);
        map.set('status', this.Status);
        map.set('paymentMethod', this.PaymentMethod);
        map.set('result', this.Result);
        map.set('trimester', this.Trimester);
        map.set('year', this.Year);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('taxType', this.TaxType);
        map.set('status', this.Status);
        map.set('paymentMethod', this.PaymentMethod);
        map.set('result', this.Result);
        map.set('trimester', this.Trimester);
        map.set('year', this.Year);
        return map;
    }
}

class StorableTaxModel extends TaxModel implements IStorable<ITaxModel> {
    getCollection(): ICollection<ITaxModel> {
        return taxModels;
    }
    getLocalStorage(): string {
        return 'taxModels';
    }
}

class Message implements IMessage {
    private id: string;
    private name: string;
    private title: string;
    private description: string;
    private date: Date;
    private type: string;
    private status: string;
    private endDate: Date;
    private key: string;

    constructor(name?: string, title?: string, description?: string, date?: Date, type?: string, status?: string, endDate?: Date) {
        this.id = new KeyGenerator().generate(15);
        this.name = name || '';
        this.title = title || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || '';
        this.status = status || '';
        this.endDate = endDate || new Date();
        this.key = this.id || '';
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(value: string) {
        this.id = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Title(): string {
        return this.title;
    }

    public set Title(value: string) {
        this.title = value;
    }

    public get Description(): string {
        return this.description;
    }

    public set Description(value: string) {
        this.description = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    public get Status(): string {
        return this.status;
    }

    public set Status(value: string) {
        this.status = value;
    }

    public get EndDate(): Date {
        return this.endDate;
    }

    public set EndDate(value: Date) {
        this.endDate = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    getKey(): string {
        return this.id.toString();
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('mame', this.name);
        map.set('title', this.title);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('endDate', this.endDate);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('mame', this.name);
        map.set('title', this.title);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('endDate', this.endDate);
        return map;
    }
}

class StorableMessage extends Message implements IStorable<IMessage> {
    getCollection(): ICollection<IMessage> {
        return messages;
    }
    getLocalStorage(): string {
        return 'messages';
    }
}

class MessageChat implements IMessageChat {
    private id: string;
    private idMessage: number;
    private name: string;
    private description: string;
    private date: Date;
    private type: string;
    private key: string;

    constructor(idMessage?: number, name?: string, description?: string, date?: Date, type?: string) {
        this.id = new KeyGenerator().generate(15);
        this.idMessage = idMessage || 0;
        this.name = name || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || '';
        this.key = this.id || '';
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(value: string) {
        this.id = value;
    }

    public get IdMessage(): number {
        return this.idMessage;
    }

    public set IdMessage(value: number) {
        this.idMessage = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Description(): string {
        return this.description;
    }

    public set Description(value: string) {
        this.description = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    getKey(): string {
        return this.id.toString();
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        return map;
    }
}

class StorableMessageChat extends MessageChat implements IStorable<IMessageChat> {
    getCollection(): ICollection<IMessageChat> {
        return messageChats;
    }
    getLocalStorage(): string {
        return 'messageChats';
    }
}

class Employee implements IEmployee {
    private name: string;
    private lastname: string;
    private document: string;
    private email: string;
    private phone: string;
    private naf: string;
    private active: boolean;
    private key: string;

    constructor(name?: string, lastname?: string, document?: string, email?: string, phone?: string, naf?: string, active?: boolean) {
        this.key = document || '';
        this.name = name || '';
        this.lastname = lastname || '';
        this.document = document || '';
        this.email = email || '';
        this.phone = phone || '';
        this.naf = naf || '';
        this.active = active || false;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Lastname(): string {
        return this.lastname;
    }

    public set Lastname(value: string) {
        this.lastname = value;
    }

    public get Document(): string {
        return this.document;
    }

    public set Document(value: string) {
        this.document = value;
    }

    public get Email(): string {
        return this.email;
    }

    public set Email(value: string) {
        this.email = value;
    }

    public get Phone(): string {
        return this.phone;
    }

    public set Phone(value: string) {
        this.phone = value;
    }

    public get Naf(): string {
        return this.naf;
    }

    public set Naf(value: string) {
        this.naf = value;
    }

    public get Active(): boolean {
        return this.active;
    }

    public set Active(value: boolean) {
        this.active = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastname', this.lastname);
        map.set('document', this.document);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('naf', this.naf);
        map.set('active', this.active);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastname', this.lastname);
        map.set('document', this.document);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('naf', this.naf);
        map.set('active', this.active);
        return map;
    }
}

class StorableEmployee extends Employee implements IStorable<IEmployee> {
    getCollection(): ICollection<IEmployee> {
        return employees;
    }
    getLocalStorage(): string {
        return 'employees';
    }
}

class Auth implements IAuth {
    private email: string;
    private password: string;
    private key: string;

    constructor(email?: string, password?: string) {
        this.email = email || '';
        this.password = password || '';
        this.key = email || '';
    }

    public get Email(): string {
        return this.email;
    }

    public set Email(value: string) {
        this.email = value;
    }

    public get Password(): string {
        return this.password;
    }

    public set Password(value: string) {
        this.password = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    getKey(): string {
        return this.email;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('email', this.email);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('email', this.email);
        return map;
    }
}

class StorableAuth extends Auth implements IStorable<IAuth> {
    getCollection(): ICollection<IAuth> {
        return auths;
    }
    getLocalStorage(): string {
        return 'auths';
    }
}
    

/**
 * 
 * READ FROM LOCAL STORAGE ON PROYECT START FOR DEVELOPMENT
 * 
 */

let documents: ICollection<IDocument> = new Collection<IDocument>();
let storableDocuments = new StorableDocument();
let localDocuments = new LocalStorage<IDocument>(Document);
documents = localDocuments.read(storableDocuments.getLocalStorage())
// documents.add(new Document('', 'file1', 1, 'pdf', new Date(), 'path1'));
// documents.add(new Document('', 'file2', 2, 'pdf', new Date(), 'path2'));
// documents.add(new Document('', 'file3', 3, 'jpg', new Date(), 'path3'));
// documents.add(new Document('', 'file4', 4, 'pdf', new Date(), 'path4'));
// documents.add(new Document('', 'file5', 5, 'pdf', new Date(), 'path5'));
// documents.add(new Document('', 'file6', 6, 'jpg', new Date(), 'path6'));

let folders: ICollection<IFolder> = new Collection<IFolder>();
let storableFolders = new StorableFolder();
let localFolders = new LocalStorage<IFolder>(Folder);
folders = localFolders.read(storableFolders.getLocalStorage())
// folders.add(new Folder('A contabilizar', '', '/a_contabilizar',));
// folders.add(new Folder('Contabilizado', '', '/contabilizado',));
// folders.add(new Folder('Papelera', '', '/papelera',));
// folders.add(new Folder('Fiscal', '', '/fiscal',));
// folders.add(new Folder('Laboral', '', '/laboral',));
// folders.add(new Folder('Maria Rico Gómez', '/laboral', '/laboral/48150243L',));
// folders.add(new Folder('Juan Carlos Aragón Pérez', '/laboral', '/laboral/11556837G',));
// folders.add(new Folder('Maria Rico Álvarez', '/laboral', '/laboral/86638678R',));

let enterprises: ICollection<IEnterprise> = new Collection<IEnterprise>();
let storableEnterprises = new StorableEnterprise();
let localEnterprises = new LocalStorage<IEnterprise>(Enterprise);
enterprises = localEnterprises.read(storableEnterprises.getLocalStorage())
enterprises.add(new Enterprise('Pet Estudio', 'B16880148'));

let documentNotes: ICollection<IDocumentNote> = new Collection<IDocumentNote>();
let storableDocumentNotes = new StorableDocumentNote();
let localDocumentNotes = new LocalStorage<IDocumentNote>(DocumentNote);
documentNotes = localDocumentNotes.read(storableDocumentNotes.getLocalStorage())

let banks: ICollection<IBank> = new Collection<IBank>();
let storableBanks = new StorableBank();
let localBanks = new LocalStorage<IBank>(Bank);
banks = localBanks.read(storableBanks.getLocalStorage())

let taxModels: ICollection<ITaxModel> = new Collection<ITaxModel>();
let storableTaxModels = new StorableTaxModel();
let localTaxModels = new LocalStorage<ITaxModel>(TaxModel);
taxModels = localTaxModels.read(storableTaxModels.getLocalStorage())

let messages: ICollection<IMessage> = new Collection<IMessage>();
let storableMessages = new StorableMessage();
let localMessages = new LocalStorage<IMessage>(Message);
messages = localMessages.read(storableMessages.getLocalStorage())

let messageChats: ICollection<IMessageChat> = new Collection<IMessageChat>();
let storableMessageChats = new StorableMessageChat();
let localMessageChats = new LocalStorage<IMessageChat>(MessageChat);
messageChats = localMessageChats.read(storableMessageChats.getLocalStorage())

let employees: ICollection<IEmployee> = new Collection<IEmployee>();
let storableEmployees = new StorableEmployee();
let localEmployees = new LocalStorage<IEmployee>(Employee);
employees = localEmployees.read(storableEmployees.getLocalStorage())

let auths: ICollection<IAuth> = new Collection<IAuth>();
let storableAuths = new StorableAuth();
let localAuths = new LocalStorage<IAuth>(Auth);
auths = localAuths.read(storableAuths.getLocalStorage())
auths.add(new Auth('test@aonsolutions.test', 'test'));
auths.add(new Auth('admin', 'admin'));



// function getLambda() : Promise<any>{
//     return new Promise((resolve,reject) => {
//         fetch('https://tjx4cclp6g7yzeloe23lowb6ka0gphmh.lambda-url.eu-west-1.on.aws/?domain=despacho-ayudatdemo.aonsolutions.net')
//         .then((response) => {
//             resolve(response);
//         }).catch((error) => {
//             reject(error);
//         })
//     })
// }


// function proccessReadable(response:any): Promise<string>{
//     return new Promise((resolve,reject) => {
//         let result: string = '';
//         const reader = response.body?.getReader();
//         if(reader){
//             reader.read().then(function processText({ done, value }:any):any {
//                 if(done){
//                     resolve(result);                    
//                     return;
//                 }
//                 if(value)
//                     result += new TextDecoder().decode(value)
//                 return reader.read().then(processText);
//             });
//         }else{
//             reject(new Response('0101'))
//         }
//     })
// }

// if(!localStorage.getItem('dump')){
//     getLambda().then((response) => {
//         proccessReadable(response)
//         .then((result) => {
//             result = result.split('var').join('');
//             eval(result);
//             saveToLocalStorage('enterprise', enterprises);
//             saveToLocalStorage('employee', employees);
//         });
//     });
// }