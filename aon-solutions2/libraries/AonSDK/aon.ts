import { Type } from '@angular/core';
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
    /**
     * Create a class for available single object crud methods, that will be used to create, update, delete or get a single object.
     */
    createSingleObjectCrud(): ISingleObjectCreator<T> | ISingleObjectUpdater<T> | ISingleObjectRemover<T> | ISingleObjectReader<T>;
}

interface IMultipleObjectCrudFactory<T extends ICollectable> {
    /**
     * Create a class for available multiple object crud methods, that will be used to create, update, delete or get a collection of objects.
     */
    createMultipleObjectCrud(): IMultipleObjectCreator<T> | IMultipleObjectUpdater<T> | IMultipleObjectRemover<T> | IMultipleObjectReader<T>;
}

interface IAuthenticationManagerFactory {
    /**
     * Create the authentication manager instance that contains the methods to manage authentication
     */
    createAuthenticationManager(): IAuthenticationManager;
}

interface IReportingDataAccessFactory {
    /**
     * Create the reporting data access instance that contains the methods to manage reporting
     */
    createReportingDataAccess(): IReportingDataAccess;
}

/*
 *
 * CONCRETE FACTORYS IMPLEMENTATIONS
 *
 */

export class DocumentFactory implements ISingleObjectCrudFactory<IDocument>, IMultipleObjectCrudFactory<IDocument> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocument> {
        return new GenericSingleObjectCrud<Document>(new GenericSingleObjectCrudRepository<Document>(new StorableDocument(), Document), Document);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocument> {
        return new GenericMultipleObjectCrud<Document>(new GenericMultipleObjectCrudRepository<Document>(new StorableDocument(), Document), Document);
    }
}

export class FolderFactory implements ISingleObjectCrudFactory<IFolder>, IMultipleObjectCrudFactory<IFolder> {
    createSingleObjectCrud(): ISingleObjectCrud<IFolder> {
        return new GenericSingleObjectCrud<Folder>(new GenericSingleObjectCrudRepository<Folder>(new StorableFolder(), Folder), Folder);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IFolder> {
        return new GenericMultipleObjectCrud<Folder>(new GenericMultipleObjectCrudRepository<Folder>(new StorableFolder(), Folder), Folder);
    }
}

export class DocumenNoteFactory implements ISingleObjectCrudFactory<IDocumentNote>, IMultipleObjectCrudFactory<IDocumentNote> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocumentNote> {
        return new GenericSingleObjectCrud<DocumentNote>(new GenericSingleObjectCrudRepository<DocumentNote>(new StorableDocumentNote(), DocumentNote), DocumentNote);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocumentNote> {
        return new GenericMultipleObjectCrud<DocumentNote>(new GenericMultipleObjectCrudRepository<DocumentNote>(new StorableDocumentNote(), DocumentNote), DocumentNote);
    }
}

export class MessageFactory implements ISingleObjectCrudFactory<IMessage>, IMultipleObjectCrudFactory<IMessage> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessage> {
        return new GenericSingleObjectCrud<Message>(new GenericSingleObjectCrudRepository<Message>(new StorableMessage(), Message), Message);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessage> {
        return new GenericMultipleObjectCrud<Message>(new GenericMultipleObjectCrudRepository<Message>(new StorableMessage(), Message), Message);
    }
}

export class MessageChatFactory implements ISingleObjectCrudFactory<IMessageChat>, IMultipleObjectCrudFactory<IMessageChat> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessageChat> {
        return new GenericSingleObjectCrud<MessageChat>(new GenericSingleObjectCrudRepository<MessageChat>(new StorableMessageChat(), MessageChat), MessageChat);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessageChat> {
        return new GenericMultipleObjectCrud<MessageChat>(new GenericMultipleObjectCrudRepository<MessageChat>(new StorableMessageChat(), MessageChat), MessageChat);
    }
}

export class EnterpriseFactory implements ISingleObjectCrudFactory<IEnterprise>, IMultipleObjectCrudFactory<IEnterprise> {
    createSingleObjectCrud(): ISingleObjectReader<IEnterprise> {
        return new GenericSingleObjectCrud<Enterprise>(new GenericSingleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise), Enterprise);
    }
    createMultipleObjectCrud(): IMultipleObjectReader<IEnterprise> {
        return new GenericMultipleObjectCrud<Enterprise>(new GenericMultipleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise), Enterprise);
    }
}

