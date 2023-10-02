import { ICollectable, IEnterprise, IMessage, IMark, IProductStatus, IProductCode, IProductType, IProductClass, IProductCategory, ITax, IIRPF, IInvoiceCategory, IInvoiceSerie, IInvoiceTransactionType, IRegistryEnterprise, IUser, IDocument } from "./modelsInterfaces";
import { IResponse, IFilter, ICollection } from "./utilitiesInterfaces";

export interface ISingleObjectReader<T extends ICollectable> {
    /**
     * Get a single object by its key
     * @param key The key of the object
     * @returns Return a single object
     */
    getElement(key: string): Promise<IResponse<T>>;
}

export interface ISingleObjectCreator<T extends ICollectable> {
    /**
     * Create a single object
     * @param element The element to create
     * @returns Return the created object
     */
    createElement(element: T): Promise<IResponse<T>>;
}

export interface ISingleObjectUpdater<T extends ICollectable> {
    /**
     * Updated a single object
     * @param element The element to update
     * @returns Return the updated object
     */
    updateElement(element: T): Promise<IResponse<T>>;
}

export interface ISingleObjectRemover<T extends ICollectable> {
    /**
     * Delete a single object
     * @param key The key of the object
     * @returns Return true if the object was removed or false otherwise
     */
    deleteElement(key: string): Promise<IResponse<boolean>>;
}

export interface ISingleObjectCrud<T extends ICollectable> extends ISingleObjectReader<T>, ISingleObjectCreator<T>, ISingleObjectUpdater<T>, ISingleObjectRemover<T> {}

export interface IMultipleObjectReader<T extends ICollectable> {
    /**
     * Get a collection of objects
     * @param filter The filter to apply
     * @returns Return the collection of objects
     */
    getCollection(filter?: IFilter): Promise<IResponse<ICollection<T>>>;
}

export interface IMultipleObjectCreator<T extends ICollectable> {
    /**
     * Create a collection of objects
     * @param collection The collection to create
     * @returns Return a collection with created objects
     */
    createCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>>;
}

export interface IMultipleObjectUpdater<T extends ICollectable> {
    /**
     * Update a collection of objects
     * @param collection The collection to update
     * @returns Return a collection with updated objects
     */
    updateCollection(collection: ICollection<T>): Promise<IResponse<ICollection<T>>>;
}

export interface IMultipleObjectRemover<T extends ICollectable> {
    /**
     * Delete a collection of objects
     * @param keys The keys of the collection
     * @returns Return true if the collection was removed or false otherwise
     */
    deleteCollection(keys: string []): Promise<IResponse<boolean>>;
}

export interface IMultipleObjectCrud<T extends ICollectable> extends IMultipleObjectReader<T>, IMultipleObjectCreator<T>, IMultipleObjectUpdater<T>, IMultipleObjectRemover<T> {}

export interface IAuthenticationManager {
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
    setEnterprise(enterprise: IEnterprise): IResponse<boolean>;
}

/**
 * Interface for methods of reporting data
 */
export interface IReportingDataAccess {
    cobrosPagos(): Promise<IResponse<Object>>;
    ventasGastos(): Promise<IResponse<Object>>;
}

/**
 * Interface for specific methods of message
 */
export interface IMessageSpecificMethods {
    /**
     * Archive an open message
     * @param element The message
     */
    archiveMessage(element: IMessage): Promise<IResponse<IMessage>>;
    /**
     * Reopen an archived message
     * @param element The message
     */
    reopenMessage(element: IMessage): Promise<IResponse<IMessage>>;
    /**
     * Returns the number of messages
     * @param filter The filter
     */
    getMessageCount(filter?: IFilter): Promise<IResponse<number>>;
    /**
     * Returns true if the operation was succes
     * @param message The message of type notification to mark as read
     */
    markAsReadNotification(message: IMessage): Promise<IResponse<boolean>>;
}

export interface IMarkSpecificMethods {
    /**
     * Returns all the marks for one user
     */
    getMarksOfOneUser(userId: string, filter: IFilter): Promise<IResponse<ICollection<IMark>>>;
}

export interface IMarkDetailSpecificMethods {
    /**
     * Marks an entry
     */
    markEntry(): Promise<IResponse<boolean>>;
    /**
     * Marks a pause
     */
    markPause(): Promise<IResponse<boolean>>;
    /**
     * Marks an exit
     */
    markExit(): Promise<IResponse<boolean>>;
}

export interface IProductSpecificMethods {
    /**
     * Returns all the products status
     */
    getProductStatus(filter?: IFilter): Promise<IResponse<ICollection<IProductStatus>>>;
    /**
     * Returns all the product codes
     */
    getProductCodes(filter?: IFilter): Promise<IResponse<ICollection<IProductCode>>>;
    /**
     * Returns all the product types
     */
    getProductTypes(filter?: IFilter): Promise<IResponse<ICollection<IProductType>>>;
    /**
     * Returns all the product classes
     */
    getProductClasses(filter?: IFilter): Promise<IResponse<ICollection<IProductClass>>>;
    /**
     * Returns all the product categories
     */
    getProductCategories(filter?: IFilter): Promise<IResponse<ICollection<IProductCategory>>>;
}

export interface IInvoiceSpecificMethods {
    /**
     * Returns all the taxes
     */
    getTaxList(filter?: IFilter): Promise<IResponse<ICollection<ITax>>>;
    /**
     * Returns all the IRPF
     * @param filter
     */
    getIRPFList(filter?: IFilter): Promise<IResponse<ICollection<IIRPF>>>;
    /**
     * Returns all the invoice categories
     * @param filter
     */
    getInvoiceCategoryList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceCategory>>>;
    /**
     * Returns all the invoice series
     * @param filter
     */
    getInvoiceSerieList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceSerie>>>;
    /**
     * Returns all the invoice types
     * @param filter
     */
    getTransactionTypeList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceTransactionType>>>;
    /**
     * Returns all the payment methods
     * @param filter
     */
    getPaymentMethodList(filter?: IFilter): Promise<IResponse<ICollection<ITax>>>;
    /**
     * Returns all the invoice activities
     * @param filter
     */
    getInvoiceActivityList(filter?: IFilter): Promise<IResponse<ICollection<ITax>>>;
}

export interface IEnterpriseSpecificMethods {
    getCurrentEnterpriseData(): Promise<IResponse<IEnterprise>>;
    getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>>;
    updateCurrentEnterpriseData(enterprise: IEnterprise): Promise<IResponse<IEnterprise>>;
    updateCurrentEnterpriseRegistryData(registryEnterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>>;
}

export interface IUserSpecificMethods {
    getCurrentUserData(): Promise<IResponse<IUser>>;
    updateCurrentUserData(user: IUser): Promise<IResponse<IUser>>;
}

export interface IDocumentSpecificMethods {
    getRawFile(document: IDocument): Promise<IResponse<string>>;
}