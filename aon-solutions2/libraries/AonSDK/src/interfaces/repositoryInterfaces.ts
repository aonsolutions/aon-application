import { ICollectable, IMessage, IMark, IProductStatus, IProductCode, IProductType, IProductClass, IProductCategory, ITax, IIRPF, IInvoiceCategory, IInvoiceSerie, IInvoiceTransactionType, IEnterprise, IRegistryEnterprise, IUser, IDocument, IBank, ITaxModel } from "./modelsInterfaces";
import { IFilter, ICollection } from "./utilitiesInterfaces";

export interface IRepositorySingleObjectReader<T> {
    /**
     * Get a single object by its key
     * @param key The key
     */
    get(key: string): Promise<T>;
}

export interface IRepositorySingleObjectCreator<T> {
    /**
     * Create a single object
     * @param element The element
     */
    create(element: T): Promise<T>;
}

export interface IRepositorySingleObjectUpdater<T> {
    /**
     * Update a single object
     * @param element The element
     */
    update(element: T): Promise<T>;
}

export interface IRepositorySingleObjectRemover<T> {
    /**
     * Delete a single object
     * @param key The key
     */
    delete(key: string): Promise<void>;
}

export interface IRepositoryMultipleObjectReader<T extends ICollectable> {
    /**
     * Get a collection
     * @param filter The filter
     * @returns The collection
     */
    get(filter?: IFilter): Promise<ICollection<T>>;
}

export interface IRepositoryMultipleObjectCreator<T extends ICollectable> {
    /**
     * Create all objects of given collection
     * @param collection The collection to create
     * @returns The collection of created objects
     */
    create(collection: ICollection<T>): Promise<ICollection<T>>;
}

export interface IRepositoryMultipleObjectUpdater<T extends ICollectable> {
    /**
     * Update all objects of given collection
     * @param collection The collection to update
     * @returns The collection of updated objects
     */
    update(collection: ICollection<T>): Promise<ICollection<T>>;
}

export interface IRepositoryMultipleObjectRemover<T extends ICollectable> {
    /**
     * Delete all objects of given array of Keys
     * @param keys The keys
     */
    delete(keys: string []): Promise<void>;
}

export interface ISingleObjectCrudRepository<T extends ICollectable> extends IRepositorySingleObjectReader<T>, IRepositorySingleObjectCreator<T>, IRepositorySingleObjectUpdater<T>, IRepositorySingleObjectRemover<T> {}

export interface IMultipleObjectCrudRepository<T extends ICollectable> extends IRepositoryMultipleObjectReader<T>, IRepositoryMultipleObjectCreator<T>, IRepositoryMultipleObjectUpdater<T>, IRepositoryMultipleObjectRemover<T> {}

export interface IAuthenticationRepository {
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
    /**
     * Set login on localStorage
     */
    userInfo(): Promise<void>;
    /**
     * Set the registry on localStorage of the user
     */
    setRegistry(): Promise<void>;
}

export interface IReportingRepository {
    cobrosPagos(): Promise<any>;
    ventasGastos(): Promise<any>;
}

export interface IMessageSpecificMethodsRepository {
    /**
     * Archive an open message
     * @param element The message
     */
    archiveMessage(element: IMessage): Promise<IMessage>;
    /**
     * Reopen an archived message
     * @param element The message
     */
    reopenMessage(element: IMessage): Promise<IMessage>;
    /**
     * Returns the number of messages
     * @param filter The filter
     */
    getMessageCount(filter?: IFilter): Promise<number>;
    /**
     * Returns true if the operation was succes
     * @param message The message of type notification to mark as read
     */
    markAsReadNotification(message: IMessage): Promise<boolean>;
}

export interface IMarkSpecificMethodsRepository {
    getMarksOfOneUser(userId: string, filter: IFilter): Promise<ICollection<IMark>>;
}

export interface IProductSpecificMethodsRepository {
    getProductStatus(filter: IFilter): Promise<ICollection<IProductStatus>>;
    getProductCodes(filter: IFilter): Promise<ICollection<IProductCode>>;
    getProductTypes(filter: IFilter): Promise<ICollection<IProductType>>;
    getProductClasses(filter: IFilter): Promise<ICollection<IProductClass>>;
    getProductCategories(filter: IFilter): Promise<ICollection<IProductCategory>>;
}

export interface IInvoiceSpecificMethodsRepository {
    getTaxList(filter?: IFilter | undefined): Promise<ICollection<ITax>>;
    getIRPFList(filter?: IFilter | undefined): Promise<ICollection<IIRPF>>;
    getInvoiceCategoryList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceCategory>>;
    getInvoiceSerieList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceSerie>>;
    getTransactionTypeList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceTransactionType>>;
    getPaymentMethodList(filter?: IFilter | undefined): Promise<ICollection<ITax>>;
    getInvoiceActivityList(filter?: IFilter | undefined): Promise<ICollection<ITax>>;
}

export interface IEnterpriseSpecificMethodsRepository {
    getCurrentEnterpriseData(): Promise<IEnterprise>;
    updateCurrentEntepriseData(enterprise: IEnterprise): Promise<IEnterprise>;
    getCurrentEnterpriseRegistryData(): Promise<IRegistryEnterprise>;
    updateCurrentEnterpriseRegistryData(registryEnterprise: IRegistryEnterprise): Promise<IRegistryEnterprise>;
}

export interface IUserSpecificMethodsRepository {
    getCurrentUserData(): Promise<IUser>;
    updateCurrentUserData(user: IUser): Promise<IUser>;
}

export interface IDocumentSpecificMethodsRepository {
    uploadDocument(document: IDocument, file: File): Promise<boolean>;
    getRawFile(document: IDocument): Promise<any>;
}

export interface ITaxModelSpecificMethodsRepository {
    payTaxModelWithNRC(model: ITaxModel, nrc: string): Promise<boolean>;
    payTaxModelWithBank(model: ITaxModel, bank: IBank): Promise<boolean>;
}