export class BankFactory implements ISingleObjectCrudFactory<IBank>, IMultipleObjectCrudFactory<IBank> {
    createSingleObjectCrud(): ISingleObjectCrud<IBank> {
        return new GenericSingleObjectCrud<Bank>(new GenericSingleObjectCrudRepository<Bank>(new StorableBank(), Bank), Bank);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IBank> {
        return new GenericMultipleObjectCrud<Bank>(new GenericMultipleObjectCrudRepository<Bank>(new StorableBank(), Bank), Bank);
    }
}

export class TaxModelFactory implements ISingleObjectCrudFactory<ITaxModel>, IMultipleObjectCrudFactory<ITaxModel> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaxModel> {
        return new GenericSingleObjectCrud<TaxModel>(new GenericSingleObjectCrudRepository<TaxModel>(new StorableTaxModel(), TaxModel), TaxModel);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ITaxModel> {
        return new GenericMultipleObjectCrud<TaxModel>(new GenericMultipleObjectCrudRepository<TaxModel>(new StorableTaxModel(), TaxModel), TaxModel);
    }
}

export class EmployeeFactory implements ISingleObjectCrudFactory<IEmployee>, IMultipleObjectCrudFactory<IEmployee> {
    createSingleObjectCrud(): ISingleObjectCrud<IEmployee> {
        return new GenericSingleObjectCrud<Employee>(new GenericSingleObjectCrudRepository<Employee>(new StorableEmployee(), Employee), Employee);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IEmployee> {
        return new GenericMultipleObjectCrud<Employee>(new GenericMultipleObjectCrudRepository<Employee>(new StorableEmployee(), Employee), Employee);
    }
}

export class AuthenticationFactory implements IAuthenticationManagerFactory {
    createAuthenticationManager(): IAuthenticationManager {
        return new AuthenticationManager(new AuthenticationRepository(new StorableAuth()));
    }
}

export class ReportingFactory implements IReportingDataAccessFactory {
    createReportingDataAccess(): IReportingDataAccess {
        return new ReportingDataAccess(new ReportingRepository());
    }
}

export class UserFactory implements ISingleObjectCrudFactory<IUser>, IMultipleObjectCrudFactory<IUser> {
    createSingleObjectCrud(): ISingleObjectCrud<IUser> {
        return new GenericSingleObjectCrud<User>(new GenericSingleObjectCrudRepository<User>(new StorableUser(), User), User);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IUser> {
        return new GenericMultipleObjectCrud<User>(new GenericMultipleObjectCrudRepository<User>(new StorableUser(), User), User);
    }
}

/*
 *
 * INTERFACES TO DEFINE METHODS FOR CLIENT
 *
 */

interface ISingleObjectReader<T extends ICollectable> {
    /**
     * Get a single object by its key
     * @param key The key of the object
     * @returns Return a single object
     */
    getElement(key: string): Promise<IResponse<T>>;
}

interface ISingleObjectCreator<T extends ICollectable> {
    /**
     * Create a single object
     * @param element The element to create
     * @returns Return the created object
     */
    createElement(element: T): Promise<IResponse<T>>;
}

interface ISingleObjectUpdater<T extends ICollectable> {
    /**
     * Updated a single object
     * @param element The element to update
     * @returns Return the updated object
     */
    updateElement(element: T): Promise<IResponse<T>>;
}

interface ISingleObjectRemover<T extends ICollectable> {
    /**
     * Delete a single object
     * @param key The key of the object
     * @returns Return true if the object was removed or false otherwise
     */
    deleteElement(key: string): Promise<IResponse<boolean>>;
}

interface ISingleObjectCrud<T extends ICollectable> extends ISingleObjectReader<T>, ISingleObjectCreator<T>, ISingleObjectUpdater<T>, ISingleObjectRemover<T> {}

interface IMultipleObjectReader<T extends ICollectable> {
    /**
     * Get a collection of objects
     * @param filter The filter to apply
     * @returns Return the collection of objects
     */
    getCollection(filter?: IFilter): Promise<IResponse<ICollection<T>>>;
}

interface IMultipleObjectCreator<T extends ICollectable> {
    /**
     * Create a collection of objects
     * @param collection The collection to create
     * @returns Return a collection with created objects
     */
    createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>>;
}

interface IMultipleObjectUpdater<T extends ICollectable> {
    /**
     * Update a collection of objects
     * @param collection The collection to update
     * @returns Return a collection with updated objects
     */
    updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>>;
}

interface IMultipleObjectRemover<T extends ICollectable> {
    /**
     * Delete a collection of objects
     * @param keys The keys of the collection
     * @returns Return true if the collection was removed or false otherwise
     */
    deleteCollection(keys: string []): Promise<IResponse<boolean>>;
}

interface IMultipleObjectCrud<T extends ICollectable> extends IMultipleObjectReader<T>, IMultipleObjectCreator<T>, IMultipleObjectUpdater<T>, IMultipleObjectRemover<T> {}

interface IAuthenticationManager {
    /**
     * Method to login the aplication, set the token on localstorage if the authentication was successful
     * @param email The email of the user
     * @param password The password of the user
     * @returns Returns true if the login was successful or false otherwise
     */
    login(email: string, password: string): Promise<IResponse<boolean>>;
    /**
     * Method to logout the aplication, remove the token and the enterprise from localstorage
     * @returns Returns true if the logout was successful or false otherwise
     */
    logout(): Promise<IResponse<boolean>>;
    /**
     * Method to login the aplication, set the token on localstorage if the authentication was successful
     * @param token The token of the user
     * @returns Returns true if the login was successful or false otherwise
     */
    tokenLogin(token: string): Promise<IResponse<boolean>>;
    /**
     * Checks if user is authenticated
     * @returns Returns true if the user is authenticated or false otherwise
     */
    isAuthenticated(): IResponse<boolean>;
    /**
     * Checks if enterprise is selected
     * @returns Returns true if the enterprise is selected or false otherwise
     */
    isEnterpriseSelected(): IResponse<boolean>;
    /**
     * Get the current enterprise selected by the user
     * @returns Returns the enterprise
     */
    getEnterpriseSelected(): IResponse<string>;
    /**
     * Set the enterprise selected by the user
     * @param enterprise The enterprise selected
     * @returns Returns true if the enterprise was set or false otherwise
     */
    setEnterprise(enterprise: string): IResponse<boolean>;
}

/**
 * Interface for methods of reporting data
 */
interface IReportingDataAccess {
    cobrosPagos(): Promise<IResponse<Object>>;
    ventasGastos(): Promise<IResponse<Object>>;
}

/*
 *
 * IMPLEMENTATION OF INTERFACES FOR THE CLIENTS -
 *
 */


class GenericSingleObjectCrud<T extends IModel> implements ISingleObjectCrud<T> {
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
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0201');
        }
    }

    async updateElement(element: T): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.update(element));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202');
        }
    }

    async deleteElement(key: string): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(key);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0203');
        }
    }

    async getElement(key: string): Promise<IResponse<T>> {
        try {
            return new Response<T>(await this.repository.get(key));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0206');
        }
    }
}

class GenericMultipleObjectCrud<T extends IModel> implements IMultipleObjectCrud<T> {
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
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0206');
        }
    }

    async createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.create(collection));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0201');
        }
    }

    async updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>> {
        try {
            return new Response<ICollection<T>>(await this.repository.update(collection));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0202');
        }
    }

    async deleteCollection(keys: string[]): Promise<IResponse<boolean>> {
        try {
            this.repository.delete(keys);
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0203');
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
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0101');
        }
    }

    async logout(): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.logout()
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0111');
        }
    }

    async tokenLogin(token: string): Promise<IResponse<boolean>> {
        try {
            await this.authenticationRepository.tokenLogin(token)
            return new Response<boolean>(true);
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0101');
        }
    }

    isAuthenticated(): IResponse<boolean> {
        return new Response<boolean>(localStorage.getItem('token') ? true : false);
    }

    isEnterpriseSelected(): IResponse<boolean> {
        return new Response<boolean>(localStorage.getItem('enterprise') ? true : false);
    }

    setEnterprise(enterprise: string): IResponse<boolean> {
        if(enterprise) localStorage.setItem('enterprise', enterprise);
        else throw new ErrorResponse('0113');
        return new Response<boolean>(true);
    }

    getEnterpriseSelected(): IResponse<string> {
        return new Response<string>(localStorage.getItem('enterprise'));
    }
}

class ReportingDataAccess implements IReportingDataAccess {

    private reportingRepository;

    constructor(reportingRepository: IReportingRepository) {
        this.reportingRepository = reportingRepository;
    }

    async cobrosPagos(): Promise<IResponse<Object>> {
        return new Response<Object>(this.reportingRepository.cobrosPagos());
    }

    async ventasGastos(): Promise<IResponse<Object>> {
        return new Response<Object>(this.reportingRepository.ventasGastos());
    }

}

/*
 *
 * REPOSITORY INTERFACES
 *
 */

interface IRepositorySingleObjectReader<T> {
    /**
     * Get a single object by its key
     * @param key The key
     */
    get(key: string): Promise<T>;
}

interface IRepositorySingleObjectCreator<T> {
    /**
     * Create a single object
     * @param element The element
     */
    create(element: T): Promise<T>;
}

interface IRepositorySingleObjectUpdater<T> {
    /**
     * Update a single object
     * @param element The element
     */
    update(element: T): Promise<T>;
}

interface IRepositorySingleObjectRemover<T> {
    /**
     * Delete a single object
     * @param key The key
     */
    delete(key: string): Promise<void>;
}

interface IRepositoryMultipleObjectReader<T extends ICollectable> {
    /**
     * Get a collection
     * @param filter The filter
     * @returns The collection
     */
    get(filter?: IFilter): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectCreator<T extends ICollectable> {
    /**
     * Create all objects of given collection
     * @param collection The collection to create
     * @returns The collection of created objects
     */
    create(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectUpdater<T extends ICollectable> {
    /**
     * Update all objects of given collection
     * @param collection The collection to update
     * @returns The collection of updated objects
     */
    update(collection: ICollection<T>): Promise<ICollection<T>>;
}

interface IRepositoryMultipleObjectRemover<T extends ICollectable> {
    /**
     * Delete all objects of given array of Keys
     * @param keys The keys
     */
    delete(keys: string []): Promise<void>;
}

interface ISingleObjectCrudRepository<T extends ICollectable> extends IRepositorySingleObjectReader<T>, IRepositorySingleObjectCreator<T>, IRepositorySingleObjectUpdater<T>, IRepositorySingleObjectRemover<T> {}

interface IMultipleObjectCrudRepository<T extends ICollectable> extends IRepositoryMultipleObjectReader<T>, IRepositoryMultipleObjectCreator<T>, IRepositoryMultipleObjectUpdater<T>, IRepositoryMultipleObjectRemover<T> {}

interface IAuthenticationRepository {
    /**
     * If email and password are succesful, write a new token on localstorage to authenticate the user, otherwise throw an error
     * @param email The email of the user
     * @param password The password of the user
     */
    login(email: string, password: string): Promise<void>;
    /**
     * Remove the token from localstorage
     */
    logout(): Promise<void>;
    /**
     * If token is succesful, write a new token on localstorage to authenticate the user, otherwise throw an error
     * @param token The token
     */
    tokenLogin(token: string): Promise<void>;
}

interface IReportingRepository {
    cobrosPagos(): Promise<any>;
    ventasGastos(): Promise<any>;
}

/*
 *
 * CONCRETE REPOSITORY IMPLEMENTATION
 *
 */

class GenericMultipleObjectCrudRepository<T extends IModel> implements IMultipleObjectCrudRepository<T> {
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

class GenericSingleObjectCrudRepository<T extends IModel> implements ISingleObjectCrudRepository<T> {
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

class AuthenticationRepository implements IAuthenticationRepository {

    private storableAuth: IStorable<IAuth>;

    constructor(storableAuth: IStorable<IAuth>) {
        this.storableAuth = storableAuth;
    }

    async login(email: string, password: string): Promise<void> {
        let collection = this.storableAuth.getCollection()
        if(collection.exists(email) && collection.get(email).Password == password) localStorage.setItem('token', 'testToken')
        else throw new ErrorResponse('0101');
    }

    async logout(): Promise<void> {
        if(localStorage.getItem('token')) { localStorage.removeItem('token'); localStorage.removeItem('enterprise'); }
        else throw new ErrorResponse('0111');
    }

    async tokenLogin(token: string): Promise<void> {
        if(!localStorage.getItem('token')) localStorage.setItem('token', 'testToken');
        else throw new ErrorResponse('0101');
    }
}

class ReportingRepository implements IReportingRepository {

    async cobrosPagos(): Promise<any> {
        let datasets: any[] = [], cobros: any[] = [], pagos: any[] = [], label: any[] = [];
        for(let i = 0; i < 12; i++){
            cobros.push(Math.floor(Math.random()*2000))
            pagos.push(Math.floor(Math.random()*2000))
        }
        datasets.push({data:cobros, label:'cobros'})
        datasets.push({data:pagos, label:'pagos'})
        return { datasets: datasets, label: [1,2,3,4,5,6,7,8,9,10,11,12] };
    }

    async ventasGastos(): Promise<any> {
        let datasets: any[] = [], ventas: any[] = [], gastos: any[] = [], label: any[] = [];
        for(let i = 0; i < 12; i++){
            ventas.push(Math.floor(Math.random()*2000))
            gastos.push(Math.floor(Math.random()*2000))
        }
        datasets.push({data:ventas, label:'ventas'})
        datasets.push({data:gastos, label:'gastos'})
        return { datasets: datasets, label: [1,2,3,4,5,6,7,8,9,10,11,12] };
    }

}

/*
 *
 * UTILITIES INTERFACE, FOR EXAMPLE COLLECTION TO MANAGE A LIST OF OBJECTS
 *
 */

export interface IResponse<T> {
    code: string;
    description: string;
    result: T;
}

interface ILocalStorage<T extends ICollectable> {
    /**
     * Read a collection from localstorage
     * @param model The name of the model in local storage
     */
    read(model:string): ICollection<T>;
    /**
     * Overwrite a collection in localstorage
     * @param model The name of the model in local storage
     * @param collection The collection to save on localstorage
     */
    write(model:string, collection: ICollection<T>): void;
}

export interface ICollection<T extends ICollectable> extends Iterable<T> {
    /**
     * Gets the size of the collection
     * @returns The size of the collection
     */
    size(): number;
    /**
     * Checks if the collection is empty
     * @returns True if the collection is empty, false otherwise
     */
    isEmpty(): boolean;
    /**
     * Check if element exists in collection
     * @param key The key of the element. You can use .getKey() to get it
     * @returns True if the element exists, false otherwise
     */
    exists(key: string): boolean;
    /**
     * Get element by key
     * @param key The key of the element. You can use .getKey() to get it
     * @return The element
     */
    get(key: string): T;
    /**
     * Add new element to collection
     * @param element Element to add
     */
    add(element: T): void;
    /**
     * Remove the element from collection
     * @param key The key of the element. You can use .getKey() to get it
     */
    remove(key: string): void;
    /**
     * Sort the current collection. You need to add order to IFilter
     * @param filter The filter to apply
     */
    sort(filter: IFilter): void;
    /**
     * forEach method to loop over the collection
     *
     * @Example collection.foreach((element,index) => {
     *    console.log(element, index);
     * })
     */
    forEach(callbackfn: (value: T, index: string) => void): void;
    /**
     * Filter the current collection. You need to add field or interval fields to IFilter
     * @param filter The filter to apply
     */
    filter(filter: IFilter): ICollection<T>;
    /**
     * Get a section of the array
     * @param start The beginning index of the specified portion of the array.
     * @param end The end index of the specified portion of the array.
     * @returns Copy of a section of the array
     */
    slice(start: number, end: number): ICollection<T>;
    /**
     * Paginate the collection
     * @param pageNum The page number
     * @param totalPage The number of elements per page
     * @returns A copy of the collection paginated
     */
    paginate(pageNum: number, totalPage:number): ICollection<T>;
    /**
     * Convert the collection to an array
     * @returns The collection as an array
     */
    toArray(): T[];
    /**
     * Copy the array content on the collection
     * @param Array Array of objects to copy to the collection
     */
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

export class Response<T> implements IResponse<T> {
    code: string;
    description: string;
    result: T;
    constructor(data: any) {
            this.code = '0000';
            this.description = ERRORS['0000' as keyof typeof ERRORS].description;
            this.result = data;
    }
}

class ErrorResponse implements IResponse<string> {
    code: string;
    description: string;
    result: string;
    constructor(data: any) {
        this.code = data as string;
        this.description = ERRORS[data as keyof typeof ERRORS].description;
        this.result = ERRORS[data as keyof typeof ERRORS].result;
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

    private setSelectedFields(fields: string[]): void {
        this.filter.selectedFields = fields;
    }
    /**
     * Set the page number and page items to paginate
     * @param pageNum The page number
     * @param pageItems The number of elements for page
     */
    setPageNumAndItems(pageNum: number, pageItems: number): void {
        this.filter.pageNum = pageNum;
        this.filter.pageItems = pageItems;
    }
    /**
     * Add new field to filter
     * @param field The field to filter as string
     * @param value The value of the field
     */
    addField(field: string, value:any): void {
        if(!this.filter.fields) this.filter.fields = new Map<string,any>();
        this.filter.fields?.set(field, value);
    }
    /**
     * Add new interval field to filter
     * @param field The field to filter as string
     * @param startValue Initial value of interval
     * @param endValue Last value of interval
     */
    addInterval(field: string, startValue: any, endValue: any): void {
        if(!this.filter.intervalFields) this.filter.intervalFields = new Map<string,any>();
        this.filter.intervalFields?.set(field, {
            start: startValue,
            end: endValue
        });
    }
    /**
     * Set the order to sort
     * @param field The field to filter as string
     * @param order The order to sort. Can be 'asc' or 'desc'
     */
    addOrder(field:string, order: string): void {
        if(!this.filter.orderBy) this.filter.orderBy = new Map<string,string>();
        this.filter.orderBy?.set(field, order);
    }
    /**
     * Get the IFilter object builded
     * @returns The filter as IFilter
     */
    getFilter(): IFilter {
        return this.filter;
    }
    /**
     * Clear all the filter
     */
    clearAll(): void {
        this.filter = {};
    }
    /**
     * Clear fields of filter
     */
    clearFields(): void {
        delete this.filter.fields;
    }
    /**
     * Clear interval fields of filter
     */
    clearIntervals(): void {
        delete this.filter.intervalFields;
    }

    private clearSelectedFields(): void {
        delete this.filter.selectedFields;
    }
    /**
     * Clear page number and page items
     */
    clearPageNumAndItems(): void {
        delete this.filter.pageNum;
        delete this.filter.pageItems;
    }
}

export class Collection<T extends ICollectable> implements ICollection<T> {

    // The data of the collection is saved in a Map
    private data: Map<string,T>;

    constructor() {
        this.data = new Map<string, T>();
    }

    // Iterator for collection to work on loops of type => for(const element of collection)
    [Symbol.iterator](): Iterator<T, any, undefined> {
        return this.data.values();
    }

    // Returns the size of the collection
    size(): number {
        return this.data.size;
    }

    // Check if collection is empty
    isEmpty(): boolean {
        return this.data.size == 0 ? true : false;
    }

    // Check if one element exists on the collection
    exists(key: string): boolean {
        return this.data.has(key);
    }

    // Get one element of collection by key
    get(key: string): T {
        const element = this.data.get(key);
        if(element) return element; else throw new Error("Element not found in collection");
    }

    // Add new element to collection
    add(element: T): void {
        this.data.set(element.getKey(), element);
    }

    // Remove element from collection
    remove(key: string): void {
        this.data.delete(key);
    }

    // Sort collection using IFilter interface, can be ordered by many fields
    sort(filter: IFilter): void {
        // Check if collection is empty and throw an error
        if(this.isEmpty()) throw new Error('SORT ERROR: No hay elementos para ordenar');
        // Check if filter is defined
        if(filter.orderBy == undefined) throw new Error('SORT ERROR: No esta definido el filtro para ordenar');
        // Check if filter is valid, the sort order must be asc or desc and the field must exist on getSortableFields() method of object
        filter.orderBy.forEach((key, value) => {
            if(this.toArray()[0].getSortableFields().get(value.toLowerCase()) == undefined) throw new Error('SORT ERROR: No campo a ordenar no existe o no se puede ordenar');
            if(key.toLowerCase() != 'asc' && key.toLowerCase() != 'desc') throw new Error('SORT ERROR: Orden invalido');
        });
        let orders = Array.from(filter.orderBy?.entries())
        // Recursive function to sort the collection, return 1 or -1 if the value is less or greater than the other
        // If not call sorFunction again for the next field
        let sortFuction = (left: T, right: T, n: number): number => {
            if(left.getSortableFields().get(orders[n][0]) < right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? 1 : -1;
            if(left.getSortableFields().get(orders[n][0]) > right.getSortableFields().get(orders[n][0])) return orders[n][1] == 'desc' ? -1 : 1;
            if(orders[n+1]) return sortFuction(left, right, n+1);
            else return 0;
        }
        // The call to sortFuction from inside .sort of primitive array
        let array = this.toArray().sort((left, right) => {
            let n = 0;
            return sortFuction(left, right, n);
        })
        // Remove the actual collection
        this.data.clear();
        // Copy the sorted array to the collection
        this.copyArrayToCollection(array);
    }

    // ForEach function to loop over the collection
    forEach(callbackfn: (value: T, index: string) => void): void {
        this.data.forEach((element, key) => {
            callbackfn(element, key);
        })
    }

    // Function to filter the collection, return new collection dont modify the original
    filter(filter: IFilter): ICollection<T> {
        // Check if collection is empty and throw an error
        if(this.isEmpty()) throw new Error('FILTER ERROR: No hay elementos para filtrar');
        // Check if filter is defined
        if(filter.fields?.size == 0 && filter.intervalFields?.size == 0) throw new Error('FILTER ERROR: No esta definido el filtro para filtrar');
        let filteredArray = this.toArray();
        // Apply the filter usings fields of filter and == operator
        if(filter.fields?.size != 0)
            filter.fields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element =>
                    typeof value != 'string' ? element.getFilterableFields().get(key.toLowerCase()) == value : element.getFilterableFields().get(key.toLowerCase()) == value//element.getFilterableFields().get(key.toLowerCase()).includes(value)
                )
            })
        // Apply the filter usings interval fields and >= and <= operator
        if(filter.intervalFields?.size != 0)
            filter.intervalFields?.forEach((value, key) => {
                filteredArray = filteredArray.filter(element =>
                    value.end >= element.getFilterableFields().get(key.toLowerCase()) <= value.start
                )
            })
        let collection = new Collection<T>();
        // Copy the filtered array to new collection
        collection.copyArrayToCollection(filteredArray);
        // Return the new collection
        return collection;
    }

    // Slice the collection
    slice(start: number, end: number): ICollection<T> {
        let collection = new Collection<T>()
        collection.copyArrayToCollection(this.toArray().slice(start,end));
        return collection;
    }

    // Paginate the collection
    paginate(pageNum: number, pageItems: number): ICollection<T> {
        let collection = new Collection<T>();
        collection.copyArrayToCollection(this.toArray().slice((pageNum - 1) * pageItems, pageNum * pageItems));
        return collection;
    }

    // Get the collection as an array
    toArray(): T[] {
        return Array.from(this.data.values());
    }

    // Copy an array of objects to the collection
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
    createCertificate(): ICertificate;
    createEnterprise(): IEnterprise;
    createDocumentNote(): IDocumentNote;
    createBank(): IBank;
    createTaxModel(): ITaxModel;
    createMessage(): IMessage;
    createMessageChat(): IMessageChat;
    createEmployee(): IEmployee;
    createUser(): IUser;
}

interface ICollectionFactory {
    createDocumentCollection(): ICollection<IDocument>;
    createFolderCollection(): ICollection<IFolder>;
    createEnterpriseCollection(): ICollection<IEnterprise>;
    createDocumentNoteCollection(): ICollection<IDocumentNote>;
    createBankCollection(): ICollection<IBank>;
    createTaxModelCollection(): ICollection<ITaxModel>;
    createMessageCollection(): ICollection<IMessage>;
    createMessageChatCollection(): ICollection<IMessageChat>;
    createEmployeeCollection(): ICollection<IEmployee>;
    createUserCollection(): ICollection<IUser>;
}

interface ICollectable {
    /**
     * Get the value of unique key of the object
     * @returns The unique key
     */
    getKey(): string;
    /**
     * Get the filterable fields of the object and their values
     * @returns The filterable fields as Map
     */
    getFilterableFields(): Map<string,any>;
    /**
     * Get the sortable fields of the object and their values
     * @returns The sortable fields as Map
     */
    getSortableFields(): Map<string,any>;
}

interface IModel extends ICollectable {
    /**
     * Internal key of object not visible outside SDK
     */
    Key: string;
}

interface IStorable<T extends ICollectable> {
    /**
     * Get the collection on memory of an object
     */
    getCollection(): ICollection<T>;
    /**
     * Get the value of string where the object is stored in local storage
     */
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

export interface ICertificate extends ICollectable {
  Name: string;
  RepresentationType: string;
  ExpirationDate: Date;
  Alias: string;
  Type: string;
  Tgss: boolean;
  Sepe: boolean;
  Aeat: boolean;
}

export interface IEnterprise extends ICollectable {
    Name: string;
    ProfilePhoto: string;
    Address: string;
    Country: string;
    Province: string;
    SocialReason: string;
    Email: string;
    Phone: string;
    Website: string;
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
    IdMessage: string;
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

interface IUser extends ICollectable {
  Name: string,
  Lastname: string,
  Document: string,
  Email: string,
  Password: string,
  Phone: string,
  Active: boolean
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

    createCertificate(name?: string, representationType?: string, expirationDate?: Date, alias?: string, type?: string, tgss?: boolean, sepe?: boolean, aeat?: boolean): ICertificate {
        return new Certificate(name, representationType, expirationDate, alias, type, tgss, sepe, aeat);
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

    createMessageChat(idMessage?: string, name?: string, description?: string, date?: Date, type?: string): IMessageChat {
        return new MessageChat(idMessage, name, description, date, type);
    }

    createEmployee(name?: string, lastname?: string, document?: string, email?: string, phone?: string, naf?: string, active?: boolean): IEmployee {
        return new Employee(name, lastname, document, email, phone, naf, active);
    }

    createUser(name?: string, lastname?: string, document?: string, email?: string, password?: string, phone?: string, active?: boolean): IUser {
        return new User(name, lastname, document, email, password, phone, active);
    }
}

export class CollectionFactory implements ICollectionFactory {
    createDocumentCollection(): ICollection<IDocument> {
      return new Collection<Document>();
    }

    createFolderCollection(): ICollection<IFolder> {
      return new Collection<Folder>();
    }

    createCertificateCollection(): ICollection<ICertificate> {
      return new Collection<Certificate>();
    }

    createEnterpriseCollection(): ICollection<IEnterprise> {
      return new Collection<Enterprise>();
    }

    createDocumentNoteCollection(): ICollection<IDocumentNote> {
      return new Collection<DocumentNote>();
    }

    createBankCollection(): ICollection<IBank> {
      return new Collection<Bank>();
    }

    createTaxModelCollection(): ICollection<ITaxModel> {
      return new Collection<TaxModel>();
    }

    createMessageCollection(): ICollection<IMessage> {
      return new Collection<Message>();
    }

    createMessageChatCollection(): ICollection<IMessageChat> {
      return new Collection<MessageChat>();
    }

    createEmployeeCollection(): ICollection<IEmployee> {
        return new Collection<Employee>();
    }

    createUserCollection(): ICollection<IUser> {
        return new Collection<User>();
    }
}

class Document implements IDocument, IModel {
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
        map.set('path', this.Path);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('filename', this.FileName);
        map.set('filesize', this.FileSize);
        map.set('filetype', this.FileType);
        map.set('path', this.Path);
        return map;
    }
}

class StorableDocument extends Document implements IStorable<Document> {
    getCollection(): ICollection<Document> {
        return documents;
    }
    getLocalStorage(): string {
        return 'documents';
    }
}

class Folder implements IFolder, IModel  {
    private name: string;
    private path: string;
    private parent: string;
    private key: string;

    constructor(name?: string, parent?: string) {
        this.name = name || '';
        this.path = parent?.toLocaleLowerCase().split(' ').join('_') + '/' + name?.toLocaleLowerCase().split(' ').join('_') || '';
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

class StorableFolder extends Folder implements IStorable<Folder> {
    getCollection(): ICollection<Folder> {
        return folders;
    }
    getLocalStorage(): string {
        return 'folders';
    }
}

class Certificate implements ICertificate, IModel {
  private name: string;
  private representationType: string;
  private expirationDate: Date;
  private alias: string;
  private type: string;
  private tgss: boolean;
  private sepe: boolean;
  private aeat: boolean;
  private key: string;

  constructor(name?: string, representationType?: string, expirationDate?: Date, alias?: string, type?: string, tgss?: boolean, sepe?: boolean, aeat?: boolean) {
    this.name = name || '';
    this.representationType = representationType || '';
    this.expirationDate = expirationDate || new Date();
    this.alias = alias || '';
    this.type = type || '';
    this.tgss = tgss || false;
    this.sepe = sepe || false;
    this.aeat = aeat || false;
    this.key = name || '';
  }

  public get Name(): string {
    return this.name;
  }

  public set Name(value: string) {
    this.name = value;
  }

  public get RepresentationType(): string {
    return this.representationType;
  }

  public set RepresentationType(value: string) {
    this.representationType = value;
  }

  public get ExpirationDate(): Date {
    return this.expirationDate;
  }

  public set ExpirationDate(value: Date) {
    this.expirationDate = value;
  }

  public get Alias(): string {
    return this.alias;
  }

  public set Alias(value: string) {
    this.alias = value;
  }

  public get Type(): string {
    return this.type;
  }

  public set Type(value: string) {
    this.type = value;
  }

  public get Tgss(): boolean {
    return this.tgss;
  }

  public set Tgss(value: boolean) {
    this.tgss = value;
  }

  public get Sepe(): boolean {
    return this.sepe;
  }

  public set Sepe(value: boolean) {
    this.sepe = value;
  }

  public get Aeat(): boolean {
    return this.aeat;
  }

  public set Aeat(value: boolean) {
    this.aeat = value;
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
    map.set('representationType', this.RepresentationType);
    map.set('expirationDate', this.ExpirationDate);
    map.set('alias', this.Alias);
    map.set('type', this.Type);
    map.set('tgss', this.Tgss);
    map.set('sepe', this.Sepe);
    map.set('aeat', this.Aeat);
    return map;
  }

  getSortableFields(): Map<string,any> {
    let map = new Map<string, any>();
    map.set('name', this.Name);
    map.set('representationType', this.RepresentationType);
    map.set('expirationDate', this.ExpirationDate);
    map.set('alias', this.Alias);
    map.set('type', this.Type);
    map.set('tgss', this.Tgss);
    map.set('sepe', this.Sepe);
    map.set('aeat', this.Aeat);
    return map;
  }

}

class StorableCertificate extends Certificate implements IStorable<Certificate> {
    getCollection(): ICollection<Certificate> {
        return certificates;
    }
    getLocalStorage(): string {
        return 'certificates';
    }
}

class Enterprise implements IEnterprise, IModel {
    private name: string;
    private profilePhoto: string;
    private address: string;
    private country: string;
    private province: string;
    private socialReason: string;
    private email: string;
    private phone: string;
    private website: string;
    private document: string;
    private key: string;

    public get Name(): string {
      return this.name;
    }

    public set Name(value: string) {
      this.name = value;
    }

    public get Address(): string {
      return this.address;
    }

    public set Address(value: string) {
      this.address = value;
    }

    public get Country(): string {
      return this.country;
    }

    public set Country(value: string) {
      this.country = value;
    }

    public get Province(): string {
      return this.province;
    }

    public set Province(value: string) {
      this.province = value;
    }

    public get SocialReason(): string {
      return this.socialReason;
    }

    public set SocialReason(value: string) {
      this.socialReason = value;
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

    public get Website(): string {
      return this.website;
    }

    public set Website(value: string) {
      this.website = value;
    }

    public get ProfilePhoto(): string {
        return this.profilePhoto;
    }

    public set ProfilePhoto(value: string) {
        this.profilePhoto = value;
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

    constructor(name?: string, document?: string, profilePhoto?: string, address?: string, country?: string, province?: string, socialReason?: string, email?: string, phone?: string, website?: string, key?: string) {
        this.name = name || '';
        this.profilePhoto = profilePhoto || '';
        this.address = address || '';
        this.country = country || '';
        this.province = province || '';
        this.socialReason = socialReason || '';
        this.email = email || '';
        this.phone = phone || '';
        this.website = website || '';
        this.document = document || document || '';
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

class StorableEnterprise extends Enterprise implements IStorable<Enterprise> {
    getCollection(): ICollection<Enterprise> {
        return enterprises;
    }
    getLocalStorage(): string {
        return 'enterprises';
    }
}

class DocumentNote implements IDocumentNote, IModel  {
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

class StorableDocumentNote extends DocumentNote implements IStorable<DocumentNote> {
    getCollection(): ICollection<DocumentNote> {
        return documentNotes;
    }
    getLocalStorage(): string {
        return 'documentNotes';
    }
}

class Bank implements IBank, IModel  {
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

class StorableBank extends Bank implements IStorable<Bank> {
    getCollection(): ICollection<Bank> {
        return banks;
    }
    getLocalStorage(): string {
        return 'banks';
    }
}

class TaxModel implements ITaxModel, IModel  {
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
        if(name && trimester && year) this.key = name + ';' + trimester.toString() + ';' + year.toString();
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

class StorableTaxModel extends TaxModel implements IStorable<TaxModel> {
    getCollection(): ICollection<TaxModel> {
        return taxModels;
    }
    getLocalStorage(): string {
        return 'taxModels';
    }
}

class Message implements IMessage, IModel  {
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

class StorableMessage extends Message implements IStorable<Message> {
    getCollection(): ICollection<Message> {
        return messages;
    }
    getLocalStorage(): string {
        return 'messages';
    }
}

class MessageChat implements IMessageChat, IModel  {
    private id: string;
    private idMessage: string;
    private name: string;
    private description: string;
    private date: Date;
    private type: string;
    private key: string;

    constructor(idMessage?: string, name?: string, description?: string, date?: Date, type?: string) {
        this.id = new KeyGenerator().generate(15);
        this.idMessage = idMessage || '';
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

    public get IdMessage(): string {
        return this.idMessage;
    }

    public set IdMessage(value: string) {
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

class StorableMessageChat extends MessageChat implements IStorable<MessageChat> {
    getCollection(): ICollection<MessageChat> {
        return messageChats;
    }
    getLocalStorage(): string {
        return 'messageChats';
    }
}

class Employee implements IEmployee, IModel  {
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

class StorableEmployee extends Employee implements IStorable<Employee> {
    getCollection(): ICollection<Employee> {
        return employees;
    }
    getLocalStorage(): string {
        return 'employees';
    }
}

class Auth implements IAuth, IModel  {
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

class StorableAuth extends Auth implements IStorable<Auth> {
    getCollection(): ICollection<Auth> {
        return auths;
    }
    getLocalStorage(): string {
        return 'auths';
    }
}

class User implements IUser, IModel  {
  private name: string;
  private lastname: string;
  private document: string;
  private email: string;
  private password: string;
  private phone: string;
  private active: boolean;
  private key: string;

  constructor(name?: string, lastname?: string, document?: string, email?: string, password?: string, phone?: string, active?: boolean) {
    this.name = name || '';
    this.lastname = lastname || '';
    this.document = document || '';
    this.email = email || '';
    this.password = password || '';
    this.phone = phone || '';
    this.active = active || true;
    this.key = document || '';
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

  public get Password(): string {
    return this.password;
  }

  public set Password(value: string) {
    this.password = value;
  }

  public get Phone(): string {
    return this.phone;
  }

  public set Phone(value: string) {
    this.phone = value;
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

  public getKey(): string {
    return this.key;
  }

  public getFilterableFields(): Map<string, any> {
    let map = new Map<string, any>();

    map.set('name', this.name);
    map.set('lastname', this.lastname);
    map.set('document', this.document);
    map.set('email', this.email);
    map.set('password', this.password);
    map.set('phone', this.phone);
    map.set('active', this.active);

    return map;
  }

  public getSortableFields(): Map<string, any> {
    let map = new Map<string, any>();

    map.set('name', this.name);
    map.set('lastname', this.lastname);
    map.set('document', this.document);
    map.set('email', this.email);
    map.set('password', this.password);
    map.set('phone', this.phone);
    map.set('active', this.active);

    return map;
  }
}

class StorableUser extends User implements IStorable<User> {
    getCollection(): ICollection<User> {
        return users;
    }
    getLocalStorage(): string {
        return 'users';
    }
}


/**
 *
 * READ FROM LOCAL STORAGE ON PROYECT START FOR DEVELOPMENT
 *
 */

let documents: ICollection<Document> = new Collection<Document>();
let storableDocuments = new StorableDocument();
let localDocuments = new LocalStorage<Document>(Document);
documents = localDocuments.read(storableDocuments.getLocalStorage())
if(documents.size() == 0){
    documents.add(new Document('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',
     'file1', 1, 'image/png', new Date(), '/a_contabilizar'));
    documents.add(new Document('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',
     'file2', 2, 'image/png', new Date(), '/a_contabilizar'));
    documents.add(new Document('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',
     'file3', 3, 'image/png', new Date(), '/contabilizado'));
    documents.add(new Document('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',
     'file4', 4, 'image/png', new Date(), '/contabilizado'));
    documents.add(new Document('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',
     'file5', 5, 'image/png', new Date(), '/papelera'));
    documents.add(new Document('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',
     'file6', 6, 'image/png', new Date(), '/papelera'));
    documents.add(new Document('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',
     'file7', 6, 'image/png', new Date(), '/fiscal'));
    documents.add(new Document('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',
     'file8', 6, 'image/png', new Date(), '/fiscal'));
    documents.add(new Document('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',
     'file9', 6, 'image/png', new Date(), '/laboral/maria_rico_gómez'));
    documents.add(new Document('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',
     'file10', 6, 'image/png', new Date(), '/laboral/juan_carlos_aragón_pérez'));
    documents.add(new Document('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',
     'file11', 6, 'image/png', new Date(), '/laboral/juan_carlos_aragón_pérez'));
    documents.add(new Document('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',
     'file12', 6, 'image/png', new Date(), '/laboral/11556837G'));
    documents.add(new Document('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',
     'file13', 6, 'image/png', new Date(), '/laboral/juan_carlos_aragón_pérez'));
    documents.add(new Document('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',
     'file14', 6, 'image/png', new Date(), '/laboral/86638678R'));
    localDocuments.write(storableDocuments.getLocalStorage(), documents);
}

let folders: ICollection<Folder> = new Collection<Folder>();
let storableFolders = new StorableFolder();
let localFolders = new LocalStorage<Folder>(Folder);
folders = localFolders.read(storableFolders.getLocalStorage())
if(folders.size() == 0){
    folders.add(new Folder('A contabilizar', ''));
    folders.add(new Folder('Contabilizado', ''));
    folders.add(new Folder('Papelera', ''));
    folders.add(new Folder('Fiscal', ''));
    folders.add(new Folder('Laboral', ''));
    folders.add(new Folder('Maria Rico Gómez', '/laboral'));
    folders.add(new Folder('Juan Carlos Aragón Pérez', '/laboral'));
    folders.add(new Folder('Maria Rico Álvarez', '/laboral'));
    localFolders.write(storableFolders.getLocalStorage(), folders);
}

let certificates: ICollection<Certificate> = new Collection<Certificate>();
let storableCertificates = new StorableCertificate();
let localCertificates = new LocalStorage<Certificate>(Certificate);
certificates = localCertificates.read(storableCertificates.getLocalStorage())
if(certificates.size() == 0){
  certificates.add(new Certificate('Certificado 1'));
  certificates.add(new Certificate('Certificado 2'));
  certificates.add(new Certificate('Certificado 3'));
  localCertificates.write(storableCertificates.getLocalStorage(), certificates);
}

let enterprises: ICollection<Enterprise> = new Collection<Enterprise>();
let storableEnterprises = new StorableEnterprise();
let localEnterprises = new LocalStorage<Enterprise>(Enterprise);
enterprises = localEnterprises.read(storableEnterprises.getLocalStorage())
if(enterprises.size() == 0){
    enterprises.add(new Enterprise('Pet Estudio', 'B16880148'));
    enterprises.add(new Enterprise('MENG SA', 'U14241855'));
    enterprises.add(new Enterprise('PORTABAGE SL', 'U53716270'));
    localEnterprises.write(storableEnterprises.getLocalStorage(), enterprises);
}

let documentNotes: ICollection<DocumentNote> = new Collection<DocumentNote>();
let storableDocumentNotes = new StorableDocumentNote();
let localDocumentNotes = new LocalStorage<DocumentNote>(DocumentNote);
documentNotes = localDocumentNotes.read(storableDocumentNotes.getLocalStorage())
if(documentNotes.size() == 0){
    documentNotes.add(new DocumentNote('texto de la nota', '/a_contabilizar/file1'));
    documentNotes.add(new DocumentNote('texto de la nota', '/contabilizado/file3'));
    documentNotes.add(new DocumentNote('texto de la nota', '/papelera/file5'));
    documentNotes.add(new DocumentNote('texto de la nota', '/fiscal/file7'));
    documentNotes.add(new DocumentNote('texto de la nota', '/laboral/48150243L/file9'));
    documentNotes.add(new DocumentNote('texto de la nota', '/laboral/11556837G/file11'));
    documentNotes.add(new DocumentNote('texto de la nota', '/laboral/86638678R/file13'));
    localDocumentNotes.write(storableDocumentNotes.getLocalStorage(), documentNotes);
}


let banks: ICollection<Bank> = new Collection<Bank>();
let storableBanks = new StorableBank();
let localBanks = new LocalStorage<Bank>(Bank);
banks = localBanks.read(storableBanks.getLocalStorage());
if(banks.size() == 0){
    banks.add(new Bank('Caixa Bank',1500,'whereIsMyPath?'));
    banks.add(new Bank('Banco Nación',500,'whereIsMyPath?2'));
    banks.add(new Bank('Bankinter',2500,'whereIsMyPath?3'));
    localBanks.write(storableBanks.getLocalStorage(), banks);
}

let taxModels: ICollection<TaxModel> = new Collection<TaxModel>();
let storableTaxModels = new StorableTaxModel();
let localTaxModels = new LocalStorage<TaxModel>(TaxModel);
taxModels = localTaxModels.read(storableTaxModels.getLocalStorage())
if(taxModels.size() == 0){
    taxModels.add(new TaxModel( '180','IVA','presentado','domicialición bancaria','356',1,2023));
    taxModels.add(new TaxModel( '303','IVA','presentado','tranferencia','789',1,2023));
    taxModels.add(new TaxModel( '180','IVA','presentado','domicialición bancaria','1245',2,2023));
    taxModels.add(new TaxModel( '303','IVA','presentado','tranferencia','1024',2,2023));
    taxModels.add(new TaxModel( '180','IVA','en proceso','domicialición bancaria','1538',3,2023));
    taxModels.add(new TaxModel( '303','IVA','pendiente','tranferencia','987',3,2023));
    taxModels.add(new TaxModel( '130','IVA','presentado','tranferencia','189',3,2023));
    taxModels.add(new TaxModel( '347','IVA','rectificado','tranferencia','684',3,2023));
    taxModels.add(new TaxModel( '180','IVA','en proceso','domicialición bancaria','1784',4,2023));
    taxModels.add(new TaxModel( '303','IVA','pendiente','domicialición bancaria','1345',4,2023));
    taxModels.add(new TaxModel( '130','IVA','presentado','tranferencia','541',4,2023));
    taxModels.add(new TaxModel( '347','IVA','rectificado','domicialición bancaria','1354',4,2023));
    localTaxModels.write(storableTaxModels.getLocalStorage(), taxModels);
}

let messages: ICollection<Message> = new Collection<Message>();
let storableMessages = new StorableMessage();
let localMessages = new LocalStorage<Message>(Message);
messages = localMessages.read(storableMessages.getLocalStorage())
if(messages.size() == 0){
    messages.add(new Message('Asesor1','Asunto 1','sunt in culpa qui officia deserunt',new Date("2023-06-12"),'consulta','abierta'));
    messages.add(new Message('Asesor2','Asunto 1','sunt in culpa qui officia deserunt',new Date("2023-06-26"),'consulta','abierta'));
    messages.add(new Message('Asesor3','Asunto 1','sunt in culpa qui officia deserunt',new Date("2023-07-16"),'consulta','cerrada'));
    messages.add(new Message('Asesor1','Asunto 2','sunt in culpa qui officia deserunt',new Date("2023-05-16"),'notificacion','vista'));
    messages.add(new Message('Asesor2','Asunto 2','sunt in culpa qui officia deserunt',new Date("2023-06-17"),'notificacion','nueva'));
    messages.add(new Message('Asesor3','Asunto 2','sunt in culpa qui officia deserunt',new Date("2023-07-18"),'notificacion','nueva'));
    messages.add(new Message('Asesor1','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-06-03"),'tarea','realizada',new Date("2023-08-24")));
    messages.add(new Message('Asesor2','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-06-19"),'tarea','pendiente',new Date("2023-08-28")));
    messages.add(new Message('Asesor3','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-07-22"),'tarea','pendiente',new Date("2023-09-05")));
    localMessages.write(storableMessages.getLocalStorage(), messages);
}

let messageChats: ICollection<MessageChat> = new Collection<MessageChat>();
let storableMessageChats = new StorableMessageChat();
let localMessageChats = new LocalStorage<MessageChat>(MessageChat);
messageChats = localMessageChats.read(storableMessageChats.getLocalStorage())
let filter = new FilterBuilder();
filter.addField('type','consulta');
let filteredMessages = messages.filter(filter.getFilter());
if(messageChats.size() == 0 && filteredMessages.size() != 0){
    messageChats.add(new MessageChat(filteredMessages.toArray()[0].Key,'Asesor1','sunt in culpa qui officia deserunt',new Date("2023-06-12"), 'enviado'));
    messageChats.add(new MessageChat(filteredMessages.toArray()[0].Key,'Asesor1','sunt in culpa qui officia deserunt',new Date("2023-06-12"), 'recibido'));
    messageChats.add(new MessageChat(filteredMessages.toArray()[0].Key,'Asesor1','sunt in culpa qui officia deserunt',new Date("2023-06-12"), 'enviado'));
    localMessageChats.write(storableMessageChats.getLocalStorage(), messageChats);
}

let employees: ICollection<Employee> = new Collection<Employee>();
let storableEmployees = new StorableEmployee();
let localEmployees = new LocalStorage<Employee>(Employee);
employees = localEmployees.read(storableEmployees.getLocalStorage());
if(employees.size() == 0){
    employees.add(new Employee('Maria','Rico Gómez','48150243L','mariaricogomez@gmail.test','690619302','390423363729',true));
    employees.add(new Employee('Maria','Pérez Álvarez','86638678R','mariaperezalvarez@gmail.test','656796396','650423363729',false));
    employees.add(new Employee('Juan Carlos','Aragón Pérez','11556837G','juancarlosaragonperez@gmail.test','619068048','490423363729',true));
    localEmployees.write(storableEmployees.getLocalStorage(), employees);
}

let users: ICollection<User> = new Collection<User>();
let storableUsers = new StorableUser();
let localUsers = new LocalStorage<User>(User);
users = localUsers.read(storableUsers.getLocalStorage())
if(users.size() == 0){
  users.add(new User(
    'Kathryn',
    'Ledner',
    '35532252N',
    'kathrynledner@gmail.test',
    'test',
    '690619302',
    true
    ));

  users.add(new User(
    'Eusebio',
    'González',
    '94385657M',
    'eusebiogonzalez@gmail.test',
    'test',
    '656796396',
    true
  ));

  users.add(new User(
    'Juan',
    'Macejkovic',
    '11556837G',
    'juanmacejkovic@gmail.test',
    'test',
    '619068048',
    true
  ));

  localUsers.write(storableUsers.getLocalStorage(), users);
}

let auths: ICollection<Auth> = new Collection<Auth>();
let storableAuths = new StorableAuth();
let localAuths = new LocalStorage<Auth>(Auth);
auths = localAuths.read(storableAuths.getLocalStorage())
if(auths.size() == 0){
    auths.add(new Auth('test@aonsolutions.test', 'test'));
    auths.add(new Auth('admin', 'admin'));
    localAuths.write(storableAuths.getLocalStorage(), auths);
}



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
