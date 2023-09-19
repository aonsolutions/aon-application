/**
 *  Configuration parameters
 */


// true para activar que los datos lleguen desde la api, false para usar datos ficticion locales
let APIEnvironment = false;
// true activa unos tests simples para ver que los métodos funcionan correctamente, false para desactivarlos
let test: boolean = false;

/**
 * 
 * CONSTS
 * 
 */

// URL for test environment
const BASE_URL = 'https://aonsolutions.org';

const GET_SINGLE = 'singleObjectGet'
const CREATE_SINGLE = 'singleObjectCreate';
const UPDATE_SINGLE = 'singleObjectUpdate';
const DELETE_SINGLE = 'singleObjectDelete';
const GET_MULTIPLE = 'multipleObjectGet';
const CREATE_MULTIPLE = 'multipleObjectCreate';
const UPDATE_MULTIPLE = 'multipleObjectUpdate';
const DELETE_MULTIPLE = 'multipleObjectDelete';

const GET_METHOD = 'GET';
const POST_METHOD = 'POST';
const PUT_METHOD = 'PUT';
const DELETE_METHOD = 'DELETE';


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
        return new GenericSingleObjectCrud<Document>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Document>(new ApiDocument(), Document) : 
            new GenericSingleObjectCrudRepository<Document>(new StorableDocument(), Document)
            ), 
            Document);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocument> {
        return new GenericMultipleObjectCrud<Document>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<Document>(new ApiDocument(), Document) : 
            new GenericMultipleObjectCrudRepository<Document>(new StorableDocument(), Document)
            ), 
            Document);
    }
}

export class DocumentTagFactory implements ISingleObjectCrudFactory<IDocumentTag>, IMultipleObjectCrudFactory<IDocumentTag> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocumentTag> {
        return new GenericSingleObjectCrud<DocumentTag>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<DocumentTag>(new ApiDocumentTag(), DocumentTag) : 
            new GenericSingleObjectCrudRepository<DocumentTag>(new StorableDocumentTag(), DocumentTag)
            ), 
            DocumentTag);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocumentTag> {
        return new GenericMultipleObjectCrud<DocumentTag>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<DocumentTag>(new ApiDocumentTag(), DocumentTag) : 
            new GenericMultipleObjectCrudRepository<DocumentTag>(new StorableDocumentTag(), DocumentTag)
            ), 
            DocumentTag);
    }
}

export class CertificateFactory implements ISingleObjectCrudFactory<ICertificate>, IMultipleObjectCrudFactory<ICertificate> {
    createSingleObjectCrud(): ISingleObjectCrud<ICertificate> {
        return new GenericSingleObjectCrud<Certificate>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Certificate>(new ApiCertificate(), Certificate) : 
            new GenericSingleObjectCrudRepository<Certificate>(new StorableCertificate(), Certificate)
            ), 
            Certificate);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ICertificate> {
        return new GenericMultipleObjectCrud<Certificate>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<Certificate>(new ApiCertificate(), Certificate) : 
            new GenericMultipleObjectCrudRepository<Certificate>(new StorableCertificate(), Certificate)
            ), 
            Certificate);
    }
}

export class FolderFactory implements ISingleObjectCrudFactory<IFolder>, IMultipleObjectCrudFactory<IFolder> {
    createSingleObjectCrud(): ISingleObjectCrud<IFolder> {
        return new GenericSingleObjectCrud<Folder>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Folder>(new ApiFolder(), Folder) : 
            new GenericSingleObjectCrudRepository<Folder>(new StorableFolder(), Folder)
            ), 
            Folder);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IFolder> {
        return new GenericMultipleObjectCrud<Folder>( 
            (APIEnvironment ? 
            new APIFolderMultipleObjectCrudRepository() : 
            new GenericMultipleObjectCrudRepository<Folder>(new StorableFolder(), Folder)
            ), 
            Folder);
    }
}

export class DocumenNoteFactory implements ISingleObjectCrudFactory<IDocumentNote>, IMultipleObjectCrudFactory<IDocumentNote> {
    createSingleObjectCrud(): ISingleObjectCrud<IDocumentNote> {
        return new GenericSingleObjectCrud<DocumentNote>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<DocumentNote>(new ApiDocumentNote(), DocumentNote) : 
            new GenericSingleObjectCrudRepository<DocumentNote>(new StorableDocumentNote(), DocumentNote)
            ), 
            DocumentNote);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IDocumentNote> {
        return new GenericMultipleObjectCrud<DocumentNote>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<DocumentNote>(new ApiDocumentNote(), DocumentNote) : 
            new GenericMultipleObjectCrudRepository<DocumentNote>(new StorableDocumentNote(), DocumentNote)
            ), 
            DocumentNote);
    }
}

export class MessageFactory implements ISingleObjectCrudFactory<IMessage>, IMultipleObjectCrudFactory<IMessage> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessage> {
        return new GenericSingleObjectCrud<Message>( 
            (APIEnvironment ? 
            // new APIGenericSingleObjectCrudRepository<Message>(new ApiMessage(), Message) : 
            new APIMessageSingleObjectCrudRepository(new ApiMessage(), Message) : 
            new GenericSingleObjectCrudRepository<Message>(new StorableMessage(), Message)
            ), 
            Message);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessage> {
        return new GenericMultipleObjectCrud<Message>( 
            (APIEnvironment ? 
            new APIMessageMultipleObjectCrudRepository(new ApiMessage(), Message) : 
            new GenericMultipleObjectCrudRepository<Message>(new StorableMessage(), Message)
            ), 
            Message);
    }
    createMessageSpecificMethods(): IMessageSpecificMethods {
        return new MessageSpecificMethods();
    }
}

export class TaskHolderFactory implements ISingleObjectCrudFactory<ITaskHolder>, IMultipleObjectCrudFactory<ITaskHolder> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaskHolder> {
        return new GenericSingleObjectCrud<TaskHolder>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<TaskHolder>(new ApiTaskHolder(), TaskHolder) : 
            new GenericSingleObjectCrudRepository<TaskHolder>(new StorableTaskHolder(), TaskHolder)
            ), 
            TaskHolder);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ITaskHolder> {
        return new GenericMultipleObjectCrud<TaskHolder>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<TaskHolder>(new ApiTaskHolder(), TaskHolder) : 
            new GenericMultipleObjectCrudRepository<TaskHolder>(new StorableTaskHolder(), TaskHolder)
            ), 
            TaskHolder);
    }
}

export class MessageChatFactory implements ISingleObjectCrudFactory<IMessageChat>, IMultipleObjectCrudFactory<IMessageChat> {
    createSingleObjectCrud(): ISingleObjectCrud<IMessageChat> {
        return new GenericSingleObjectCrud<MessageChat>( 
            (APIEnvironment ? 
            new APIMessageChatSingleObjectCrudRepository(new ApiMessageChat(), MessageChat) : 
            new GenericSingleObjectCrudRepository<MessageChat>(new StorableMessageChat(), MessageChat)
            ), 
            MessageChat);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMessageChat> {
        return new GenericMultipleObjectCrud<MessageChat>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<MessageChat>(new ApiMessageChat(), MessageChat) : 
            new GenericMultipleObjectCrudRepository<MessageChat>(new StorableMessageChat(), MessageChat)
            ), 
            MessageChat);
    }
}

export class EnterpriseFactory implements ISingleObjectCrudFactory<IEnterprise>, IMultipleObjectCrudFactory<IEnterprise> {
    createSingleObjectCrud(): ISingleObjectCrud<IEnterprise> {
        return new GenericSingleObjectCrud<Enterprise>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Enterprise>(new ApiEnterprise(), Enterprise) : 
            new GenericSingleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise)
            ), 
            Enterprise);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IEnterprise> {
        return new GenericMultipleObjectCrud<Enterprise>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<Enterprise>(new ApiEnterprise(), Enterprise) : 
            new GenericMultipleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise)
            ), 
            Enterprise);
    }
    createSpecificMethods(): IEnterpriseSpecificMethods {
        return new EnterpriseSpecificMethods(new LocalEnterpriseSpecificMethodsRepository());
    }
}

export class BankFactory implements ISingleObjectCrudFactory<IBank>, IMultipleObjectCrudFactory<IBank> {
    createSingleObjectCrud(): ISingleObjectCrud<IBank> {
        return new GenericSingleObjectCrud<Bank>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Bank>(new ApiBank(), Bank) : 
            new GenericSingleObjectCrudRepository<Bank>(new StorableBank(), Bank)
            ), 
            Bank);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IBank> {
        return new GenericMultipleObjectCrud<Bank>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<Bank>(new ApiBank(), Bank) : 
            new GenericMultipleObjectCrudRepository<Bank>(new StorableBank(), Bank)
            ), 
            Bank);
    }
}

export class RegistryEnterpriseFactory implements ISingleObjectCrudFactory<IRegistryEnterprise>, IMultipleObjectCrudFactory<IRegistryEnterprise> {
    createSingleObjectCrud(): ISingleObjectCrud<IRegistryEnterprise> {
        throw new Error('Method not implemented.');
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IRegistryEnterprise> {
        throw new Error('Method not implemented.');
    }
  }

export class TaxModelFactory implements ISingleObjectCrudFactory<ITaxModel>, IMultipleObjectCrudFactory<ITaxModel> {
    createSingleObjectCrud(): ISingleObjectCrud<ITaxModel> {
        return new GenericSingleObjectCrud<TaxModel>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<TaxModel>(new ApiTaxModel(), TaxModel) : 
            new GenericSingleObjectCrudRepository<TaxModel>(new StorableTaxModel(), TaxModel)
            ), 
            TaxModel);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<ITaxModel> {
        return new GenericMultipleObjectCrud<TaxModel>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<TaxModel>(new ApiTaxModel(), TaxModel) : 
            new GenericMultipleObjectCrudRepository<TaxModel>(new StorableTaxModel(), TaxModel)
            ), 
            TaxModel);
    }
}

export class EmployeeFactory implements ISingleObjectCrudFactory<IEmployee>, IMultipleObjectCrudFactory<IEmployee> {
    createSingleObjectCrud(): ISingleObjectCrud<IEmployee> {
        return new GenericSingleObjectCrud<Employee>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Employee>(new ApiEmployee(), Employee) : 
            new GenericSingleObjectCrudRepository<Employee>(new StorableEmployee(), Employee)
            ), 
            Employee);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IEmployee> {
        return new GenericMultipleObjectCrud<Employee>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<Employee>(new ApiEmployee(), Employee) : 
            new GenericMultipleObjectCrudRepository<Employee>(new StorableEmployee(), Employee)
            ), 
            Employee);
    }
}

export class AuthenticationFactory implements IAuthenticationManagerFactory {
    createAuthenticationManager(): IAuthenticationManager {
        return new AuthenticationManager(APIEnvironment ? new APIAuthenticationRepository() : new AuthenticationRepository(new StorableUser()));
    }
}

export class ReportingFactory implements IReportingDataAccessFactory {
    createReportingDataAccess(): IReportingDataAccess {
        // TO DO - create reporting repository for api
        return new ReportingDataAccess(new ReportingRepository());
    }
}

export class MarkFactory implements ISingleObjectCrudFactory<IMark>, IMultipleObjectCrudFactory<IMark> {
    createSingleObjectCrud(): ISingleObjectCrud<IMark> {
        return new GenericSingleObjectCrud<Mark>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<Mark>(new ApiMark(), Mark) : 
            new GenericSingleObjectCrudRepository<Mark>(new StorableMark(), Mark)
            ), 
            Mark);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMark> {
        return new GenericMultipleObjectCrud<Mark>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<Mark>(new ApiMark(), Mark) : 
            new GenericMultipleObjectCrudRepository<Mark>(new StorableMark(), Mark)
            ), 
            Mark);
    }
    createSpecificMethods(): IMarkSpecificMethods {
        return new MarkSpecificMethods();
    }
}

export class MarkDetailFactory implements ISingleObjectCrudFactory<IMarkDetail>, IMultipleObjectCrudFactory<IMarkDetail> {
    createSingleObjectCrud(): ISingleObjectCrud<IMarkDetail> {
        return new GenericSingleObjectCrud<MarkDetail>( 
            (APIEnvironment ? 
            new APIGenericSingleObjectCrudRepository<MarkDetail>(new ApiMarkDetail(), MarkDetail) : 
            new GenericSingleObjectCrudRepository<MarkDetail>(new StorableMarkDetail(), MarkDetail)
            ), 
            MarkDetail);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IMarkDetail> {
        return new GenericMultipleObjectCrud<MarkDetail>( 
            (APIEnvironment ? 
            new APIGenericMultipleObjectCrudRepository<MarkDetail>(new ApiMarkDetail(), MarkDetail) : 
            new GenericMultipleObjectCrudRepository<MarkDetail>(new StorableMarkDetail(), MarkDetail)
            ), 
            MarkDetail);
    }
}

export class UserFactory implements ISingleObjectCrudFactory<IUser>, IMultipleObjectCrudFactory<IUser> {
    createSingleObjectCrud(): ISingleObjectCrud<IUser> {
        throw new Error('Method not implemented.');
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IUser> {
        throw new Error('Method not implemented.');
    }
    createSpecificMethods(): IUserSpecificMethods {
        return new UserSpecificMethods(new LocalUserSpecificMethodsRepository());
    }
}

export class ContractFactory implements ISingleObjectCrudFactory<IContract>, IMultipleObjectCrudFactory<IContract> {
    createSingleObjectCrud(): ISingleObjectCrud<IContract> {
        return new GenericSingleObjectCrud<Contract>(new GenericSingleObjectCrudRepository<Contract>(new StorableContract(), Contract), Contract);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IContract> {
        return new GenericMultipleObjectCrud<Contract>(new GenericMultipleObjectCrudRepository<Contract>(new StorableContract(), Contract), Contract);
    }

}

export class ContactFactory implements ISingleObjectCrudFactory<IContact>, IMultipleObjectCrudFactory<IContact> {
    createSingleObjectCrud(): ISingleObjectCrud<IContact> {
        return new GenericSingleObjectCrud<Contact>(new GenericSingleObjectCrudRepository<Contact>(new StorableContact(), Contact), Contact);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IContact> {
        return new GenericMultipleObjectCrud<Contact>(new GenericMultipleObjectCrudRepository<Contact>(new StorableContact(), Contact), Contact);
    }
}

export class ProductFactory implements ISingleObjectCrudFactory<IProduct>, IMultipleObjectCrudFactory<IProduct> {
    createSingleObjectCrud(): ISingleObjectCrud<IProduct> {
        return new GenericSingleObjectCrud<Product>(new GenericSingleObjectCrudRepository<Product>(new StorableProduct(), Product), Product);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IProduct> {
        return new GenericMultipleObjectCrud<Product>(new GenericMultipleObjectCrudRepository<Product>(new StorableProduct(), Product), Product);
    }
    createSpecificMethods(): IProductSpecificMethods {
        return new ProductSpecificMethods(new LocalProductSpecificMethodsRepository());
    }
}

class IRPFFactory implements IMultipleObjectCrudFactory<IIRPF> {
    createMultipleObjectCrud(): IMultipleObjectReader<IIRPF> {
        return new GenericMultipleObjectCrud<IRPF>(new GenericMultipleObjectCrudRepository<IRPF>(new StorableIRPF(), IRPF), IRPF);
    }
}

class TaxFactory implements IMultipleObjectCrudFactory<ITax> {
    createMultipleObjectCrud(): IMultipleObjectReader<ITax> {
        return new GenericMultipleObjectCrud<Tax>(new GenericMultipleObjectCrudRepository<Tax>(new StorableTax(), Tax), Tax);
    }
}

class PaymentMethodFactory implements IMultipleObjectCrudFactory<IPaymentMethod> {
    createMultipleObjectCrud(): IMultipleObjectReader<IPaymentMethod> {
        return new GenericMultipleObjectCrud<PaymentMethod>(new GenericMultipleObjectCrudRepository<PaymentMethod>(new StorablePaymentMethod(), PaymentMethod), PaymentMethod);
    }
}

class InvoiceCategoryFactory implements IMultipleObjectCrudFactory<IInvoiceCategory> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceCategory> {
        return new GenericMultipleObjectCrud<InvoiceCategory>(new GenericMultipleObjectCrudRepository<InvoiceCategory>(new StorableInvoiceCategory(), InvoiceCategory), InvoiceCategory);
    }
}

class InvoiceSerieFactory implements IMultipleObjectCrudFactory<IInvoiceSerie> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceSerie> {
        return new GenericMultipleObjectCrud<InvoiceSerie>(new GenericMultipleObjectCrudRepository<InvoiceSerie>(new StorableInvoiceSerie(), InvoiceSerie), InvoiceSerie);
    }
}

class InvoiceActivityFactory implements IMultipleObjectCrudFactory<IInvoiceActivity> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceActivity> {
        return new GenericMultipleObjectCrud<InvoiceActivity>(new GenericMultipleObjectCrudRepository<InvoiceActivity>(new StorableInvoiceActivity(), InvoiceActivity), InvoiceActivity);
    }
}

class InvoiceTransactionTypeFactory implements IMultipleObjectCrudFactory<IInvoiceTransactionType> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceTransactionType> {
        return new GenericMultipleObjectCrud<InvoiceTransactionType>(new GenericMultipleObjectCrudRepository<InvoiceTransactionType>(new StorableInvoiceTransactionType(), InvoiceTransactionType), InvoiceTransactionType);
    }
}

class ProductCodeFactory implements IMultipleObjectCrudFactory<IProductCode> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductCode> {
        return new GenericMultipleObjectCrud<ProductCode>(new GenericMultipleObjectCrudRepository<ProductCode>(new StorableProductCode(), ProductCode), ProductCode);
    }
} 

class ProductClassFactory implements IMultipleObjectCrudFactory<IProductClass> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductClass> {
        return new GenericMultipleObjectCrud<ProductClass>(new GenericMultipleObjectCrudRepository<ProductClass>(new StorableProductClass(), ProductClass), ProductClass);
    }
}

class ProductCategoryFactory implements IMultipleObjectCrudFactory<IProductCategory> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductCategory> {
        return new GenericMultipleObjectCrud<ProductCategory>(new GenericMultipleObjectCrudRepository<ProductCategory>(new StorableProductCategory(), ProductCategory), ProductCategory);
    }
}

class ProductTypeFactory implements IMultipleObjectCrudFactory<IProductType> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductType> {
        return new GenericMultipleObjectCrud<ProductType>(new GenericMultipleObjectCrudRepository<ProductType>(new StorableProductType(), ProductType), ProductType);
    }
}

class ProductStatusFactory implements IMultipleObjectCrudFactory<IProductStatus> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductStatus> {
        return new GenericMultipleObjectCrud<ProductStatus>(new GenericMultipleObjectCrudRepository<ProductStatus>(new StorableProductStatus(), ProductStatus), ProductStatus);
    }
}

export class InvoiceFactory implements ISingleObjectCrudFactory<IInvoice>, IMultipleObjectCrudFactory<IInvoice> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoice> {
        return new GenericMultipleObjectCrud<Invoice>(new GenericMultipleObjectCrudRepository<Invoice>(new StorableInvoice(), Invoice), Invoice);
    }

    createSingleObjectCrud(): ISingleObjectCrud<IInvoice> {
        return new GenericSingleObjectCrud<Invoice>(new GenericSingleObjectCrudRepository<Invoice>(new StorableInvoice(), Invoice), Invoice);
    }

    createSpecificMethods(): IInvoiceSpecificMethods {
        return new InvoiceSpecificMethods(new LocalInvoiceSpecificMethodsRepository());
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
    setEnterprise(enterprise: IEnterprise): IResponse<boolean>;
}

/**
 * Interface for methods of reporting data
 */
interface IReportingDataAccess {
    cobrosPagos(): Promise<IResponse<Object>>;
    ventasGastos(): Promise<IResponse<Object>>;
}

/**
 * Interface for specific methods of message 
 */
interface IMessageSpecificMethods {
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
    markAsReadNotification(message: Message): Promise<IResponse<boolean>>;
}

interface IMarkSpecificMethods {
    /**
     * Returns all the marks for one user
     */
    getMarksOfOneUser(userId: string, filter: IFilter): Promise<IResponse<ICollection<IMark>>>;
}

interface IMarkDetailSpecificMethods {
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

interface IProductSpecificMethods {
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

interface IInvoiceSpecificMethods {
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

interface IEnterpriseSpecificMethods {
    getCurrentEnterpriseData(): Promise<IResponse<IEnterprise>>;
    getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>>;
    updateCurrentEnterpriseData(enterprise: IEnterprise): Promise<IResponse<IEnterprise>>;
    updateCurrentEnterpriseRegistryData(registryEnterprise: IRegistryEnterprise): Promise<IResponse<IRegistryEnterprise>>;
}

interface IUserSpecificMethods {
    getCurrentUserData(): Promise<IResponse<IUser>>;
    updateCurrentUserData(user: IUser): Promise<IResponse<IUser>>;
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
            throw error;
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

    setEnterprise(enterprise: IEnterprise): IResponse<boolean> {
        if(enterprise){
            localStorage.setItem('enterprise', enterprise.Document);
            localStorage.setItem('domainId', enterprise.DomainId);
            localStorage.setItem('domainName', enterprise.DomainName);
            localStorage.setItem('registry', enterprise.Registry);
            this.authenticationRepository.userInfo();
        }
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

class MessageSpecificMethods implements IMessageSpecificMethods {

    repository = new APIMessageSpecificMethodsRepository();

    async archiveMessage(element: Message): Promise<IResponse<IMessage>> {
        return new Response<IMessage>(await this.repository.archiveMessage(element));
    }

    async reopenMessage(element: Message): Promise<IResponse<IMessage>> {
        return new Response<IMessage>(await this.repository.reopenMessage(element));
    }

    async getMessageCount(filter?: IFilter): Promise<IResponse<number>> {
        return new Response<number>(await this.repository.getMessageCount(filter));
    }

    async markAsReadNotification(message: Message): Promise<IResponse<boolean>> {
        return new Response<boolean>(await this.repository.markAsReadNotification(message));
    }

}

class MarkSpecificMethods implements IMarkSpecificMethods {
    
    repository = new APIMarkSpecificMethodsRepository();
    
    async getMarksOfOneUser(userId: string, filter?: IFilter): Promise<IResponse<ICollection<IMark>>> {
        try{
            return new Response<ICollection<IMark>>(await this.repository.getMarksOfOneUser(userId, filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}

class ProductSpecificMethods implements IProductSpecificMethods {

    protected SpecificMethodsRepository: LocalProductSpecificMethodsRepository;

    constructor(ProductSpecificMethodsRepository: LocalProductSpecificMethodsRepository) {
        this.SpecificMethodsRepository = ProductSpecificMethodsRepository;
    }

    async getProductStatus(filter?: IFilter): Promise<IResponse<ICollection<IProductStatus>>> {
        try{
            return new Response<ICollection<IProductStatus>>(this.SpecificMethodsRepository.getProductStatus(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductCodes(filter?: IFilter): Promise<IResponse<ICollection<IProductCode>>> {
        try{
            return new Response<ICollection<IProductCode>>(this.SpecificMethodsRepository.getProductCodes(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductTypes(filter?: IFilter): Promise<IResponse<ICollection<IProductType>>> {
        try{
            return new Response<ICollection<IProductType>>(this.SpecificMethodsRepository.getProductTypes(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductClasses(filter?: IFilter): Promise<IResponse<ICollection<IProductClass>>> {
        try{
            return new Response<ICollection<IProductClass>>(this.SpecificMethodsRepository.getProductClasses(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getProductCategories(filter?: IFilter): Promise<IResponse<ICollection<IProductCategory>>> {
        try{
            return new Response<ICollection<IProductCategory>>(this.SpecificMethodsRepository.getProductCategories(filter));
        }catch(error){
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
    
}

class InvoiceSpecificMethods implements IInvoiceSpecificMethods {
    protected SpecificMethodsRepository: LocalInvoiceSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: LocalInvoiceSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    async getTaxList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getTaxList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getIRPFList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IIRPF>>> {
        try {
            return new Response<ICollection<IIRPF>>(this.SpecificMethodsRepository.getIRPFList());   
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getInvoiceCategoryList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceCategory>>> {
        try {
            return new Response<ICollection<IInvoiceCategory>>(this.SpecificMethodsRepository.getInvoiceCategoryList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getInvoiceSerieList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceSerie>>> {
        try {
            return new Response<ICollection<IInvoiceSerie>>(this.SpecificMethodsRepository.getInvoiceSerieList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getTransactionTypeList(filter?: IFilter | undefined): Promise<IResponse<ICollection<IInvoiceTransactionType>>> {
        try {
            return new Response<ICollection<IInvoiceTransactionType>>(this.SpecificMethodsRepository.getTransactionTypeList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getPaymentMethodList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getPaymentMethodList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getInvoiceActivityList(filter?: IFilter | undefined): Promise<IResponse<ICollection<ITax>>> {
        try {
            return new Response<ICollection<ITax>>(this.SpecificMethodsRepository.getInvoiceActivityList());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}

class EnterpriseSpecificMethods implements IEnterpriseSpecificMethods {
    protected SpecificMethodsRepository: IEnterpriseSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: IEnterpriseSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    async getCurrentEnterpriseData(): Promise<IResponse<IEnterprise>> {
        try {
            return new Response<IEnterprise>(await this.SpecificMethodsRepository.getCurrentEnterpriseData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async getCurrentEnterpriseRegistryData(): Promise<IResponse<IRegistryEnterprise>> {
        try {
            return new Response<IRegistryEnterprise>(await this.SpecificMethodsRepository.getCurrentEnterpriseRegistryData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async updateCurrentEnterpriseData(enterprise: Enterprise): Promise<IResponse<IEnterprise>> {
        try {
            return new Response<IEnterprise>(await this.SpecificMethodsRepository.updateCurrentEntepriseData(enterprise));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async updateCurrentEnterpriseRegistryData(registryEnterprise: RegistryEnterprise): Promise<IResponse<IRegistryEnterprise>> {
        try {
            return new Response<IRegistryEnterprise>(await this.SpecificMethodsRepository.updateCurrentEnterpriseRegistryData(registryEnterprise));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }
}

class UserSpecificMethods implements IUserSpecificMethods {
    protected SpecificMethodsRepository: IUserSpecificMethodsRepository;

    constructor(SpecificMethodsRepository: IUserSpecificMethodsRepository){
        this.SpecificMethodsRepository = SpecificMethodsRepository;
    }

    async getCurrentUserData(): Promise<IResponse<IUser>> {
        try {
            return new Response<IUser>(await this.SpecificMethodsRepository.getCurrentUserData());
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
    }

    async updateCurrentUserData(user: User): Promise<IResponse<IUser>> {
        try {
            return new Response<IUser>(await this.SpecificMethodsRepository.updateCurrentUserData(user));
        } catch (error) {
            throw error instanceof ErrorResponse ?  error : new ErrorResponse('0123');
        }
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
    /**
     * Set login on localStorage
     */
    userInfo(): Promise<void>;
}

interface IReportingRepository {
    cobrosPagos(): Promise<any>;
    ventasGastos(): Promise<any>;
}

interface IMessageSpecificMethodsRepository {
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

interface IMarkSpecificMethodsRepository {
    getMarksOfOneUser(userId: string, filter: IFilter): Promise<ICollection<IMark>>;
}

interface IProductSpecificMethodsRepository {
    getProductStatus(filter: IFilter): Promise<ICollection<IProductStatus>>;
    getProductCodes(filter: IFilter): Promise<ICollection<IProductCode>>;
    getProductTypes(filter: IFilter): Promise<ICollection<IProductType>>;
    getProductClasses(filter: IFilter): Promise<ICollection<IProductClass>>;
    getProductCategories(filter: IFilter): Promise<ICollection<IProductCategory>>;
}

interface IInvoiceSpecificMethodsRepository {
    getTaxList(filter?: IFilter | undefined): Promise<ICollection<ITax>>;
    getIRPFList(filter?: IFilter | undefined): Promise<ICollection<IIRPF>>;
    getInvoiceCategoryList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceCategory>>;
    getInvoiceSerieList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceSerie>>;
    getTransactionTypeList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceTransactionType>>;
    getPaymentMethodList(filter?: IFilter | undefined): Promise<ICollection<ITax>>;
    getInvoiceActivityList(filter?: IFilter | undefined): Promise<ICollection<ITax>>;
}

interface IEnterpriseSpecificMethodsRepository {
    getCurrentEnterpriseData(): Promise<IEnterprise>;
    updateCurrentEntepriseData(enterprise: IEnterprise): Promise<IEnterprise>;
    getCurrentEnterpriseRegistryData(): Promise<IRegistryEnterprise>;
    updateCurrentEnterpriseRegistryData(registryEnterprise: IRegistryEnterprise): Promise<IRegistryEnterprise>;
}

interface IUserSpecificMethodsRepository {
    getCurrentUserData(): Promise<IUser>;
    updateCurrentUserData(user: IUser): Promise<IUser>;
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

    private storableAuth: IStorable<IUser>;

    constructor(storableAuth: IStorable<IUser>) {
        this.storableAuth = storableAuth;
    }

    async userInfo(): Promise<void> {
    }

    async login(email: string, password: string): Promise<void> {
        let filter = new FilterBuilder();
        filter.addField('email', email);
        let collection = this.storableAuth.getCollection().filter(filter.getFilter());
        if(collection.size() == 1 && collection.toArray()[0].Password == password){
            localStorage.setItem('token', 'testToken')
            localStorage.setItem('user', collection.toArray()[0].Document)
        }
        else throw new ErrorResponse('0101');
    }

    async logout(): Promise<void> {
        if(localStorage.getItem('token')) { 
            // localStorage.removeItem('token'); 
            // localStorage.removeItem('enterprise');
            // localStorage.removeItem('domainId'); 
            // localStorage.removeItem('domainName');
            // localStorage.removeItem('user');
            localStorage.clear();
        }
        else throw new ErrorResponse('0111');
    }

    async tokenLogin(token: string): Promise<void> {
        if(!localStorage.getItem('token')) localStorage.setItem('token', 'testToken');
        else throw new ErrorResponse('0101');
    }
}


// REPOSITORIO PARA LAS LLAMADAS GLOBALES A LA API PARA OPERACIONES CRUD SOBRE UN SOLO OBJETO, 
// SI SE NECESITA UN COMPORTAMIENTO ESPECIFICO HEREDAR Y SOBREESCRIBIR DICHO MÉTODO
class APIGenericSingleObjectCrudRepository<T extends IModel> implements ISingleObjectCrudRepository<T> {
    protected httpRequest: IApiHttpRequest = new ApiHttpRequest();
    protected type: { new (): T };
    protected model: IModel;
    protected apiModel: IApiModel;

    constructor(apiModel: IApiModel, type: { new (): T }){
        this.type = type;
        this.model = new this.type();
        this.apiModel = apiModel;
    }

    async get(key: string, type?: string): Promise<T> {
        // TO DO revisar filter, type y id
        let filter = new FilterBuilder();
        filter.addField('id',key);
        if(type) filter.addField('type',type)
        let url = this.apiModel.getUrl(GET_SINGLE, filter.getFilter());
        let method = this.apiModel.getMethod(GET_SINGLE, filter.getFilter());
        let response = await this.httpRequest.httpRequest(BASE_URL + url, method, {}, {})
        return this.apiModel.parseDataToReceive(response, GET_SINGLE);
    }

    async create(element: T): Promise<T> {
        let data = this.apiModel.parseDataToSend(element, CREATE_SINGLE);
        let response = await this.httpRequest.httpRequest(BASE_URL + this.apiModel.getUrl(CREATE_SINGLE), this.apiModel.getMethod(CREATE_SINGLE), {}, data);
        return this.apiModel.parseDataToReceive(response, GET_SINGLE);
    }

    async update(element: T): Promise<T> {
        let data = this.apiModel.parseDataToSend(element, UPDATE_SINGLE);
        let url = this.apiModel.getUrl(UPDATE_SINGLE);
        let method = this.apiModel.getMethod(UPDATE_SINGLE);
        let response = await this.httpRequest.httpRequest(BASE_URL + url, method, element, data)
        if(response) return element;
        throw new ErrorResponse('0199')
    }

    async delete(key: string): Promise<void> {
        throw new ErrorResponse('0199')
    }

}

// REPOSITORIO PARA LAS LLAMADAS GLOBALES A LA API PARA OPERACIONES CRUD SOBRE UN CONJUNTO DE OBJETOS, 
// SI SE NECESITA UN COMPORTAMIENTO ESPECIFICO HEREDAR Y SOBREESCRIBIR DICHO MÉTODO
class APIGenericMultipleObjectCrudRepository<T extends IModel> implements IMultipleObjectCrudRepository<T> {

    protected httpRequest: IApiHttpRequest = new ApiHttpRequest();
    protected type: { new (): T };
    protected model: IModel;
    protected apiModel: IApiModel;

    constructor(apiModel: IApiModel, type: { new (): T }){
        this.type = type;
        this.model = new this.type();
        this.apiModel = apiModel;
    }

    async get(filter?: IFilter): Promise<ICollection<T>> {
        let urls = this.apiModel.getUrl(GET_MULTIPLE,filter);
        let method = this.apiModel.getMethod(GET_MULTIPLE,filter);
        let collection: ICollection<T> = new Collection<T>();
        for(let url of urls){
            let response = await this.httpRequest.httpRequest(BASE_URL + url, method, {}, {})
            response.forEach((element: any) => {
                collection.add(this.apiModel.parseDataToReceive(element, GET_MULTIPLE, filter))
            })
        }
        if(this.apiModel.localFilter() && collection.size() > 0){
            if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
            if(filter?.orderBy) collection.sort(filter);
            if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        }
        return collection;
    }

    async create(collection: ICollection<T>): Promise<ICollection<T>> {
        throw new ErrorResponse('0199')
    }

    async update(collection: ICollection<T>): Promise<ICollection<T>> {
        throw new ErrorResponse('0199')
    }

    async delete(keys: string[]): Promise<void> {
        throw new ErrorResponse('0199')
    }

}

class APIFolderMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Folder> {

    constructor(){
        super(new ApiFolder(), Folder);
    }

    async get(filter?: IFilter | undefined): Promise<ICollection<Folder>> {
        let collection: ICollection<Folder> = new Collection<Folder>();
        for(let folder of apiFolders)
            collection.add(folder)
        let response = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/workplace',GET_METHOD,{},{})
        let workplaces = ''
        response.forEach((element: any) => {
            workplaces+=element.id + ';'
        })
        let filterFolder = new FilterBuilder();
        if(!filter) {
            filterFolder.addField('workplace', workplaces)
        }else{
            filter.fields?.set('workplace', workplaces)
        }
        collection.copyArrayToCollection((await super.get(filter ? filter : filterFolder.getFilter())).toArray())
        return collection;
    }

}

class APIMessageSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }

    async create(message: Message): Promise<Message> {
        let cauInfo = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/cau', GET_METHOD, {}, {})
        let sender = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/taskholder?id=' + localStorage.getItem('registry'), GET_METHOD, {}, {})
        let taskHolder = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/taskholder?id=' + message.TaskHolder.Id, GET_METHOD, {}, {})
        let domain = {
            id: localStorage.getItem('domainId'),
            name: localStorage.getItem('domainName')
        }
        let workflow = {
            comment: "",
            domain: localStorage.getItem('domainId'),
            task_holder: sender,
            type: "opened",
            email: cauInfo.auth.email || ''
        }
        let description = JSON.stringify({
            observation: message.Description,
            cauinfo: cauInfo,
        })
        let json = {
            domain: domain,
            sender: sender,
            task_holder: taskHolder,
            workgroup: {},
            title: message.Title,
            registry: {},
            workflow: [workflow],
            description: description,
            status: 'pending',
            source: 'query',
            source_id: null,
            files: [],
            project: {},
            tags: [],
            childs: [],
            domaintmp: domain,
            workflowtmp: workflow,
            mytaskholder: sender,
            auth: cauInfo.auth,
            domaincompany: cauInfo.company.domain
        }
        let newMessage: Message = this.apiModel.parseDataToReceive(await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task', POST_METHOD, {}, json), GET_SINGLE);
        return newMessage;
    }
}

class APIMessageMultipleObjectCrudRepository extends APIGenericMultipleObjectCrudRepository<Message> {
    constructor(apiModel: ApiMessage, type: { new (): Message }){
        super(apiModel, type);
    }
}

class APIMessageChatSingleObjectCrudRepository extends APIGenericSingleObjectCrudRepository<MessageChat> {
    constructor(apiModel: ApiMessageChat, type: { new (): MessageChat }){
        super(apiModel, type);
    }

    async create(messageChat: MessageChat): Promise<MessageChat> {
        //comment
        //domain
        //email
        //task
        //task_holder
        //type
        let sender = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/taskholder?id=' + localStorage.getItem('registry'), GET_METHOD, {}, {})
        let json = {
            comment: messageChat.Description,
            domain: localStorage.getItem('domainId'),
            email: '',
            task: messageChat.IdMessage,
            task_holder: sender,
            type: 'comment'
        }
        let newMessage: MessageChat = this.apiModel.parseDataToReceive(await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/workflow', POST_METHOD, {}, json), GET_MULTIPLE);
        return newMessage;
        throw new ErrorResponse('0199')
    }
}

class APIMessageSpecificMethodsRepository implements IMessageSpecificMethodsRepository {

    protected httpRequest: IApiHttpRequest = new ApiHttpRequest();
    repository = new APIGenericSingleObjectCrudRepository<Message>(new ApiMessage(), Message);

    async archiveMessage(element: Message): Promise<IMessage> {
        if(element.Type != 'notificacion'){
            element.Status = 'deleted';
            let updatedMessage: IMessage = await this.repository.update(element);
            return updatedMessage;
        }
        throw new ErrorResponse('0199');
    }

    async reopenMessage(element: Message): Promise<IMessage> {
        if(element.Type != 'notificacion'){
            element.Status = 'pending'
            let updatedMessage: IMessage = await this.repository.update(element);
            return updatedMessage;
        }
        throw new ErrorResponse('0199');
    }

    async getMessageCount(filter?: IFilter): Promise<number> {
        if(filter && filter.fields && filter.fields.get('type') == 'notificacion'){
            let result = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/notification/total-notification', GET_METHOD, {}, {})
            return result.notification
        } else if(filter && filter.fields && (filter.fields.get('type') == 'tarea' || filter.fields.get('type') == 'consulta')){
            let result = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/status/count?task_holder=' + localStorage.getItem('registry'), GET_METHOD, {}, {});
            return result.status.pending;
        } else {
            let result1 = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/notification/total-notification', GET_METHOD, {}, {})
            let result2 = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/task/status/count?task_holder=' + localStorage.getItem('registry'), GET_METHOD, {}, {});
            return result1.notification + result2.status.pending;
        }
    }

    async markAsReadNotification(element: Message): Promise<boolean> {
        if(element.Type == 'notificacion'){
            let result = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/notification/mark-read-notification', POST_METHOD, {}, {source_id: element.Id});
            if(result.success && result.success == true)
                return true;
            else 
                return false;
        }
        else
            throw new ErrorResponse('0199');
    }
}

class APIMarkSpecificMethodsRepository implements IMarkSpecificMethodsRepository {
    private http: ApiHttpRequest = new ApiHttpRequest();
    private model: ApiMark = new ApiMark();
    
    async getMarksOfOneUser(userId: string, filter?: IFilter): Promise<ICollection<IMark>> {
        let url = '/ms/api/timecontrol/list-holder';
        let params = {
            taskHolderId: userId,
            group: 'DAY',
            startDate: '2023-09-04',
            endDate: '2023-09-08'
        }
        url = this.http.makeURL(url, params);
        let method = GET_METHOD;
        let collection: ICollection<IMark> = new Collection<IMark>();
        let response = await this.http.httpRequest(BASE_URL + url, method, {}, {})
        response.forEach((element: any) => {
            collection.add(this.model.parseDataToReceive(element, GET_MULTIPLE, filter))
        })
        if(this.model.localFilter() && collection.size() > 0){
            if(filter?.intervalFields || filter?.fields) collection = collection.filter(filter);
            if(filter?.orderBy) collection.sort(filter);
            if(filter?.pageItems && filter.pageNum) collection = collection.paginate(filter.pageNum,filter.pageItems);
        }
        return collection;
    }
}

class APIAuthenticationRepository implements IAuthenticationRepository {

    httpRequest = new ApiHttpRequest();

    constructor() {
    }

    async login(email: string, password: string): Promise<void> {
        let data = {
            username: email,
            password: password
        }
        let url = BASE_URL + '/ms/api/login'
        let method = 'POST';
        let customHeaders = {}
        let result = await this.httpRequest.httpRequest(url,method,customHeaders,data)
        if(result.type == "error") throw new ErrorResponse('0101');
        else localStorage.setItem('token', result.session_id);
    }

    async logout(): Promise<void> {
        if(localStorage.getItem('token')) { 
            localStorage.removeItem('token'); 
            localStorage.removeItem('enterprise');
            localStorage.removeItem('domainId'); 
            localStorage.removeItem('domainName'); 
        }
        else throw new ErrorResponse('0111');
    }

    async tokenLogin(token: string): Promise<void> {
    }

    async userInfo(): Promise<void> {
        let userInfo = await this.httpRequest.httpRequest(BASE_URL + '/ms/api/user/info', GET_METHOD, {}, {})
        if(userInfo.type == "error") throw new ErrorResponse('0101');
        localStorage.setItem('login', userInfo.login);
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

class LocalProductSpecificMethodsRepository implements IProductSpecificMethodsRepository {
    async getProductStatus(filter?: IFilter): Promise<ICollection<IProductStatus>> {
        return (await new ProductStatusFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductCodes(filter?: IFilter): Promise<ICollection<IProductCode>> {
        return (await new ProductCodeFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductTypes(filter?: IFilter): Promise<ICollection<IProductType>> {
        return (await new ProductTypeFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductClasses(filter?: IFilter): Promise<ICollection<IProductClass>> {
        return (await new ProductClassFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
    async getProductCategories(filter?: IFilter): Promise<ICollection<IProductCategory>> {
        return (await new ProductCategoryFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
}

class LocalInvoiceSpecificMethodsRepository implements IInvoiceSpecificMethodsRepository {
    async getTaxList(filter?: IFilter | undefined): Promise<ICollection<ITax>> {
        return (await new TaxFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getIRPFList(filter?: IFilter | undefined): Promise<ICollection<IIRPF>> {
        return (await new IRPFFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getInvoiceCategoryList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceCategory>> {
        return (await new InvoiceCategoryFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getInvoiceSerieList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceSerie>> {
        return (await new InvoiceSerieFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getTransactionTypeList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceTransactionType>> {
        return (await new InvoiceTransactionTypeFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getPaymentMethodList(filter?: IFilter | undefined): Promise<ICollection<ITax>> {
        return (await new PaymentMethodFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getInvoiceActivityList(filter?: IFilter | undefined): Promise<ICollection<ITax>> {
        return (await new InvoiceActivityFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
}

class LocalEnterpriseSpecificMethodsRepository implements IEnterpriseSpecificMethodsRepository {
    async getCurrentEnterpriseData(): Promise<IEnterprise> {
        return enterprises.get(localStorage.getItem('enterprise') || '');
    }

    async updateCurrentEntepriseData(enterprise: Enterprise): Promise<IEnterprise> {
        return  new GenericSingleObjectCrudRepository<Enterprise>(new StorableEnterprise(), Enterprise).update(enterprise);
    }

    async getCurrentEnterpriseRegistryData(): Promise<IRegistryEnterprise> {
        return registryEnterprises.get(localStorage.getItem('enterprise') || '');
    }

    async updateCurrentEnterpriseRegistryData(registryEnterprise: RegistryEnterprise): Promise<IRegistryEnterprise> {
        return  new GenericSingleObjectCrudRepository<RegistryEnterprise>(new StorableRegistryEnterprise(), RegistryEnterprise).update(registryEnterprise);
    }
}

class LocalUserSpecificMethodsRepository implements IUserSpecificMethodsRepository {
    async getCurrentUserData(): Promise<IUser> {
        return users.get(localStorage.getItem('user') || '');
    }

    async updateCurrentUserData(user: User): Promise<IUser> {
        return new GenericSingleObjectCrudRepository<User>(new StorableUser(), User).update(user);
    }
}

/*
 *
 * UTILITIES INTERFACE, FOR EXAMPLE COLLECTION TO MANAGE A LIST OF OBJECTS
 *
 */

interface IApiHttpRequest {
    httpRequest(url: string, method: string, customHeaders: any, data: any): Promise<any>;
    makeURL(url: string, params: any): string;
}

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

class ApiHttpRequest implements IApiHttpRequest {
    async httpRequest(url: string, method: string, customHeaders: any = {}, data: any): Promise<any> {
        let headersAuth = {
            session_id: localStorage.getItem('token'),
            domain_name: localStorage.getItem('domainName'),
            domain_id: localStorage.getItem('domainId'),
            domain_login: localStorage.getItem('login'),
        }
        let headers = new Object();
        Object.assign(headers,customHeaders);
        Object.assign(headers,headersAuth);
        let options = new Object();
        Object.defineProperty(options,'method',{value: method});
        Object.defineProperty(options,'headers',{value: headers});
        if(method == 'POST') Object.defineProperty(options,'body',{value: JSON.stringify(data)});
        let result = await fetch(url,options);
        let dataJson = await result.json();
        return dataJson;
    }
    
    makeURL(url: string, params: any): string {
        const esc = encodeURIComponent;
        return url + '?' + Object.keys(params).map(k => `${esc(k)}=${esc(params[k as keyof typeof params])}`).join('&')
    }
}

class KeyGenerator {
    static characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    constructor(){}
    static generate(length: number): string {
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
        if(!this.filter.fields?.has(field)) this.filter.fields?.set(field, []);
        this.filter.fields?.get(field)?.push(value);
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
                    element.getFilterableFields().has(key.toLowerCase()) ? value.includes(element.getFilterableFields().get(key.toLowerCase())) : true//element.getFilterableFields().get(key.toLowerCase()).includes(value)
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

export interface IFactory {
    createDocument(): IDocument;
    createFolder(): IFolder;
    createCertificate(): ICertificate;
    createEnterprise(): IEnterprise;
    createRegistryEnterprise(): IRegistryEnterprise;
    createDocumentNote(): IDocumentNote;
    createBank(): IBank;
    createTaxModel(): ITaxModel;
    createMessage(): IMessage;
    createMessageChat(): IMessageChat;
    createEmployee(): IEmployee;
    createUser(): IUser;
}

export interface ICollectionFactory {
    createDocumentCollection(): ICollection<IDocument>;
    createFolderCollection(): ICollection<IFolder>;
    createEnterpriseCollection(): ICollection<IEnterprise>;
    createDocumentNoteCollection(): ICollection<IDocumentNote>;
    createBankCollection(): ICollection<IBank>;
    createTaxModelCollection(): ICollection<ITaxModel>;
    createMessageCollection(): ICollection<IMessage>;
    createMessageChatCollection(): ICollection<IMessageChat>;
    createEmployeeCollection(): ICollection<IEmployee>;
    createMarkCollection(): ICollection<IMark>;
    createUserCollection(): ICollection<IUser>;
    createContractCollection(): ICollection<IContract>;
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
    /**
     * API json object
     */
    ApiObject: any;
    
}

interface IApiModel {
    /**
     * Get the url needed for the api http request.
     * @param currentMethod The method is calling the api
     * @return And array with the method of http needed and the url
     */
    getUrl(currentMethod: string, filter?: IFilter): string[];
    /**
     * Get method and the url of http needed for the api http request. POST, GET...
     * @param currentMethod The method is calling the api
     * @return And array with the method of http needed and the url
     */
    getMethod(currentMethod:string, filter?: IFilter): string;
    /**
     * Parse de data to send to format api
     * @param data to send
     * @returns data parsed for api
     */
    parseDataToSend(data: any, currentMethod?:string, filter?: IFilter): any;
    /**
     * Parse de data received to sdk object
     * @param data received
     * @returns data parsed for sdk object
     */
    parseDataToReceive(data: any, currentMethod?:string, filter?: IFilter): any;
    /**
     * @returns true if filters are applied in local, false otherwhise
     */
    localFilter(currentMethod?: string, filter?: IFilter): boolean;
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

export interface IDocumentTag extends ICollectable {
    Name: string;
}

export interface IFolder extends ICollectable {
    Name: string;
    Path: string;
    Parent: string;
}

export interface ICertificate extends ICollectable {
  Name: string;
  RepresentationType: string;
  ExpeditionDate: Date;
  ExpirationDate: Date;
  Alias: string;
  Type: string;
  Tgss: boolean;
  Sepe: boolean;
  Aeat: boolean;
  DocumentUser: string;
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
    DomainName: string;
    DomainId: string;
    Registry: string;
}

export interface IRegistryEnterprise extends ICollectable {
  IdEnterprise: string,
  Description: string,
  DateCreation: Date,
  DateRegistration: Date,
  Notary: string,
  Protocol: string,
  Inscription: string
}

export interface IDocumentNote extends ICollectable {
    Text: string;
    Path: string;
}

export interface IBank extends ICollectable {
    Name: string;
    Total: number;
    Logo: string;
    SwiftBic: string;
    Iban: string;
    LastUpdate: Date;
    SyncStatus: string;
}

export type statusTaxModel = 'en proceso' | 'pendiente' | 'rectificado' | 'confirmado' | 'presentado';

export interface ITaxModel extends ICollectable {
    Name: string;
    TaxType: string;
    Status: statusTaxModel;
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
    LastMessageChatOrigin: boolean;
}

export interface ITaskHolder extends ICollectable {
    Id: string;
    Name: string;
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

export interface IContract extends ICollectable {
    Name: string;
    LastName: string;
    Type: string;
    GrossCost: number;
    StartDate: Date;
    EndDate?: Date;
    WorkCenter: string;
    Active: boolean;
}

// export interface IMark extends ICollectable {
//     Id: string;
//     Name: string,
//     Lastname: string,
//     IdEmployee: string, // Ver si este es necesario, o id del usuario
//     Date: Date,
//     EntryDate: Date,
//     ExitDate: Date,
//     Pause: IPause,
//     Location: string,
//     Ccc: string, // código cuenta de cotización
//     Workplace: string,
//     Status: string
// }

export interface IMark extends ICollectable {
    // Id: string, // ????
    // Lastname: string, 
    // Pause: IPause,
    // Ccc: string, // código cuenta de cotización
    // Workplace: string,
    Name: string, // nombre del usuario que marca
    IdUser: string, // id del usuario que marca
    Date: Date, // fecha del marcage
    EntryDate: Date, // hora de entrada del marcaje
    ExitDate: Date, // hora de salida del marcaje
    Time: Date; // duracion del marcaje total
    Location: any, // localizacion al marcar
    Status: string // entrada, salida, pausa
}

export interface IMarkDetail extends ICollectable {
    Id: string,
    LastDate: Date,
    LastModification: Date,
    Status: string,
    Location: any
}

export interface IPause {
    StartPause: Date,
    EndPause: Date
}

export interface IUser extends ICollectable {
    Name: string,
    Lastname: string,
    Document: string,
    Email: string,
    Password: string,
    Phone: string,
    Active: boolean,
    Enterprises: string [],
}

interface IAuth extends ICollectable{
    Email: string;
    Password: string;
}

export interface ICommunity {
  label: string;
  _about: string;
}

export interface ICountry {
  name:         Name;
  tld?:         string[];
  cca2:         string;
  ccn3?:        string;
  cca3:         string;
  cioc?:        string;
  independent?: boolean;
  status:       Status;
  unMember:     boolean;
  currencies?:  Currencies;
  idd:          Idd;
  capital?:     string[];
  altSpellings: string[];
  region:       Region;
  subregion?:   string;
  languages?:   { [key: string]: string };
  translations: { [key: string]: Translation };
  latlng:       number[];
  landlocked:   boolean;
  borders?:     string[];
  area:         number;
  demonyms?:    Demonyms;
  flag:         string;
  maps:         Maps;
  population:   number;
  gini?:        { [key: string]: number };
  fifa?:        string;
  car:          Car;
  timezones:    string[];
  continents:   Continent[];
  flags:        Flags;
  coatOfArms:   CoatOfArms;
  startOfWeek:  StartOfWeek;
  capitalInfo:  CapitalInfo;
  postalCode?:  PostalCode;
}

export interface CapitalInfo {
  latlng?: number[];
}

export interface Car {
  signs?: string[];
  side:   Side;
}

export enum Side {
  Left = "left",
  Right = "right",
}

export interface CoatOfArms {
  png?: string;
  svg?: string;
}

export enum Continent {
  Africa = "Africa",
  Antarctica = "Antarctica",
  Asia = "Asia",
  Europe = "Europe",
  NorthAmerica = "North America",
  Oceania = "Oceania",
  SouthAmerica = "South America",
}

export interface Currencies {
  ZAR?: Aed;
  NOK?: Aed;
  WST?: Aed;
  GMD?: Aed;
  XCD?: Aed;
  EUR?: Aed;
  AWG?: Aed;
  XOF?: Aed;
  KPW?: Aed;
  PYG?: Aed;
  BMD?: Aed;
  XAF?: Aed;
  USD?: Aed;
  GBP?: Aed;
  MZN?: Aed;
  SOS?: Aed;
  SGD?: Aed;
  NIO?: Aed;
  AUD?: Aed;
  PEN?: Aed;
  MXN?: Aed;
  BAM?: BAM;
  BHD?: Aed;
  MOP?: Aed;
  BBD?: Aed;
  UZS?: Aed;
  CNY?: Aed;
  MWK?: Aed;
  ZWL?: Aed;
  KES?: Aed;
  PKR?: Aed;
  FJD?: Aed;
  SZL?: Aed;
  JEP?: Aed;
  TWD?: Aed;
  LKR?: Aed;
  BYN?: Aed;
  AED?: Aed;
  ANG?: Aed;
  CRC?: Aed;
  AOA?: Aed;
  UYU?: Aed;
  CDF?: Aed;
  KWD?: Aed;
  TRY?: Aed;
  MRU?: Aed;
  TVD?: Aed;
  PAB?: Aed;
  EGP?: Aed;
  AZN?: Aed;
  RWF?: Aed;
  INR?: Aed;
  ISK?: Aed;
  SRD?: Aed;
  BGN?: Aed;
  SLL?: Aed;
  TND?: Aed;
  CUC?: Aed;
  CUP?: Aed;
  TTD?: Aed;
  KMF?: Aed;
  SHP?: Aed;
  RON?: Aed;
  NPR?: Aed;
  SAR?: Aed;
  DOP?: Aed;
  DKK?: Aed;
  FOK?: Aed;
  KID?: Aed;
  VUV?: Aed;
  HUF?: Aed;
  YER?: Aed;
  SCR?: Aed;
  LYD?: Aed;
  ILS?: Aed;
  VND?: Aed;
  IRR?: Aed;
  NAD?: Aed;
  LBP?: Aed;
  MYR?: Aed;
  MNT?: Aed;
  GEL?: Aed;
  TJS?: Aed;
  ALL?: Aed;
  TMT?: Aed;
  COP?: Aed;
  VES?: Aed;
  GNF?: Aed;
  SSP?: Aed;
  UAH?: Aed;
  FKP?: Aed;
  HNL?: Aed;
  BRL?: Aed;
  MUR?: Aed;
  THB?: Aed;
  BOB?: Aed;
  SEK?: Aed;
  GGP?: Aed;
  ZMW?: Aed;
  ERN?: Aed;
  KZT?: Aed;
  MAD?: Aed;
  JOD?: Aed;
  MMK?: Aed;
  CZK?: Aed;
  JMD?: Aed;
  KGS?: Aed;
  SDG?: BAM;
  STN?: Aed;
  GIP?: Aed;
  LSL?: Aed;
  PLN?: Aed;
  JPY?: Aed;
  LRD?: Aed;
  CVE?: Aed;
  IMP?: Aed;
  BIF?: Aed;
  PGK?: Aed;
  UGX?: Aed;
  AFN?: Aed;
  XPF?: Aed;
  BWP?: Aed;
  LAK?: Aed;
  GTQ?: Aed;
  CHF?: Aed;
  SBD?: Aed;
  SYP?: Aed;
  BDT?: Aed;
  DJF?: Aed;
  GHS?: Aed;
  OMR?: Aed;
  BSD?: Aed;
  DZD?: Aed;
  HTG?: Aed;
  PHP?: Aed;
  CKD?: Aed;
  NZD?: Aed;
  TOP?: Aed;
  MGA?: Aed;
  CAD?: Aed;
  AMD?: Aed;
  NGN?: Aed;
  BZD?: Aed;
  RUB?: Aed;
  KYD?: Aed;
  MDL?: Aed;
  RSD?: Aed;
  CLP?: Aed;
  IDR?: Aed;
  MVR?: Aed;
  BND?: Aed;
  GYD?: Aed;
  TZS?: Aed;
  KHR?: Aed;
  QAR?: Aed;
  ARS?: Aed;
  IQD?: Aed;
  BTN?: Aed;
  KRW?: Aed;
  HKD?: Aed;
  MKD?: Aed;
  ETB?: Aed;
}

export interface Aed {
  name:   string;
  symbol: string;
}

export interface BAM {
  name: string;
}

export interface Demonyms {
  eng:  Eng;
  fra?: Eng;
}

export interface Eng {
  f: string;
  m: string;
}

export interface Flags {
  png:  string;
  svg:  string;
  alt?: string;
}

export interface Idd {
  root?:     string;
  suffixes?: string[];
}

export interface Maps {
  googleMaps:     string;
  openStreetMaps: string;
}

export interface Name {
  common:      string;
  official:    string;
  nativeName?: { [key: string]: Translation };
}

export interface Translation {
  official: string;
  common:   string;
}

export interface PostalCode {
  format: string;
  regex?: string;
}

export enum Region {
  Africa = "Africa",
  Americas = "Americas",
  Antarctic = "Antarctic",
  Asia = "Asia",
  Europe = "Europe",
  Oceania = "Oceania",
}

export enum StartOfWeek {
  Monday = "monday",
  Saturday = "saturday",
  Sunday = "sunday",
}

export enum Status {
  OfficiallyAssigned = "officially-assigned",
  UserAssigned = "user-assigned",
}

interface ISimpleValue {
    Value: string;
}

export enum InvoiceType { GASTO = 'gasto', VENTA = 'venta' }

export enum InvoiceStatus { A_CONTABILIZAR = 'a_contabilizar', CONTABILIZADO = 'contabilizado' }

export interface IInvoice extends ICollectable{
    Serie: IInvoiceSerie;
    InvoiceNumber: string;
    Date: Date;
    TotalAmount: number;
    Contact: IContact;
    Category: IInvoiceCategory;
    Lines: ICollection<IInvoiceLine>;
    TransactionType: IInvoiceTransactionType;
    Activity: IInvoiceActivity;
    CriCaja: boolean;
    RE: boolean;
    RegAgri: boolean;
    Tax: ITax;
    TaxBase: number;
    TaxQuota: number;
    IRPF: IIRPF;
    IRPFBase: number;
    IRPFQuota: number;
    InvoiceExpirationLines: ICollection<IInvoiceExpirationLine>;
    Rectified: boolean;
    Type: InvoiceType;
    Status: InvoiceStatus;
}

export interface IInvoiceLine extends ICollectable{
    Product: IProduct;
    Quantity: number;
    Price: number;
    Discount: number;
    TotalPrice: number;
    Tax: ITax;
    IRPF: IIRPF;
}

export interface IInvoiceExpirationLine extends ICollectable{
    Date: Date;
    PaymentMethod: IPaymentMethod;
    BankAccount: string;
    Amount: number;
}

export interface IInvoiceSerie extends ICollectable, ISimpleValue {}

export interface IInvoiceTransactionType extends ICollectable, ISimpleValue{}

export interface IInvoiceActivity extends ICollectable, ISimpleValue{}

export interface IIRPF extends ICollectable, ISimpleValue {
    Type: string;
}

export interface ITax extends ICollectable, ISimpleValue {}

export interface IPaymentMethod extends ICollectable, ISimpleValue {}

export interface IInvoiceCategory extends ICollectable, ISimpleValue {}

export interface IProduct extends ICollectable{
    Code: IProductCode;
    Name: string;
    Category: IProductCategory;
    Class: IProductClass;
    Type: IProductType;
    Status: IProductStatus;
    Tax: ITax;
    IRPF: IIRPF;
    BarCode: string;
    Description: string;
    CostPrice: number;
    Benefit: number;
    Price: number;
    Pvp: number;
}

export interface IProductCode extends ICollectable, ISimpleValue {}

export interface IProductCategory extends ICollectable, ISimpleValue {}

export interface IProductClass extends ICollectable, ISimpleValue {}

export interface IProductType extends ICollectable, ISimpleValue {}

export interface IProductStatus extends ICollectable, ISimpleValue {}

export interface IContact extends ICollectable{
    Name: string;
    ComercialName: string;
    Country: string;
    /**
     * Can be DNI, CIF, etc
     */
    Document: string;
    Address: string;
    Email: string;
    Phone: string;
    Web: string;
    BankAccount: string;
    PaymentMethod: IPaymentMethod;
    TransactionType: IInvoiceTransactionType;
    IRPF: boolean;
    RE: boolean;
}

/*
 *
 * IMPLEMENTATION OF INTERFACES FOR CONCRETE CLASSES
 *
 */

export class Factory implements IFactory {
    createInvoiceLine(product?: IProduct,quantity?: number,price?: number,discount?: number,totalPrice?: number,tax?: ITax,irpf?: IIRPF): IInvoiceLine {
        return new InvoiceLine(product, quantity, price, discount, totalPrice, tax, irpf);
    }

    createInvoiceExpirationLine(date?: Date, paymentMethod?: IPaymentMethod, bankAccount?: string, amount?: number): IInvoiceExpirationLine {
        return new InvoiceExpirationLine(date, paymentMethod, bankAccount, amount);
    }

    createInvoice(serie?: IInvoiceSerie, invoiceNumber?: string, date?: Date, totalAmount?: number, contact?: IContact, category?: IInvoiceCategory, 
        lines?: ICollection<IInvoiceLine>, transactionType?: IInvoiceTransactionType, activity?: IInvoiceActivity, criCaja?: boolean, re?: boolean, 
        regAgri?: boolean, tax?: ITax, taxBase?: number, taxQuota?: number, irpf?: IIRPF, irpfbase?: number, irpfquota?: number, 
        type?: InvoiceType, rectified?: boolean): IInvoice {
        return new Invoice(serie, invoiceNumber, date, totalAmount, contact, category, lines, transactionType, activity, criCaja, re, regAgri, tax, taxBase, taxQuota, irpf, irpfbase, irpfquota, type, rectified);
    }

    createProduct(code?: IProductCode, name?: string, category?: IProductCategory, productClass?: IProductClass, type?: IProductType, status?: IProductStatus, tax?: ITax, irpf?: IIRPF, barCode?: string, description?: string, costPrice?: number, benefit?: number, price?: number, pvp?: number): IProduct {
        return new Product(code, name, category, productClass, type, status, tax, irpf, barCode, description, costPrice, benefit, price, pvp);
    }

    createContact(name?: string, comercialName?: string, country?: string, document?: string, address?: string, email?: string, phone?: string, web?: string, bankAccount?: string, paymentMethod?: IPaymentMethod, transactionType?: IInvoiceTransactionType, irpf?: boolean, re?: boolean): IContact {
        return new Contact(name, comercialName, country, document, address, email, phone, web, bankAccount, paymentMethod, transactionType, irpf, re);
    }

    createDocument(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string): IDocument {
        return new Document(file, fileName, fileSize, fileType, date, path);
    }

    createFolder(name?: string, parent?: string): IFolder {
        return new Folder(name, parent);
    }

    createCertificate(name?: string, representationType?: string, expeditionDate?: Date, expirationDate?: Date, alias?: string, type?: string, tgss?: boolean, sepe?: boolean, aeat?: boolean, documentUser?: string): ICertificate {
        return new Certificate(name, representationType, expeditionDate, expirationDate, alias, type, tgss, sepe, aeat);
    }

    createEnterprise(name?: string, document?: string): IEnterprise {
        return new Enterprise(name,document);
    }

    createRegistryEnterprise(name?: string, description?: string, dateCreation?: Date, dateRegistration?: Date, notary?: string, protocol?: string, inscription?: string): IRegistryEnterprise {
      return new RegistryEnterprise(name, description, dateCreation, dateRegistration, notary, protocol, inscription);
    }

    createDocumentNote(text?: string, path?: string): IDocumentNote {
        return new DocumentNote(text, path);
    }

    createBank(name?: string, total?: number, logo?: string): IBank {
        return new Bank(name, total, logo);
    }

    createTaxModel(name?: string, taxType?: string, status?: statusTaxModel, paymentMethod?: string, result?: string, trimester?: number, year?: number): ITaxModel {
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
    createInvoiceCollection(): ICollection<IInvoice> {
      return new Collection<Invoice>();
    }

    createProductCollection(): ICollection<IProduct> {
      return new Collection<Product>();
    }

    createContactCollection(): ICollection<IContact> {
      return new Collection<Contact>();
    }

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

    createRegistryEnterpriseCollection(): ICollection<IRegistryEnterprise> {
      return new Collection<RegistryEnterprise>();
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

    createMarkCollection(): ICollection<IMark> {
        return new Collection<Mark>();
    }

    createUserCollection(): ICollection<IUser> {
        return new Collection<User>();
    }

    createContractCollection(): ICollection<IContract> {
      return new Collection<Contract>();
    }
}

class SimpleValue implements IModel{
    protected key: string;
    protected value: string;
    protected apiObject: any;

    constructor(value?: string) {
        this.key = KeyGenerator.generate(15);
        this.value = value || '';
        this.apiObject = {};
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Value() {
        return this.value;
    }

    public set Value(value: string) {
        this.value = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        return map;
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
    private id: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(file?: string, fileName?: string, fileSize?: number, fileType?: string, date?: Date, path?: string, id?: string) {
        this.file = file || '';
        this.fileName = fileName || '';
        this.fileSize = fileSize || 0;
        this.fileType = fileType || '';
        this.path =  path || '';
        this.date = date || new Date();
        this.key = path && fileName ? path + '/' + fileName : '';
        this.id = id || '';
        this.apiObject = '';
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

    public get Id(): string {
        return this.id;
    }

    public set Id(id: string) {
        this.id = id;
    } 

    getKey(): string {
        return this.path + '/' + this.id;
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

class ApiDocument extends Document implements IApiModel {

    getUrl(currentMethod: string, filter: IFilter): string[] {
        // a contabilizar, contabilizado, papelera - inbox, rejected, draft
        if(currentMethod == GET_MULTIPLE){
            if(filter.fields?.has('path') && filter.fields.get('path').toLowerCase() == '/a_contabilizar')
                return ['/ms/api/invoice?status=inbox'];
            if(filter.fields?.has('path') && filter.fields.get('path').toLowerCase() == '/contabilizado')
                throw new ErrorResponse('0199')
                // return ['/ms/api/invoice?status=rejected'];
            if(filter.fields?.has('path') && filter.fields.get('path').toLowerCase() == '/fiscal')
                return ['/ms/api/fiscal/models'];
            if(filter.fields?.has('path') && filter.fields.get('path').toLowerCase() == '/papelera')
                throw new ErrorResponse('0199')
                // return ['/ms/api/invoice?status=draft'];
            if(filter.fields?.has('path') && filter.fields.get('path').toLowerCase().includes('/laboral'))
                return ['/ms/api/contract/enterprise/salaries?document=' + filter.fields.get('path').split('/')[2]]
        }
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any, currentMethod:string, filter: IFilter) {
        let document = new Document()
        if(filter && filter.fields && filter.fields?.has('path') && filter.fields?.get('path').includes('/laboral')){
            document.ApiObject = data;
            document.File = ''
            document.FileName = 'Nómina' + (data.startDate ? data.startDate : '') + ' - ' + (data.endDate ? data.endDate : '');
            document.FileSize = 0
            document.FileType = ''
            document.Date = new Date()
            document.Path = filter.fields?.get('path')
            document.Key = data.id ? data.id : ''
            document.Id = data.id ? data.id : ''
        }else {
            document.ApiObject = data;
            document.File = data.file && data.file.path ? data.file.path : '';
            document.FileName = data.name ? data.name : '';
            document.FileSize = 0;
            document.FileType = data.file && data.file.content_type ? data.file.content_type : '';
            document.Date = data.date ? data.date : new Date();
            document.Path = filter.fields?.get('path')
            document.Key = data.id ? data.id : '';
            document.Id = data.id ? data.id : '';
        }
        return document;
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
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

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

class ApiFolder extends Folder implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string[] {
        let urls: string [] = [];
        if(currentMethod == GET_MULTIPLE){
            if(filter && filter.fields && filter.fields?.has('workplace')){
                filter.fields?.get('workplace').split(';').forEach((element: any) => {
                    if(element) urls.push('/ms/api/contract/employee/workplace?workplace=' + element)
                })
                return urls;
            }
            else
                return ['/ms/api/contract/employee/workplace'];
        }
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return true;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let folder = new Folder();
        folder.ApiObject = data;
        folder.Key = data.document ? data.document : '';
        folder.Name = data.name && data.surname ? data.name + data.surname : '';
        folder.Parent = '/laboral';
        folder.Path = '/laboral/' + folder.Key;
        return folder;
        throw new ErrorResponse('0199')
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
    private expeditionDate: Date;
    private expirationDate: Date;
    private alias: string;
    private type: string;
    private tgss: boolean;
    private sepe: boolean;
    private aeat: boolean;
    private documentUser: string;
    private key: string;
    private isValid: boolean;
    protected apiObject: any;

    constructor(name?: string, representationType?: string, expeditionDate?: Date, expirationDate?: Date, alias?: string, type?: string, tgss?: boolean, sepe?: boolean, aeat?: boolean, documentUser?: string, isValid?: boolean) {
        this.name = name || '';
        this.representationType = representationType || '';
        this.expeditionDate = expeditionDate || new Date();
        this.expirationDate = expirationDate || new Date();
        this.alias = alias || '';
        this.type = type || '';
        this.tgss = tgss || false;
        this.sepe = sepe || false;
        this.aeat = aeat || false;
        this.documentUser = documentUser || '';
        this.isValid = isValid || false;
        this.key = documentUser || '';
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get IsValid(): boolean{
        return this.isValid;
    }

    public set IsValid(value: boolean){
        this.isValid = value;
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

    public get ExpeditionDate(): Date {
        return this.expeditionDate;
    }

    public set ExpeditionDate(value: Date) {
        this.expeditionDate = value;
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

    public get DocumentUser(): string {
        return this.documentUser;
    }

    public set DocumentUser(value: string) {
        this.documentUser = value;
    }

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    getKey(): string {
        return this.documentUser;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('representationType', this.RepresentationType);
        map.set('expeditionDate', this.ExpeditionDate);
        map.set('expirationDate', this.ExpirationDate);
        map.set('alias', this.Alias);
        map.set('type', this.Type);
        map.set('tgss', this.Tgss);
        map.set('sepe', this.Sepe);
        map.set('aeat', this.Aeat);
        map.set('documentUser', this.DocumentUser);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('representationType', this.RepresentationType);
        map.set('expeditionDate', this.ExpeditionDate);
        map.set('expirationDate', this.ExpirationDate);
        map.set('alias', this.Alias);
        map.set('type', this.Type);
        map.set('tgss', this.Tgss);
        map.set('sepe', this.Sepe);
        map.set('aeat', this.Aeat);
        map.set('documentUser', this.DocumentUser);
        return map;
    }

}

class ApiCertificate extends Certificate implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
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
    private domainName: string;
    private domainId: string;
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
    private registry: string;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Registry(): string {
        return this.registry;
    }

    public set Registry(value: string) {
        this.registry = value;
    }

    public get DomainName(): string{
        return this.domainName;
    }

    public set DomainName(value: string){
        this.domainName = value;
    }

    public get DomainId(): string {
        return this.domainId;
    }

    public set DomainId(value: string) {
        this.domainId = value;
    }

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

    public set Key(key: string) {
        this.key = key;
    }

    constructor(name?: string, document?: string, profilePhoto?: string, address?: string, country?: string, province?: string, socialReason?: string, email?: string, phone?: string, website?: string, registry?: string, key?: string) {
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
        this.domainId = '';
        this.domainName = '';
        this.registry = '';
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

class ApiEnterprise extends Enterprise implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string [] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/company'];
        if(currentMethod == GET_SINGLE)
            return ['/ms/api/company/one?id=' + filter.fields?.get('id')];
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        if(currentMethod == GET_SINGLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any): any {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any, currentMethod: string): any {
        if(currentMethod == GET_MULTIPLE){
            let enterprise = new Enterprise();
            enterprise.ApiObject = data;
            enterprise.Document = data.document ? data.document : '';
            enterprise.Name = data.name ? data.name : ''
            enterprise.Key = data.document ? data.document : '';
            enterprise.DomainName = data.domain ? data.domain : '';
            enterprise.DomainId = data.id ? data.id : '';
            enterprise.Registry = data.registry ? data.registry : '';
            return enterprise;
        }else if (currentMethod == GET_SINGLE){
            let enterprise = new Enterprise();
            enterprise.ApiObject = data;
            enterprise.Address = data.address.address
            enterprise.Country = data.address.country
            enterprise.Document = data.document
            enterprise.DomainId = data.domain.id
            enterprise.DomainName = data.domain.name
            enterprise.Email = '' // TO DO
            enterprise.Key = data.document
            enterprise.Name = data.name
            enterprise.Phone = '' // TO DO
            enterprise.ProfilePhoto = 
            enterprise.Province = data.address.province
            enterprise.Registry = data.id
            enterprise.SocialReason = '' // TO DO
            enterprise.Website = '' // TO DO
            return enterprise;
        }
        throw new ErrorResponse('0199')
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

class RegistryEnterprise implements IRegistryEnterprise, IModel {
  private id: string;
  private idEnterprise: string;
  private description: string;
  private dateCreation: Date;
  private dateRegistration: Date;
  private notary: string;
  private protocol: string;
  private inscription: string;
  private key: string;
  protected apiObject: any;

  constructor(idEnterprise?: string, description?: string, dateCreation?: Date, dateRegistration?: Date, notary?: string, protocol?: string, inscription?: string, key?: string) {
    this.id = KeyGenerator.generate(15);
    this.idEnterprise = idEnterprise || '';
    this.description = description || '';
    this.dateCreation = dateCreation || new Date();
    this.dateRegistration = dateRegistration || new Date();
    this.notary = notary || '';
    this.protocol = protocol || '';
    this.inscription = inscription || '';
    this.key = this.idEnterprise;
  }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

  public get Id(): string {
    return this.id;
  }

  public set Id(value: string) {
    this.id = value;
  }

  public get IdEnterprise(): string {
    return this.idEnterprise;
  }

  public set IdEnterprise(value: string) {
    this.idEnterprise = value;
  }

  public get Description(): string {
    return this.description;
  }

  public set Description(value: string) {
    this.description = value;
  }

  public get DateCreation(): Date {
    return this.dateCreation;
  }

  public set DateCreation(value: Date) {
    this.dateCreation = value;
  }

  public get DateRegistration(): Date {
    return this.dateRegistration;
  }

  public set DateRegistration(value: Date) {
    this.dateRegistration = value;
  }

  public get Notary(): string {
    return this.notary;
  }

  public set Notary(value: string) {
    this.notary = value;
  }

  public get Protocol(): string {
    return this.protocol;
  }

  public set Protocol(value: string) {
    this.protocol = value;
  }

  public get Inscription(): string {
    return this.inscription;
  }

  public set Inscription(value: string) {
    this.inscription = value;
  }

  public get Key(): string {
    return this.key;
  }

  public set Key(value: string) {
    this.key = value;
  }

  getKey(): string {
    return this.idEnterprise;
  }

  getFilterableFields(): Map<string, any> {
    let map = new Map<string, any>();
    map.set('id', this.Id);
    map.set('idEnterprise', this.IdEnterprise);
    map.set('description', this.Description);
    map.set('dateCreation', this.DateCreation);
    map.set('dateRegistration', this.DateRegistration);
    map.set('notary', this.Notary);
    map.set('protocol', this.Protocol);
    map.set('inscription', this.Inscription);
    return map;
  }

  getSortableFields(): Map<string, any> {
    let map = new Map<string, any>();
    map.set('id', this.Id);
    map.set('idEnterprise', this.IdEnterprise);
    map.set('description', this.Description);
    map.set('dateCreation', this.DateCreation);
    map.set('dateRegistration', this.DateRegistration);
    map.set('notary', this.Notary);
    map.set('protocol', this.Protocol);
    map.set('inscription', this.Inscription);
    return map;
  }

}

class StorableRegistryEnterprise extends RegistryEnterprise implements IStorable<RegistryEnterprise> {
  getCollection(): ICollection<RegistryEnterprise> {
    return registryEnterprises;
  }
  getLocalStorage(): string {
      return 'registryEnterprises';
  }
}


class DocumentNote implements IDocumentNote, IModel  {
    private text: string;
    private path: string;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
    }

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

class ApiDocumentNote extends DocumentNote implements IApiModel {
    getUrl(currentMethod: string): string[] {
        throw new ErrorResponse('0199')
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new ErrorResponse('0199')
    }
    localFilter(): boolean {
        return false;
    }
    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }
    parseDataToReceive(data: any) {
        throw new ErrorResponse('0199')
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
    private swiftBic: string;
    private iban: string;
    private lastUpdate: Date;
    private syncStatus: string;
    private name: string;
    private key: string;
    private total: number;
    private logo: string;
    protected apiObject: any;
    
    constructor(name?: string, total?: number, logo?: string, swift?: string, iban?: string, lastUpdate?: Date, syncStatus?: string) {
        this.name = name || '';
        this.total = total || 0;
        this.logo = logo || '';
        this.key = KeyGenerator.generate(15);
        this.swiftBic = swift || '';
        this.iban = iban || '';
        this.lastUpdate = lastUpdate || new Date();
        this.syncStatus = syncStatus || '';
    }

    public get Iban(): string {
        return this.iban;
    }

    public set Iban(value: string) {
        this.iban = value;
    }

    public get LastUpdate(): Date {
        return this.lastUpdate;
    }

    public set LastUpdate(value: Date) {
        this.lastUpdate = value;
    }

    public get SyncStatus(): string {
        return this.syncStatus;
    }

    public set SyncStatus(value: string) {
        this.syncStatus = value;
    }
    
    public get SwiftBic(): string {
        return this.swiftBic;
    }

    public set SwiftBic(value: string) {
        this.swiftBic = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
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
        return this.key;
    }

    getFilterableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('total', this.Total);
        map.set('swift', this.SwiftBic);
        map.set('iban', this.Iban);
        map.set('lastUpdate', this.LastUpdate);
        map.set('syncStatus', this.SyncStatus);
        return map;
    }

    getSortableFields(): Map<string,any> {
        let map = new Map<string, any>();
        map.set('name', this.Name);
        map.set('total', this.Total);
        map.set('swift', this.SwiftBic);
        map.set('iban', this.Iban);
        map.set('lastUpdate', this.LastUpdate);
        map.set('syncStatus', this.SyncStatus);
        return map;
    }
}

class ApiBank extends Bank implements IApiModel {
    getUrl(currentMethod: string): string[] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/company/banks'];
            throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
            throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let bank = new Bank();
        bank.ApiObject = data;
        bank.Key = data.id
        bank.Logo = ''
        bank.Name = data.alias ? data.alias : ''
        bank.Total = 0
        return bank;
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
    private status: statusTaxModel;
    private paymentMethod: string;
    private result: string;
    private trimester: number;
    private year: number;
    private key: string;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, taxType?: string, status?: statusTaxModel, paymentMethod?: string, result?: string, trimester?: number, year?: number) {
        this.name = name || '';
        this.taxType = taxType || '';
        this.status = status || 'pendiente';
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

    public get Status(): statusTaxModel {
        return this.status;
    }

    public set Status(value: statusTaxModel) {
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
        return this.key;
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

class ApiTaxModel extends TaxModel implements IApiModel {
    getUrl(currentMethod: string): string[] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/fiscal/models']
            throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
            throw new ErrorResponse('0199')
    }

    localFilter(currentMethod: string, filter?: IFilter): boolean {
        return true;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let tax = new TaxModel();
        tax.ApiObject = data;
        tax.Key = data.id;
        tax.Name = data.model ? data.model : '';
        tax.PaymentMethod = '';
        tax.Result = data.result ? data.result : '';
        tax.Status = data.status ? data.status : '';
        tax.TaxType = '';
        tax.Trimester = data.period ? data.period : '';
        tax.Year = data.year ? data.year : '';
        return tax;
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
    private lastMessageChatOrigin: boolean;
    private key: string;
    private taskHolder : TaskHolder;
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, title?: string, description?: string, date?: Date, type?: string, status?: string, endDate?: Date, lastMessageChatOrigin?: boolean) {
        this.id = KeyGenerator.generate(15);
        this.name = name || '';
        this.title = title || '';
        this.description = description || '';
        this.date = date || new Date();
        this.type = type || '';
        this.status = status || '';
        this.endDate = endDate || new Date();
        this.key = this.id || '';
        this.taskHolder = new TaskHolder();
        this.lastMessageChatOrigin = false;
    }

    public get LastMessageChatOrigin() {
        return this.lastMessageChatOrigin;
    }

    public set LastMessageChatOrigin(value: boolean) {
        this.lastMessageChatOrigin = value;
    }

    public get Id(): string {
        return this.id;
    }

    public set Id(value: string) {
        this.id = value;
    }

    public get TaskHolder(): TaskHolder {
        return this.taskHolder;
    }

    public set TaskHolder(value: TaskHolder) {
        this.taskHolder = value;
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

class ApiMessage extends Message implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string[] {
        if(currentMethod == GET_MULTIPLE){
            if(filter && filter.fields?.has('type') && filter.fields.get('type').toLowerCase() == 'notificacion')
                return ['/ms/api/notification?page=1&perPage=100']
            else if(filter && filter.fields?.has('type') && filter.fields.get('type').toLowerCase() == 'consulta')
                return ['/ms/api/task?source=query&page=1&perPage=100&task_holder=' + localStorage.getItem('registry') + '&sender=' + localStorage.getItem('registry')]
            else if(filter && filter.fields?.has('type') && filter.fields.get('type').toLowerCase() == 'tarea')
                return ['/ms/api/task?source=task&page=1&perPage=100&task_holder=' + localStorage.getItem('registry') + '&sender=' + localStorage.getItem('registry')]
            else
                return ['/ms/api/notification?page=1&perPage=100','/ms/api/task?source=query&page=1&perPage=100','/ms/api/task?source=task&page=1&perPage=100']
        }else if (currentMethod == GET_SINGLE){
            if(filter && filter.fields?.has('id') &&  
            (filter.fields.get('id').toLowerCase().split(';')[1] == 'consulta' || filter.fields.get('id').toLowerCase().split(';')[1] == 'tarea'))
                return ['/ms/api/task/one?id=' + filter.fields.get('id').split(';')[0]];
        }else if(currentMethod == UPDATE_SINGLE){
            return ['/ms/api/task']
        }
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        if(currentMethod == GET_SINGLE)
            return GET_METHOD;
        if(currentMethod == UPDATE_SINGLE)
            return POST_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return true;
    }

    parseDataToSend(data: Message, currentMethod:string) {
        if(currentMethod == UPDATE_SINGLE){
            let object = data.ApiObject;
            object.status = data.Status;
            return object;
        }
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let message = new Message();
        if(data.source && (data.source == 'task' || data.source == 'query')){
            let description = JSON.parse(data.description);
            message.ApiObject = data;
            message.Id = data.id + (data.source == 'query' ? ';consulta' : ';tarea');
            message.Name = data.sender.name  ? data.sender.name : '';
            message.Title = data.title ? data.title : '';
            message.Description = description.observation ? description.observation : '';
            message.Date = new Date(data.start_date);
            message.Type = data.source == 'query' ? 'consulta' : 'tarea';
            if(data.source == 'task')
                message.Status = data.status && data.status == 'pending' ? 'pendiente' : 'realizada';
            else
                message.Status = data.status && data.status == 'pending' ? 'abierta' : 'cerrada';
            message.EndDate = new Date();
            message.Key = data.id + (data.source == 'query' ? ';consulta' : ';tarea');
            return message;
        }else {
            message.ApiObject = data;
            message.Id = data.id + ';notificacion';
            message.Name = data.source ? data.source : '';
            message.Title = data.title ? data.title : '';
            message.Description = data.body ? data.body : '';
            message.Date = new Date(data.date);
            message.Type = 'notificacion';
            message.Status = data.status && data.status == 1 ? 'vista' : 'nueva';
            message.EndDate = new Date();
            message.Key = data.id + ';notificacion';
            return message;
        }
        throw new ErrorResponse('0199')
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

class TaskHolder implements ITaskHolder, IModel {
    protected id: string;
    protected name: string;
    protected key: string;
    protected apiObject: any;

    constructor(name?: string) {
        this.name = name || '';
        this.id = KeyGenerator.generate(15);
        this.key = this.id;
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

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    getKey(): string {
        return this.id;
    }
    getFilterableFields(): Map<string, any> {
        throw new Error("Method not implemented.");
    }
    getSortableFields(): Map<string, any> {
        throw new Error("Method not implemented.");
    }
    
}

class ApiTaskHolder extends TaskHolder implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        if(currentMethod == GET_MULTIPLE)
            return ['/ms/api/taskholder/list'];
        if(currentMethod == GET_SINGLE)
            return ['/ms/api/taskholder?id=' + filter?.fields?.get('id')];
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE || currentMethod == GET_SINGLE)
            return GET_METHOD;
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod: string, filter?: IFilter | undefined) {
        let taskHolder = new TaskHolder(); 
        taskHolder.ApiObject = data;
        taskHolder.Id = data.id;
        taskHolder.Key = data.id;
        taskHolder.Name = data.name;
        return taskHolder;
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return true;
    }
    
}

class StorableTaskHolder extends TaskHolder implements IStorable<TaskHolder> {
    getCollection(): ICollection<TaskHolder> {
        throw new Error("Method not implemented.");
        // return taskHolders;
    }
    getLocalStorage(): string {
        throw new Error("Method not implemented.");
        return 'taskHolders';
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
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(idMessage?: string, name?: string, description?: string, date?: Date, type?: string) {
        this.id = KeyGenerator.generate(15);
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
        map.set('idmessage', this.idMessage);
        map.set('name', this.name);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('idmessage', this.idMessage);
        map.set('name', this.name);
        map.set('description', this.description);
        map.set('date', this.date);
        map.set('type', this.type);
        return map;
    }
}

class ApiMessageChat extends MessageChat implements IApiModel {
    getUrl(currentMethod: string, filter: IFilter): string[] {
        if(currentMethod == GET_MULTIPLE && filter && filter.fields && filter.fields.has('idMessage'))
            return ['/ms/api/task/workflow?task='+filter.fields.get('idMessage')+'&domainId='+localStorage.getItem('domainId')+'&domainName='+localStorage.getItem('domainName')]
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE)
            return GET_METHOD;
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        let messageChat = new MessageChat();
        messageChat.ApiObject = data;
        messageChat.Id = data.id
        messageChat.IdMessage = data.task
        messageChat.Key = data.id
        messageChat.Name = data.task_holder.name
        messageChat.Type = data.task_holder.id == localStorage.getItem('registry') ? 'send' : 'received';
        messageChat.Description = data.comment
        messageChat.Date = data.modification_date;
        return messageChat;
        throw new ErrorResponse('0199')
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
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
    }

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

class ApiEmployee extends Employee implements IApiModel {
    getUrl(currentMethod: string): string[] {
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        throw new ErrorResponse('0199')
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

class Contract implements IContract, IModel  {
    private name: string;
    private lastName: string;
    private type: string;
    private grossCost: number;
    private startDate: Date;
    private endDate?: Date;
    private workCenter: string;
    private active: boolean;
    private key: string;
    protected apiObject: any;

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    constructor(name?: string, lastName?: string, type?: string, grossCost?: number, startDate?: Date, endDate?: Date, workCenter?: string, active?: boolean) {
        this.key = name || '';
        this.name = name || '';
        this.lastName = lastName || '';
        this.type = type || '';
        this.grossCost = grossCost || 0;
        this.startDate = startDate || new Date();
        this.endDate = endDate || new Date();
        this.workCenter = workCenter || '';
        this.active = active || false;
    }
    

    public get Name(): string {
      return this.name;
    }

    public set Name(value: string) {
      this.name = value;
    }

    public get LastName(): string {
      return this.lastName;
    }

    public set LastName(value: string) {
      this.lastName = value;
    }

    public get Type(): string {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    public get GrossCost(): number {
        return this.grossCost;
    }

    public set GrossCost(value: number) {
        this.grossCost = value;
    }

    public get StartDate(): Date {
        return this.startDate;
    }

    public set StartDate(value: Date) {
        this.startDate = value;
    }

    public get EndDate(): any {
        return this.endDate;
    }

    public set EndDate(value: any) {
        this.endDate = value;
    }

    public get WorkCenter(): string {
        return this.workCenter;
    }

    public set WorkCenter(value: string) {
        this.workCenter = value;
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
        map.set('lastName', this.lastName);
        map.set('type', this.type);
        map.set('grossCost', this.grossCost);
        map.set('startDate', this.startDate);
        map.set('endDate', this.endDate);
        map.set('workCenter', this.workCenter);
        map.set('active', this.active);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('lastName', this.lastName);
        map.set('type', this.type);
        map.set('grossCost', this.grossCost);
        map.set('startDate', this.startDate);
        map.set('endDate', this.endDate);
        map.set('workCenter', this.workCenter);
        map.set('active', this.active);
        return map;
    }

}

class StorableContract extends Contract implements IStorable<Contract> {
    getCollection(): ICollection<Contract> {
        return contracts;
    }
    getLocalStorage(): string {
        return 'contracts';
    }
}

class Mark implements IMark, IModel  {
    private key: string;
    private apiObject: any;
    private name: string;
    private idUser: string;
    private date: Date;
    private entryDate: Date;
    private exitDate: Date;
    private time: Date;
    private location: any;
    private status: string;

    public get Key(): string {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get IdUser(): string {
        return this.idUser;
    }

    public set IdUser(value: string) {
        this.idUser = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get EntryDate(): Date {
        return this.entryDate;
    }

    public set EntryDate(value: Date) {
        this.entryDate = value;
    }

    public get ExitDate(): Date {
        return this.exitDate;
    }

    public set ExitDate(value: Date) {
        this.exitDate = value;
    }

    public get Time(): Date {
        return this.time;
    }

    public set Time(value: Date) {
        this.time = value;
    }

    public get Location(): any {
        return this.location;
    }

    public set Location(value: any) {
        this.location = value;
    }

    public get Status(): string {
        return this.status;
    }

    public set Status(value: string) {
        this.status = value;
    }

    constructor(name?: string, idUser?: string, date?: Date, entryDate?: Date, exitDate?: Date, time?: Date, location?: any, status?: string) {
        this.key = KeyGenerator.generate(15);
        this.name = name || '';
        this.idUser = idUser || '';
        this.date = date || new Date();
        this.entryDate = entryDate || new Date();
        this.exitDate = exitDate || new Date();
        this.time = time || new Date();
        this.location = location || '';
        this.status = status || '';
    }

    getKey(): string {
        return this.Key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('idUser', this.idUser);
        map.set('date', this.date);
        map.set('entryDate', this.entryDate);
        map.set('exitDate', this.exitDate);
        map.set('time', this.time);
        map.set('location', this.location);
        map.set('status', this.status);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('idUser', this.idUser);
        map.set('date', this.date);
        map.set('entryDate', this.entryDate);
        map.set('exitDate', this.exitDate);
        map.set('time', this.time);
        map.set('location', this.location);
        map.set('status', this.status);
        return map;
    }
}

// class Mark implements IMark, IModel  {
//     private id: string;
//     private name: string;
//     private lastName: string;
//     private idEmployee: string;
//     private date: Date;
//     private entryDate: Date;
//     private exitDate: Date;
//     private pause: IPause;
//     private location: string ;
//     private ccc: string;
//     private workplace: string ;
//     private status: string ;
//     private key: string;
//     protected apiObject: any;

//     public get ApiObject(): any {
//         return this.apiObject;
//     }
    
//     public set ApiObject(value: any) {
//         this.apiObject = value;
//     }


//     constructor(name?: string, lastName?: string, idEmployee?: string, date?: Date, entryDate?: Date, exitDate?: Date, pause?: IPause, location?: string, ccc?: string, workplace?: string, status?: string) {
//         this.id = KeyGenerator.generate(15);
//         this.name = name || '';
//         this.lastName = lastName || '';
//         this.idEmployee = idEmployee || '';
//         this.date = date || new Date();
//         this.entryDate = entryDate || new Date();
//         this.exitDate = exitDate || new Date();
//         this.pause = pause || { StartPause: new Date(), EndPause: new Date() };
//         this.location = location || '';
//         this.ccc = ccc || '';
//         this.workplace = workplace || '';
//         this.status = status || '';
//         this.key = this.id || '';
//     }

//     public get Id(): string {
//         return this.id;
//     }

//     public set Id(value: string) {
//         this.id = value;
//     }

//     public get Name(): string {
//         return this.name;
//     }

//     public set Name(value: string) {
//         this.name = value;
//     }

//     public get Lastname(): string {
//         return this.lastName;
//     }

//     public set Lastname(value: string) {
//         this.lastName = value;
//     }

//     public get IdEmployee(): string {
//         return this.idEmployee;
//     }

//     public set IdEmployee(value: string) {
//         this.idEmployee = value;
//     }

//     public get Date(): Date {
//         return this.date;
//     }

//     public set Date(value: Date) {
//         this.date = value;
//     }

//     public get EntryDate(): Date {
//         return this.entryDate;
//     }

//     public set EntryDate(value: Date) {
//         this.entryDate = value;
//     }

//     public get ExitDate(): Date {
//         return this.exitDate;
//     }

//     public set ExitDate(value: Date) {
//         this.exitDate = value;
//     }

//     public get Pause(): IPause {
//         return this.pause;
//     }

//     public set Pause(value: IPause) {
//         this.pause = value;
//     }

//     public get Location(): string {
//         return this.location;
//     }

//     public set Location(value: string) {
//         this.location = value;
//     }

//     public get Ccc(): string {
//         return this.ccc;
//     }

//     public set Ccc(value: string) {
//         this.ccc = value;
//     }

//     public get Workplace(): string {
//         return this.workplace;
//     }

//     public set Workplace(value: string) {
//         this.workplace = value;
//     }

//     public get Status(): string {
//         return this.status;
//     }

//     public set Status(value: string) {
//         this.status = value;
//     }

//     public get Key() {
//         return this.key;
//     }

//     public set Key(value: string) {
//         this.key = value;
//     }

//     getKey(): string {
//         return this.idEmployee;
//     }

//     getFilterableFields(): Map<string, any> {
//         let map = new Map<string, any>();
//         map.set('name', this.name);
//         map.set('lastname', this.lastName);
//         map.set('idEmployee', this.idEmployee);
//         map.set('date', this.date);
//         map.set('entryDate', this.entryDate);
//         map.set('exitDate', this.exitDate);
//         map.set('pause', this.pause);
//         map.set('location', this.location);
//         map.set('ccc', this.ccc);
//         map.set('workplace', this.workplace);
//         map.set('status', this.status);
//         return map;
//     }

//     getSortableFields(): Map<string, any> {
//         let map = new Map<string, any>();
//         map.set('name', this.name);
//         map.set('lastname', this.lastName);
//         map.set('idEmployee', this.idEmployee);
//         map.set('date', this.date);
//         map.set('entryDate', this.entryDate);
//         map.set('exitDate', this.exitDate);
//         map.set('pause', this.pause);
//         map.set('location', this.location);
//         map.set('ccc', this.ccc);
//         map.set('workplace', this.workplace);
//         map.set('status', this.status);
//         return map;
//     }

// }

class ApiMark implements IApiModel {

    http = new ApiHttpRequest();

    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        if(currentMethod == GET_MULTIPLE && filter && filter.fields?.has('idUser')){
            const params = {
                group: 'DAY',
                startDate: '2023-09-06',
                endDate: '2023-09-06',
                active: true,
                user: filter.fields?.get('idUser')
            }
            return [this.http.makeURL('/ms/api/timecontrol/list-holder', params)];
        }
        if(currentMethod == GET_MULTIPLE){
            const params = {
                group: 'DAY',
                startDate: '2023-09-06',
                endDate: '2023-09-06',
                active: true
            }
            return [this.http.makeURL('/ms/api/timecontrol/list', params)];
        }
        throw new Error("Method not implemented.");
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE) return GET_METHOD
        throw new Error("Method not implemented.");
    }

    parseDataToSend(data: any, currentMethod?: string, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }

    parseDataToReceive(data: any, currentMethod?: string, filter?: IFilter | undefined) {
        let mark = new Mark();
        mark.ApiObject = data;
        mark.IdUser = data.task_holder.id ? data.task_holder.id : '';
        mark.Name = data.task_holder.name ? data.task_holder.name : '';
        mark.Location = data.coordinates ? data.coordinates : {};
        mark.Status = data.status ? data.status : '';
        mark.Date = data.date ? new Date(data.date) : new Date();
        mark.EntryDate
        mark.ExitDate
        return mark;
        throw new Error("Method not implemented.");
    }

    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return false;
    }

}

class StorableMark extends Mark implements IStorable<Mark> {
    getCollection(): ICollection<Mark> {
        return marks;
    }
    getLocalStorage(): string {
        return 'marks';
    }
}

class MarkDetail implements IMarkDetail, IModel {
    private key: string;
    private apiObject: any;
    private id: string;
    private lastDate: Date;
    private lastModification: Date;
    private status: string;
    private location: any;

    public get Key(){
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    public get ApiObject(){
        return this.apiObject;
    }

    public set ApiObject(value: any){
        this.apiObject = value;
    }

    public get Id(){
        return this.id;
    }

    public set Id(value: string){
        this.id = value;
    }

    public get LastDate(){
        return this.lastDate;
    }

    public set LastDate(value: Date){
        this.lastDate = value;
    }

    public get LastModification(){
        return this.lastModification;
    }

    public set LastModification(value: Date){
        this.lastModification = value;
    }

    public get Status(){
        return this.status;
    }

    public set Status(value: string){
        this.status = value;
    }

    public get Location(){
        return this.location;
    }

    public set Location(value: any){
        this.location = value;
    }

    constructor(id?: string, lastDate?: Date, lastModification?: Date, status?: string, location?: any) {
        this.id = id || '';
        this.lastDate = lastDate || new Date();
        this.lastModification = lastModification || new Date();
        this.status = status || '';
        this.location = location || {};
        this.key = KeyGenerator.generate(15);
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('id', this.id);
        map.set('lastDate', this.lastDate);
        map.set('lastModification', this.lastModification);
        map.set('status', this.status);
        map.set('location', this.location);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('id', this.id);
        map.set('lastDate', this.lastDate);
        map.set('lastModification', this.lastModification);
        map.set('status', this.status);
        map.set('location', this.location);
        return map;
    }
    
}

class StorableMarkDetail extends MarkDetail implements IStorable<MarkDetail> {
    getCollection(): ICollection<MarkDetail> {
        return marksDetails;
    }
    getLocalStorage(): string {
        return 'marksDetails';
    }
}

class ApiMarkDetail extends MarkDetail implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return false;
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
    private enterprises: string [];
    protected apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }
    
    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    constructor(name?: string, lastname?: string, document?: string, email?: string, password?: string, phone?: string, active?: boolean, enterprises?: string[]) {
        this.name = name || '';
        this.lastname = lastname || '';
        this.document = document || '';
        this.email = email || '';
        this.password = password || '';
        this.phone = phone || '';
        this.active = active || true;
        this.enterprises = enterprises || [];
        this.key = document || '';
    }

    public get Enterprises(): string[] {
        return this.enterprises;
    }

    public set Enterprises(value: string[]) {
        this.enterprises = value;
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

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
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

    getSortableFields(): Map<string, any> {
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

class ApiUser extends User implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new ErrorResponse('0199')
    }

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new ErrorResponse('0199')
    }

    localFilter(): boolean {
        return false;
    }

    parseDataToSend(data: any) {
        throw new ErrorResponse('0199')
    }

    parseDataToReceive(data: any) {
        throw new ErrorResponse('0199')
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

class DocumentTag implements IDocumentTag, IModel {
    private key: string;
    private name: string;
    private apiObject: any;

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    constructor(name?: string) {
        this.name = name || '';
        this.key = KeyGenerator.generate(15);
        this.apiObject = {};
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        return map;
    }
}

class StorableDocumentTag extends DocumentTag implements IStorable<DocumentTag> {
    getCollection(): ICollection<DocumentTag> {
        return documentTags;
    }
    getLocalStorage(): string {
        return 'documentTags';
    }
}

class ApiDocumentTag extends DocumentTag implements IApiModel {

    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        if(currentMethod == GET_MULTIPLE) return GET_METHOD;
        if(currentMethod == CREATE_SINGLE) return POST_METHOD;
        if(currentMethod == UPDATE_SINGLE) return POST_METHOD;
        throw new Error("Method not implemented.");
    }

    parseDataToSend(data: any, currentMethod: string, filter?: IFilter | undefined) {
        if(currentMethod == CREATE_SINGLE)
            return {'name': data.Name};
        if(currentMethod == UPDATE_SINGLE){
            data.ApiObject.name = data.name;
            return data.ApiObject;
        }
        throw new Error("Method not implemented.");
    }

    parseDataToReceive(data: any, currentMethod: string, filter?: IFilter | undefined) {
        let tag = new DocumentTag();
        tag.Name = data.name;
        tag.ApiObject = data;
        tag.Key = data.id;
        return tag;
        throw new Error("Method not implemented.");
    }

    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        return true;
    }

    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        if(currentMethod == GET_MULTIPLE){
            return ['/ms/api/attachment/' + localStorage.getItem('domainName') + '/' + localStorage.getItem('login') + '/tag?domain=' + localStorage.getItem('domainId')];
        }
        if(currentMethod == CREATE_SINGLE){
            return ['/ms/api/attachment/' + localStorage.getItem('domainName') + '/' + localStorage.getItem('login') + '/tag/create'];
        }
        if(currentMethod == UPDATE_SINGLE){
            return ['/ms/api/attachment/' + localStorage.getItem('domainName') + '/' + localStorage.getItem('login') + '/tag/edit/' + filter?.fields?.get('id')];
        }
        throw new ErrorResponse('0199')
    }

}

class Contact implements IContact, IModel {
    private key: string;
    private apiObject: any;
    private name: string;
    private comercialName: string;
    private country: string;
    private document: string;
    private address: string;
    private email: string;
    private phone: string;
    private web: string;
    private bankAccount: string;
    private paymentMethod: IPaymentMethod;
    private transactionType: IInvoiceTransactionType;
    private irpf: boolean;
    private re: boolean;

    constructor(name?: string, comercialName?: string, country?: string, document?: string, address?: string, email?: string, phone?: string, web?: string, bankAccount?: string, paymentMethod?: IPaymentMethod, transactionType?: IInvoiceTransactionType, irpf?: boolean, re?: boolean) {
        this.key = KeyGenerator.generate(15);
        this.apiObject = {};
        this.name = name || '';
        this.comercialName = comercialName || '';
        this.country = country || '';
        this.document = document || '';
        this.address = address || '';
        this.email = email || '';
        this.phone = phone || '';
        this.web = web || '';
        this.bankAccount = bankAccount || '';
        this.paymentMethod = paymentMethod || new PaymentMethod();
        this.transactionType = transactionType || new InvoiceTransactionType();
        this.irpf = irpf || false;
        this.re = re || false;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Name(): string {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get ComercialName(): string {
        return this.comercialName;
    }

    public set ComercialName(value: string) {
        this.comercialName = value;
    }

    public get Country(): string {
        return this.country;
    }

    public set Country(value: string) {
        this.country = value;
    }

    public get Document(): string {
        return this.document;
    }

    public set Document(value: string) {
        this.document = value;
    }

    public get Address(): string {
        return this.address;
    }

    public set Address(value: string) {
        this.address = value;
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

    public get Web(): string {
        return this.web;
    }

    public set Web(value: string) {
        this.web = value;
    }

    public get BankAccount(): string {
        return this.bankAccount;
    }

    public set BankAccount(value: string) {
        this.bankAccount = value;
    }

    public get PaymentMethod(): IPaymentMethod {
        return this.paymentMethod;
    }

    public set PaymentMethod(value: IPaymentMethod) {
        this.paymentMethod = value;
    }

    public get TransactionType(): IInvoiceTransactionType {
        return this.transactionType;
    }

    public set TransactionType(value: IInvoiceTransactionType) {
        this.transactionType = value;
    }

    public get IRPF(): boolean {
        return this.irpf;
    }

    public set IRPF(value: boolean) {
        this.irpf = value;
    }

    public get RE(): boolean {
        return this.re;
    }

    public set RE(value: boolean) {
        this.re = value;
    }

    getKey(): string {
        return this.key;
    }
    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('comercialName', this.comercialName);
        map.set('country', this.country);
        map.set('document', this.document);
        map.set('address', this.address);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('web', this.web);
        return map;
    }
    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('name', this.name);
        map.set('comercialName', this.comercialName);
        map.set('country', this.country);
        map.set('document', this.document);
        map.set('address', this.address);
        map.set('email', this.email);
        map.set('phone', this.phone);
        map.set('web', this.web);
        return map;
    }

}

class StorableContact implements IStorable<Contact> {
    getCollection(): ICollection<Contact> {
        return contacts;
    }
    getLocalStorage(): string {
        return 'contacts';
    }
}

class ApiContact implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
    }
}

class PaymentMethod extends SimpleValue implements IPaymentMethod, IModel {}

class StorablePaymentMethod implements IStorable<PaymentMethod> {
    getCollection(): ICollection<PaymentMethod> {
        return paymentMethods;
    }
    getLocalStorage(): string {
        return 'paymentMethods';
    }
}

class ApiPaymentMethod implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
    }
}

class InvoiceTransactionType extends SimpleValue implements IInvoiceTransactionType, IModel {}

class StorableInvoiceTransactionType implements IStorable<InvoiceTransactionType> {
    getCollection(): ICollection<InvoiceTransactionType> {
        return invoiceTransactionTypes;
    }
    getLocalStorage(): string {
        return 'invoiceTransactionTypes';
    }
}

class ApiInvoiceTransactionType implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
    }
}


class IRPF extends SimpleValue implements IIRPF, IModel{
    private type: string;

    constructor(value?: string, type?: string) {
        super(value);
        this.type = type || '';
    }

    public get Type() {
        return this.type;
    }

    public set Type(value: string) {
        this.type = value;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        map.set('type', this.type);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('value', this.value);
        map.set('type', this.type);
        return map;
    }
}

class StorableIRPF implements IStorable<IRPF> {
    getCollection(): ICollection<IRPF> {
        return irpf;
    }
    getLocalStorage(): string {
        return 'irpf';
    }
}

class Tax extends SimpleValue implements ITax, IModel {}

class StorableTax implements IStorable<Tax> {
    getCollection(): ICollection<Tax> {
        return taxes;
    }
    getLocalStorage(): string {
        return 'taxes';
    }
}

class ProductCategory extends SimpleValue implements IProductCategory, IModel{}

class StorableProductCategory implements IStorable<ProductCategory> {
    getCollection(): ICollection<ProductCategory> {
        return productCategories;
    }
    getLocalStorage(): string {
        return 'productCategories';
    }
}

class ProductClass extends SimpleValue implements IProductClass, IModel{}

class StorableProductClass implements IStorable<ProductClass> {
    getCollection(): ICollection<ProductClass> {
        return productClasses;
    }
    getLocalStorage(): string {
        return 'productClasses';
    }
}

class ProductType extends SimpleValue implements IProductType, IModel{}

class StorableProductType implements IStorable<ProductType> {
    getCollection(): ICollection<ProductType> {
        return productTypes;
    }
    getLocalStorage(): string {
        return 'productTypes';
    }
}

class ProductStatus extends SimpleValue implements IProductStatus, IModel{}

class StorableProductStatus implements IStorable<ProductStatus> {
    getCollection(): ICollection<ProductStatus> {
        return productStatuses;
    }
    getLocalStorage(): string {
        return 'productStatuses';
    }
}

class ProductCode extends SimpleValue implements IProductCode, IModel{}

class StorableProductCode implements IStorable<ProductCode> {
    getCollection(): ICollection<ProductCode> {
        return productCodes;
    }
    getLocalStorage(): string {
        return 'productCodes';
    }
}

class Product implements IProduct, IModel {
    private key: string;
    private apiObject: any;
    private code: IProductCode;
    private name: string;
    private category: IProductCategory;
    private class: IProductClass;
    private type: IProductType;
    private status: IProductStatus;
    private tax: ITax;
    private irpf: IIRPF;
    private barCode: string;
    private description: string;
    private costPrice: number;
    private benefit: number;
    private price: number;
    private pvp: number;

    constructor(code?: IProductCode, name?: string, category?: IProductCategory, productClass?: IProductClass, type?: IProductType, status?: IProductStatus,
        tax?: ITax, irpf?: IIRPF, barCode?: string, description?: string, costPrice?: number, benefit?: number, price?: number, pvp?: number) {
        this.apiObject = {};
        this.key = KeyGenerator.generate(15);
        this.code = code || new ProductCode();
        this.name = name || '';
        this.category = category || new ProductCategory();
        this.class = productClass || new ProductClass();
        this.type = type || new ProductType();
        this.status = status || new ProductStatus();
        this.tax = tax || new Tax();
        this.irpf = irpf || new IRPF();
        this.barCode = barCode || '';
        this.description = description || '';
        this.costPrice = costPrice || 0;
        this.benefit = benefit || 0;
        this.price = price || 0;
        this.pvp = pvp || 0;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Code() {
        return this.code;
    }

    public set Code(value: IProductCode) {
        this.code = value;
    }

    public get Name() {
        return this.name;
    }

    public set Name(value: string) {
        this.name = value;
    }

    public get Category() {
        return this.category;
    }

    public set Category(value: IProductCategory) {
        this.category = value;
    }

    public get Class() {
        return this.class;
    }

    public set Class(value: IProductClass) {
        this.class = value;
    }

    public get Type() {
        return this.type;
    }

    public set Type(value: IProductType) {
        this.type = value;
    }

    public get Status() {
        return this.status;
    }

    public set Status(value: IProductStatus) {
        this.status = value;
    }

    public get Tax() {
        return this.tax;
    }

    public set Tax(value: ITax) {
        this.tax = value;
    }

    public get IRPF() {
        return this.irpf;
    }

    public set IRPF(value: IIRPF) {
        this.irpf = value;
    }

    public get BarCode() {
        return this.barCode;
    }

    public set BarCode(value: string) {
        this.barCode = value;
    }

    public get Description() {
        return this.description;
    }

    public set Description(value: string) {
        this.description = value;
    }

    public get CostPrice() {
        return this.costPrice;
    }

    public set CostPrice(value: number) {
        this.costPrice = value;
    }

    public get Benefit() {
        return this.benefit;
    }

    public set Benefit(value: number) {
        this.benefit = value;
    }

    public get Price() {
        return this.price;
    }

    public set Price(value: number) {
        this.price = value;
    }

    public get Pvp() {
        return this.pvp;
    }

    public set Pvp(value: number) {
        this.pvp = value;
    }

    getKey(): string {
        return this.key;
    }
    getFilterableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('code', this.code);
        map.set('name', this.name);
        map.set('category', this.category);
        map.set('class', this.class);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('tax', this.tax);
        map.set('irpf', this.irpf);
        map.set('barCode', this.barCode);
        map.set('description', this.description);
        map.set('costPrice', this.costPrice);
        map.set('benefit', this.benefit);
        map.set('price', this.price);
        map.set('pvp', this.pvp);
        return map;
    }
    getSortableFields(): Map<string, any> {
        let map = new Map<string, any>();
        map.set('code', this.code);
        map.set('name', this.name);
        map.set('category', this.category);
        map.set('class', this.class);
        map.set('type', this.type);
        map.set('status', this.status);
        map.set('tax', this.tax);
        map.set('irpf', this.irpf);
        map.set('barCode', this.barCode);
        map.set('description', this.description);
        map.set('costPrice', this.costPrice);
        map.set('benefit', this.benefit);
        map.set('price', this.price);
        map.set('pvp', this.pvp);
        return map;
    }
}

class StorableProduct implements IStorable<Product> {
    getCollection(): ICollection<Product> {
        return products;
    }
    getLocalStorage(): string {
        return 'products';
    }
}

class InvoiceSerie extends SimpleValue implements IInvoiceSerie, IModel {}

class StorableInvoiceSerie implements IStorable<InvoiceSerie> {
    getCollection(): ICollection<InvoiceSerie> {
        return invoiceSeries;
    }
    getLocalStorage(): string {
        return 'invoiceSeries';
    }
}

class InvoiceActivity extends SimpleValue implements IInvoiceActivity, IModel {}

class StorableInvoiceActivity implements IStorable<InvoiceActivity> {
    getCollection(): ICollection<InvoiceActivity> {
        return invoiceActivities;
    }
    getLocalStorage(): string {
        return 'invoiceActivities';
    }
}

class InvoiceCategory extends SimpleValue implements IInvoiceCategory, IModel {}

class StorableInvoiceCategory implements IStorable<InvoiceCategory> {
    getCollection(): ICollection<InvoiceCategory> {
        return invoiceCategories;
    }
    getLocalStorage(): string {
        return 'invoiceCategories';
    }
}

class InvoiceLine implements IInvoiceLine, IModel {
    key: string;
    apiObject: any;
    product: IProduct;
    quantity: number;
    price: number;
    discount: number;
    totalPrice: number;
    tax: ITax;
    irpf: IIRPF;

    constructor(product?: IProduct,quantity?: number,price?: number,discount?: number,totalPrice?: number,tax?: ITax,irpf?: IIRPF){
        this.apiObject = {};
        this.key = KeyGenerator.generate(15);
        this.product = product || new Product();
        this.quantity = quantity || 0;
        this.price = price || 0;
        this.discount = discount || 0;
        this.totalPrice = totalPrice || 0;
        this.tax = tax || new Tax();
        this.irpf = irpf || new IRPF();
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Product(): IProduct {
        return this.product;
    }

    public set Product(value: IProduct) {
        this.product = value;
    }

    public get Quantity(): number {
        return this.quantity;
    }

    public set Quantity(value: number) {
        this.quantity = value;
    }

    public get Price(): number {
        return this.price;
    }

    public set Price(value: number) {
        this.price = value;
    }

    public get Discount(): number {
        return this.discount;
    }

    public set Discount(value: number) {
        this.discount = value;
    }

    public get TotalPrice(): number {
        return this.totalPrice;
    }

    public set TotalPrice(value: number) {
        this.totalPrice = value;
    }

    public get Tax(): ITax {
        return this.tax;
    }

    public set Tax(value: ITax) {
        this.tax = value;
    }

    public get IRPF(): IIRPF {
        return this.irpf;
    }

    public set IRPF(value: IIRPF) {
        this.irpf = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        return new Map<string,any>();
    }

    getSortableFields(): Map<string, any> {
        return new Map<string,any>();
    }
}

class InvoiceExpirationLine implements IInvoiceExpirationLine, IModel {
    date: Date;
    paymentMethod: IPaymentMethod;
    bankAccount: string;
    amount: number;
    key: string;
    apiObject: any;
    
    constructor(date?: Date, paymentMethod?: IPaymentMethod, bankAccount?: string, amount?: number){
        this.apiObject = {};
        this.key = KeyGenerator.generate(15);
        this.date = date || new Date();
        this.paymentMethod = paymentMethod || new PaymentMethod();
        this.bankAccount = bankAccount || '';
        this.amount = amount || 0;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Key() {
        return this.key;
    }

    public set Key(value: string) {
        this.key = value;
    }

    public get Date() {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get PaymentMethod(): IPaymentMethod {
        return this.paymentMethod;
    }

    public set PaymentMethod(value: IPaymentMethod) {
        this.paymentMethod = value;
    }

    public get BankAccount(): string {
        return this.bankAccount;
    }

    public set BankAccount(value: string) {
        this.bankAccount = value;
    }

    public get Amount(): number {
        return this.amount;
    }

    public set Amount(value: number) {
        this.amount = value;
    }

    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('date', this.date);
        map.set('paymentMethod', this.paymentMethod.Value);
        map.set('bankAccount', this.bankAccount);
        map.set('amount', this.amount);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('date', this.date);
        map.set('paymentMethod', this.paymentMethod.Value);
        map.set('bankAccount', this.bankAccount);
        map.set('amount', this.amount);
        return map;
    }
}

class Invoice implements IInvoice, IModel {
    key: string;
    apiObject: any;
    serie: IInvoiceSerie;
    invoiceNumber: string;
    date: Date;
    totalAmount: number;
    contact: IContact;
    category: IInvoiceCategory;
    lines: ICollection<IInvoiceLine>;
    transactionType: IInvoiceTransactionType;
    activity: IInvoiceActivity;
    criCaja: boolean;
    re: boolean;
    regAgri: boolean;
    tax: ITax;
    taxBase: number;
    taxQuota: number;
    irpf: IIRPF;
    irpfbase: number;
    irpfquota: number;
    invoiceExpirationLines: ICollection<IInvoiceExpirationLine>;
    type: InvoiceType;
    rectified: boolean;
    status: InvoiceStatus;

    constructor(serie?: IInvoiceSerie, invoiceNumber?: string, date?: Date, totalAmount?: number, contact?: IContact, category?: IInvoiceCategory, 
    lines?: ICollection<IInvoiceLine>, transactionType?: IInvoiceTransactionType, activity?: IInvoiceActivity, criCaja?: boolean, re?: boolean, 
    regAgri?: boolean, tax?: ITax, taxBase?: number, taxQuota?: number, irpf?: IIRPF, irpfbase?: number, irpfquota?: number, type?: InvoiceType, 
    rectified?: boolean, status?: InvoiceStatus){
        this.serie = serie || new InvoiceSerie();
        this.invoiceNumber = invoiceNumber || '';
        this.date = date || new Date();
        this.totalAmount = totalAmount || 0;
        this.contact = contact || new Contact();
        this.category = category || new InvoiceCategory();
        this.lines = lines || new Collection<IInvoiceLine>();
        this.transactionType = transactionType || new InvoiceTransactionType();
        this.activity = activity || new InvoiceActivity();
        this.criCaja = criCaja || false;
        this.re = re || false;
        this.regAgri = regAgri || false;
        this.tax = tax || new Tax();
        this.taxBase = taxBase || 0;
        this.taxQuota = taxQuota || 0;
        this.irpf = irpf || new IRPF();
        this.irpfbase = irpfbase || 0;
        this.irpfquota = irpfquota || 0;
        this.invoiceExpirationLines = new Collection<IInvoiceExpirationLine>();
        this.key = KeyGenerator.generate(15);
        this.apiObject = {};
        this.type = type || InvoiceType.GASTO;
        this.rectified = rectified || false;
        this.status = status || InvoiceStatus.A_CONTABILIZAR;
    }

    public get Status(): InvoiceStatus {
        return this.status;
    }

    public set Status(value: InvoiceStatus) {
        this.status = value;
    }

    public get Type(): InvoiceType {
        return this.type;
    }

    public set Type(value: InvoiceType) {
        this.type = value;
    }

    public get Rectified(): boolean {
        return this.rectified;
    }

    public set Rectified(value: boolean) {
        this.rectified = value;
    }

    public get Key(){
        return this.key;
    }

    public set Key(value: string){
        this.key = value;
    }

    public get ApiObject(): any {
        return this.apiObject;
    }

    public set ApiObject(value: any) {
        this.apiObject = value;
    }

    public get Serie(): IInvoiceSerie {
        return this.serie;
    }

    public set Serie(value: IInvoiceSerie) {
        this.serie = value;
    }

    public get InvoiceNumber(): string {
        return this.invoiceNumber;
    }

    public set InvoiceNumber(value: string) {
        this.invoiceNumber = value;
    }

    public get Date(): Date {
        return this.date;
    }

    public set Date(value: Date) {
        this.date = value;
    }

    public get TotalAmount(): number {
        return this.totalAmount;
    }

    public set TotalAmount(value: number) {
        this.totalAmount = value;
    }

    public get Contact(): IContact {
        return this.contact;
    }

    public set Contact(value: IContact) {
        this.contact = value;
    }

    public get Category(): IInvoiceCategory {
        return this.category;
    }

    public set Category(value: IInvoiceCategory) {
        this.category = value;
    }

    public get Lines(): ICollection<IInvoiceLine> {
        return this.lines;
    }

    public set Lines(value: ICollection<IInvoiceLine>) {
        this.lines = value;
    }

    public get TransactionType(): IInvoiceTransactionType {
        return this.transactionType;
    }

    public set TransactionType(value: IInvoiceTransactionType) {
        this.transactionType = value;
    }

    public get Activity(): IInvoiceActivity {
        return this.activity;
    }

    public set Activity(value: IInvoiceActivity) {
        this.activity = value;
    }

    public get CriCaja(): boolean {
        return this.criCaja;
    }

    public set CriCaja(value: boolean) {
        this.criCaja = value;
    }

    public get RE(): boolean {
        return this.re;
    }

    public set RE(value: boolean) {
        this.re = value;
    }

    public get RegAgri(): boolean {
        return this.regAgri;
    }

    public set RegAgri(value: boolean) {
        this.regAgri = value;
    }

    public get Tax(): ITax {
        return this.tax;
    }

    public set Tax(value: ITax) {
        this.tax = value;
    }

    public get TaxBase(): number {
        return this.taxBase;
    }

    public set TaxBase(value: number) {
        this.taxBase = value;
    }

    public get TaxQuota(): number {
        return this.taxQuota;
    }

    public set TaxQuota(value: number) {
        this.taxQuota = value;
    }

    public get IRPF(): IIRPF {
        return this.irpf;
    }

    public set IRPF(value: IIRPF) {
        this.irpf = value;
    }

    public get IRPFBase(): number {
        return this.irpfbase;
    }

    public set IRPFBase(value: number) {
        this.irpfbase = value;
    }

    public get IRPFQuota(): number {
        return this.irpfquota;
    }

    public set IRPFQuota(value: number) {
        this.irpfquota = value;
    }

    public get InvoiceExpirationLines(): ICollection<IInvoiceExpirationLine> {
        return this.invoiceExpirationLines;
    }

    public set InvoiceExpirationLines(value: ICollection<IInvoiceExpirationLine>) {
        this.invoiceExpirationLines = value;
    }
    
    getKey(): string {
        return this.key;
    }

    getFilterableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('serie', this.serie.Value);
        map.set('invoiceNumber', this.invoiceNumber);
        map.set('date', this.date);
        map.set('totalAmount', this.totalAmount);
        map.set('contact', this.contact.Name);
        map.set('category', this.category.Value);
        map.set('transactionType', this.transactionType.Value);
        map.set('activity', this.activity.Value);
        map.set('criCaja', this.criCaja);
        map.set('re', this.re);
        map.set('regAgri', this.regAgri);
        map.set('tax', this.tax.Value);
        map.set('taxBase', this.taxBase);
        map.set('taxQuota', this.taxQuota);
        map.set('irpf', this.irpf.Value);
        map.set('irpfbase', this.irpfbase);
        map.set('irpfquota', this.irpfquota);
        map.set('type', this.type);
        map.set('rectified', this.rectified);
        return map;
    }

    getSortableFields(): Map<string, any> {
        let map = new Map<string,any>();
        map.set('serie', this.serie.Value);
        map.set('invoiceNumber', this.invoiceNumber);
        map.set('date', this.date);
        map.set('totalAmount', this.totalAmount);
        map.set('contact', this.contact.Name);
        map.set('category', this.category.Value);
        map.set('transactionType', this.transactionType.Value);
        map.set('activity', this.activity.Value);
        map.set('criCaja', this.criCaja);
        map.set('re', this.re);
        map.set('regAgri', this.regAgri);
        map.set('tax', this.tax.Value);
        map.set('taxBase', this.taxBase);
        map.set('taxQuota', this.taxQuota);
        map.set('irpf', this.irpf.Value);
        map.set('irpfbase', this.irpfbase);
        map.set('irpfquota', this.irpfquota);
        map.set('type', this.type);
        map.set('rectified', this.rectified);
        return map;
    }
}

class StorableInvoice implements IStorable<Invoice> {
    getCollection(): ICollection<Invoice> {
        return invoices;
    }
    getLocalStorage(): string {
        return 'invoices';
    }
}

class ApiInvoice implements IApiModel {
    getUrl(currentMethod: string, filter?: IFilter | undefined): string[] {
        throw new Error("Method not implemented.");
    }
    getMethod(currentMethod: string, filter?: IFilter | undefined): string {
        throw new Error("Method not implemented.");
    }
    parseDataToSend(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    parseDataToReceive(data: any, currentMethod?: string | undefined, filter?: IFilter | undefined) {
        throw new Error("Method not implemented.");
    }
    localFilter(currentMethod?: string | undefined, filter?: IFilter | undefined): boolean {
        throw new Error("Method not implemented.");
    }
}


/**
 *
 * READ FROM LOCAL STORAGE ON PROYECT START FOR DEVELOPMENT
 *
 */

let productCodes: ICollection<ProductCode> = new Collection<ProductCode>();
let storableProductCodes = new StorableProductCode();
let localProductCodes = new LocalStorage<ProductCode>(ProductCode);
productCodes = localProductCodes.read(storableProductCodes.getLocalStorage());
if(productCodes.size() == 0){
    productCodes.add(new ProductCode('0000'));
    productCodes.add(new ProductCode('0001'));
    productCodes.add(new ProductCode('0002'));
    productCodes.add(new ProductCode('0003'));
    productCodes.add(new ProductCode('0004'));
    productCodes.add(new ProductCode('0005'));
    localProductCodes.write(storableProductCodes.getLocalStorage(), productCodes);
}

let productCategories: ICollection<ProductCategory> = new Collection<ProductCategory>();
let storableProductCategories = new StorableProductCategory();
let localProductCategories = new LocalStorage<ProductCategory>(ProductCategory);
productCategories = localProductCategories.read(storableProductCategories.getLocalStorage());
if(productCategories.size() == 0){
    productCategories.add(new ProductCategory('Categoria 1'));
    productCategories.add(new ProductCategory('Categoria 2'));
    productCategories.add(new ProductCategory('Categoria 3'));
    localProductCategories.write(storableProductCategories.getLocalStorage(), productCategories);
}

let productClasses: ICollection<ProductClass> = new Collection<ProductClass>();
let storableProductClasses = new StorableProductClass();
let localProductClasses = new LocalStorage<ProductClass>(ProductClass);
productClasses = localProductClasses.read(storableProductClasses.getLocalStorage())
if(productClasses.size() == 0){
    productClasses.add(new ProductClass('Clase 1'));
    productClasses.add(new ProductClass('Clase 2'));
    productClasses.add(new ProductClass('Clase 3'));
    localProductClasses.write(storableProductClasses.getLocalStorage(), productClasses);
}

let productTypes: ICollection<ProductType> = new Collection<ProductType>();
let storableProductTypes = new StorableProductType();
let localProductTypes = new LocalStorage<ProductType>(ProductType);
productTypes = localProductTypes.read(storableProductTypes.getLocalStorage());
if(productTypes.size() == 0){
    productTypes.add(new ProductType('Tipo 1'));
    productTypes.add(new ProductType('Tipo 2'));
    productTypes.add(new ProductType('Tipo 3'));
    localProductTypes.write(storableProductTypes.getLocalStorage(), productTypes);
}

let productStatuses: ICollection<ProductStatus> = new Collection<ProductStatus>();
let storableProductStatuses = new StorableProductStatus();
let localProductStatuses = new LocalStorage<ProductStatus>(ProductStatus);
productStatuses = localProductStatuses.read(storableProductStatuses.getLocalStorage());
if(productStatuses.size() == 0){
    productStatuses.add(new ProductStatus('ProductStatus 1'));
    productStatuses.add(new ProductStatus('ProductStatus 2'));
    productStatuses.add(new ProductStatus('ProductStatus 3'));
    productStatuses.add(new ProductStatus('ProductStatus 4'));
    localProductStatuses.write(storableProductStatuses.getLocalStorage(), productStatuses);
}


let irpf: ICollection<IRPF> = new Collection<IRPF>();
let storableIRPF = new StorableIRPF();
let localIRPF = new LocalStorage<IRPF>(IRPF);
irpf = localIRPF.read(storableIRPF.getLocalStorage());
if(irpf.size() == 0){
    irpf.add(new IRPF('15', 'profesional'));
    irpf.add(new IRPF('19', 'arrendamiento'));
    localIRPF.write(storableIRPF.getLocalStorage(), irpf);
}

let taxes: ICollection<Tax> = new Collection<Tax>();
let storableTaxes = new StorableTax();
let localTaxes = new LocalStorage<Tax>(Tax);
taxes = localTaxes.read(storableTaxes.getLocalStorage())
if(taxes.size() == 0){
    taxes.add(new Tax('9'));
    taxes.add(new Tax('15'));
    taxes.add(new Tax('19'));
    taxes.add(new Tax('21'));
    localTaxes.write(storableTaxes.getLocalStorage(), taxes);
}

let products: ICollection<Product> = new Collection<Product>();
let storableProducts = new StorableProduct();
let localProducts = new LocalStorage<Product>(Product);
products = localProducts.read(storableProducts.getLocalStorage())
if(products.size() == 0){
    products.add(new Product(productCodes.toArray()[0], 'Producto 1', productCategories.toArray()[0], productClasses.toArray()[0], productCategories.toArray()[0], productTypes.toArray()[0], 
        productStatuses.toArray()[0], irpf.toArray()[0], 'bar code 1', 'descripcion del producto', 32.23, 23.2, 132, 123));
    products.add(new Product(productCodes.toArray()[1], 'Producto 2', productCategories.toArray()[1], productClasses.toArray()[1], productCategories.toArray()[1], productTypes.toArray()[1], 
        productStatuses.toArray()[1], irpf.toArray()[1], 'bar code 2', 'descripcion del producto 2', 123, 54, 32, 323));
    products.add(new Product(productCodes.toArray()[0], 'Producto 3', productCategories.toArray()[0], productClasses.toArray()[0], productCategories.toArray()[0], productTypes.toArray()[0], 
        productStatuses.toArray()[0], irpf.toArray()[0], 'bar code 3', 'descripcion del producto 3', 23, 22, 32, 323));
    localProducts.write(storableProducts.getLocalStorage(), products);
}

let paymentMethods: ICollection<PaymentMethod> = new Collection<PaymentMethod>();
let storablePaymentMethods = new StorablePaymentMethod();
let localPaymentMethods = new LocalStorage<PaymentMethod>(PaymentMethod);
paymentMethods = localPaymentMethods.read(storablePaymentMethods.getLocalStorage())
if(paymentMethods.size() == 0){
    paymentMethods.add(new PaymentMethod('Transferencia'));
    paymentMethods.add(new PaymentMethod('Tarjeta de crédito'));
    paymentMethods.add(new PaymentMethod('Paypal'));
    localPaymentMethods.write(storablePaymentMethods.getLocalStorage(), paymentMethods);
}

let invoiceTransactionTypes: ICollection<InvoiceTransactionType> = new Collection<InvoiceTransactionType>();
let storableInvoiceTransactionTypes = new StorableInvoiceTransactionType();
let localInvoiceTransactionTypes = new LocalStorage<InvoiceTransactionType>(InvoiceTransactionType);
invoiceTransactionTypes = localInvoiceTransactionTypes.read(storableInvoiceTransactionTypes.getLocalStorage())
if(invoiceTransactionTypes.size() == 0){
    invoiceTransactionTypes.add(new InvoiceTransactionType('Tipo 1'));
    invoiceTransactionTypes.add(new InvoiceTransactionType('Tipo 2'));
    invoiceTransactionTypes.add(new InvoiceTransactionType('Tipo 3'));
    localInvoiceTransactionTypes.write(storableInvoiceTransactionTypes.getLocalStorage(), invoiceTransactionTypes);
}

let contacts: ICollection<Contact> = new Collection<Contact>();
let storableContacs = new StorableContact();
let localContacts = new LocalStorage<Contact>(Contact);
contacts = localContacts.read(storableContacs.getLocalStorage())
if(contacts.size() == 0){ 
    contacts.add(new Contact('Nombre del contacto', 'Comercial', 'España', '123456789', 'Calle falsa 123', 'asd@example.com', '2431412413', 'www.123.com', '123456789', paymentMethods.toArray()[0], invoiceTransactionTypes.toArray()[0], false, false));
    contacts.add(new Contact('Contacto test', 'Nombre comercial', 'España', '123456789', 'Calle falsa asd', '123@example.com', '4123143232', 'www.qwe.com', '123456789', paymentMethods.toArray()[1], invoiceTransactionTypes.toArray()[1], false, false));
    contacts.add(new Contact('Contacto de prueba', 'Comercial test', 'España', '123456789', 'Calle falsa 123', 'qwe@example.com', '537534534534', 'www.asd.com', '123456789', paymentMethods.toArray()[2], invoiceTransactionTypes.toArray()[2], false, false));
    localContacts.write(storableContacs.getLocalStorage(), contacts);
}

let invoiceSeries: ICollection<InvoiceSerie> = new Collection<InvoiceSerie>();
let storableInvoiceSeries = new StorableInvoiceSerie();
let localInvoiceSeries = new LocalStorage<InvoiceSerie>(InvoiceSerie);
invoiceSeries = localInvoiceSeries.read(storableInvoiceSeries.getLocalStorage());
if(invoiceSeries.size() == 0){
    invoiceSeries.add(new InvoiceSerie('Serie 1'));
    invoiceSeries.add(new InvoiceSerie('Serie 2'));
    invoiceSeries.add(new InvoiceSerie('Serie 3'));
    localInvoiceSeries.write(storableInvoiceSeries.getLocalStorage(), invoiceSeries);
}

let invoiceActivities: ICollection<InvoiceActivity> = new Collection<InvoiceActivity>();
let storableInvoiceActivities = new StorableInvoiceActivity();
let localInvoiceActivities = new LocalStorage<InvoiceActivity>(InvoiceActivity);
invoiceActivities = localInvoiceActivities.read(storableInvoiceActivities.getLocalStorage())
if(invoiceActivities.size() == 0){
    invoiceActivities.add(new InvoiceActivity('Actividad 1'));
    invoiceActivities.add(new InvoiceActivity('Actividad 2'));
    invoiceActivities.add(new InvoiceActivity('Actividad 3'));
    localInvoiceActivities.write(storableInvoiceActivities.getLocalStorage(), invoiceActivities);
}

let invoiceCategories: ICollection<InvoiceCategory> = new Collection<InvoiceCategory>();
let storableInvoiceCategories = new StorableInvoiceCategory();
let localInvoiceCategories = new LocalStorage<InvoiceCategory>(InvoiceCategory);
invoiceCategories = localInvoiceCategories.read(storableInvoiceCategories.getLocalStorage());
if(invoiceCategories.size() == 0){
    invoiceCategories.add(new InvoiceCategory('Categoria 1'));
    invoiceCategories.add(new InvoiceCategory('Categoria 2'));
    invoiceCategories.add(new InvoiceCategory('Categoria 3'));
    localInvoiceCategories.write(storableInvoiceCategories.getLocalStorage(), invoiceCategories);
}

let invoices: ICollection<Invoice> = new Collection<Invoice>();
let storableInvoices = new StorableInvoice();
let localInvoices = new LocalStorage<Invoice>(Invoice);
invoices = localInvoices.read(storableInvoices.getLocalStorage());
let invoiceLines = new Collection<IInvoiceLine>();
invoiceLines.add(new InvoiceLine(products.toArray()[0],1,20,0,20, taxes.toArray()[0], irpf.toArray()[0]));
if(invoices.size() == 0){
    invoices.add(new Invoice(invoiceSeries.toArray()[0], '1', new Date(), 200, contacts.toArray()[0], invoiceCategories.toArray()[0], invoiceLines,
    invoiceTransactionTypes.toArray()[0], invoiceActivities.toArray()[0], false, false, false, taxes.toArray()[0], 10, 5, irpf.toArray()[0], 10, 5));
    invoices.add(new Invoice(invoiceSeries.toArray()[0], '1', new Date(), 200, contacts.toArray()[0], invoiceCategories.toArray()[0], invoiceLines,
    invoiceTransactionTypes.toArray()[0], invoiceActivities.toArray()[0], false, false, false, taxes.toArray()[0], 10, 5, irpf.toArray()[0], 10, 5));
}

let documents: ICollection<Document> = new Collection<Document>();
let storableDocuments = new StorableDocument();
let localDocuments = new LocalStorage<Document>(Document);
documents = localDocuments.read(storableDocuments.getLocalStorage())
if(documents.size() == 0){
    documents.add(new Document('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',
     'file1', 1, 'image/png', new Date(), '/a_contabilizar'));
    documents.add(new Document('iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAkFBMVEX///8iHx8AAAAfHBwbGBgdGhoaFhYYFBQWEhL8/Pz5+fnm5uYRDAz29vYhHR3w8PCMi4s0MTEOCAgrKCjr6+vb29uEg4PDwsJDQUEmIyO4t7dXVVVMSkrl5OQKAADQz8+qqamamZmhoKBmZWU9Ozuwr6/MzMy8vLxubW1gXl4xLy9JR0d1dHRSUFB9fHxcWlo9FQvaAAALMUlEQVR4nO2de3OqOhDAG0J4KCggKKLy8oWK+v2/3d2lnXNPW4KoWNIz+f1z7wzUyZLNvrLJeXuTSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQikUgkEolEIpEIiB3s9tttmuZ5mm6j/W5l9z2iLvH32bQ8LN3JnBmWpdP5xF0eymk28/seWSckWXF2iWFojFJKKuB/mGYY8825yIK+x/ckdnaajzSVfojFmArAf95lpapGJqd80PcoH8aeHRTvXToNVPO4CcPTcrFYnsKNe5wz/f2RGivr2a9clP7+Gus4U6rFNotimu+Dsfn+aDAOomwKyqsaDN8wvMPvW5J+dLBQPmrQ8yXbr+reATHLk27gTOreYf+7ZJyVlXwsdi/bpEEFx7u0GHm0msfLzvy5AT5J4GwslE9x8+TmzAx3zuRdxjCrnWoB2S8oLC8au/mqlZm0A4fgF1HJYvbqsXWBmc1VkM9imd9a68xViVaJavPslUPrhuFZAZXT5sWdGpecq4lXTuPXjKszEtWAcerL6P4/zUIdvo3hJt2PqjvM2QQ0VJ1cho/8dbLGUEfbzMS1qYOtq6JNzB8MUYbOUQMRw0jUOG6QVgIuZg8P0N5uQER9sxVTRDNycXiLZ7IFc3YEe6O5DyzjH6Bag9bpyeAriKuVvOtmTJ2SYBRtLJ/+nSFqAlPEyxuHFkMV7SANCkIdZ1G0QNxegB80rp0EljsUUSvEyhnNDAVcdOOszX2oEjrPhHKL0ZESdbPv6NcG+QR/TqQwPFh0+9HtEgyqvhYnmTIddIRdLpxhiCLmwugpumk1vPnFV5Gzvp6Xi7UT3TSUO4MSdhTFZQwLyF/1bdMr5ji5TBTFMjRV1QxLUdxp0JwmXTwIH0oxUikz8tBRNL2y2p4U66Mc/FEUtpRFY31t7IJ/VcQwNv5ZI5Q1KJQfnRX1QzCmquxDVL25TrpFxTgLsRKjmJDY4T9PLlSrpDMUY4MVYdeLqxIiMY4Of/HahSHKJJ5gCifckZr7szaqCm/HcpsESLJLi0mMtWDGDvwgYaZRop1fMuT72CswhRk3oUsxkCY0Drer8R+VM8er3I2rUjDfr/s4iYYAScYSp5C7ClMd60vGPP32CQZZVfRXNa4Me5WCl+1qnA+zglVoZTyLscXCGxsVtVPsX0lVXuMp6hAmkW16D2wcHaaQp2lJVVl0eUVQ0zliussVIqUQC+YdDPIZBmeVGLwAcrjE2CvkxwLvlR2t4Dj2BP5er5//n2PmgsHjzJE5pThD2wafZucQ8NEJZ54GpU7YqWdb44wICzlKOtvg6PlmFrGnZASOnWOp0gml8/TJIT5JoUOWUx99jS9g7bXyRsYxXmAFkvMZghMjVkMw8QMEsAz1af0zVGC2uZkeJODY1ZDz2lXrO02MXEonae0j24GAnLXYS7pAAKqk9YvVAT3vN9fPwBSE9f4sAENI3RZZsQ8eVVvUa/qMUGI05mUvxpxaMLj6zz/DBI+jwJ/B6MyqV1N7QkncZ6qPUYdWH1fZGUjYrq67w8g2rX8G5tia9pgHBwuNsPp58g+gwG6rXxkfQRWL+ok66P2aml0IlqDeXQ83MOxLq1+xq/iz3l/AMmDLHss1e/TI9aZuBRYybmcjBpVC10u4jQk99rgrHEHmNK//wgEsLm7W8AVMMZV6Y4prlPUYt20tMprX72gn/FHXv8v/UF6fEoIOTeot3QxH3dIIBnwJh/ioRwlTlLDeCO66mUNb6bUcZeYeSFj/rGFevtH0NfBRj3veecyVEKsbLT++ucV3OfFdzxJucQ7rzfwQ3LjXrodr4FgQwdb/TM9a+hYR8Bb1EYcPiY+2aPUrNqZga06G2LOlgRSQ5/Gr5Injxr+A9jLmFDLAVdJ5jx6/IWp7m2Ha1yqowZDG4xgleMZ46fFPgJG3ygk+g5Parihvugxe5JjSviNvLMVo13qHaKP9oC2MRFQpKUefXUosp8+ujEzjK9EeuxfWN0dnn7AWwFlqyRwkTJ8Y4NM01GnexiVMIu/h/zgqbs5xPkRGQfpe6zS4EA1eqQLEJ+rphiHc40shzx+sYS0f+t3NL3WwErwhTDVCrOYBJksdVitvbyIJe6+XvuVzUFOeT1iBKyde2WAKgzX4FI1TUq5+fXTss9QGBFWpiGdOsBg48kruLCYooMopR2J5QyfqU/2qXVCANeWvtRTPinhXjq2IzhY2L3CbxWagpKxdreeF7GGQBqdijfkVNpkYm6ymEBA4Rzxj4XG10ARfRI/9twtPKNEP3N58O8dtbkau+Zd8f5id8fCBqkXcem8Awbu67L/lGyNsj++yzGhijQhV5+HlL5cwKzcET9Z4Tc4uwk4FAQ7RjCHoMg4NFZngMEJhmBErYeFkmVNslBhbpqlGyoaDGcO1QaguQh8tdrU11g3NfEn1qkWIGZbnWe/HKqkxXzQ6gmrjo+x4sA+RYFPIofGVlXOYWNr/jW0jqlvHdd5YqLJxCjkbNj+Mfbk1iW/Yu+dcJwrMH2DFnntwohsnh3aWKFMIY5mPiN7Ym4jglQPZtCyKcprvd8FNE3mFcM4Q5JTXGCZxpLXJAAZjHxi3cQBYUbZuNQH8GHvI4rTmlXg32A/Ibwj7aTATHN3OBO8hxX3hPrdGv4BJnnHt8JTLGBJ/thFmCmF9XVi3HWgZ6n2v9ZmvJJBEPXcu7xPYpqMuBTGkH1QnLrhNmHdiOxgc9Jzbf8WHFIN1dUh5Bxqhnh46S/xCcgzAuzk245fYENd3X+l3sFO0m32wCGs3YRe/1C14jodqHXiMlQuGORYi5P7MYNpNqGxOMaVu1S720wRLrYvdTAxI1VC0M7IV5pZQovI2kdpi4/lRrf/yUy1+icXBJ91YVfa5iBOQfqaqbz7X8Do7Nm1j9E8V2ayfcNXjAyRNhniu8A82DJB4zd35jTjg63VBzlXWswMl4x+huckeXCETWEeRTHni4oHVGqaQCayjyADvVvCmD3Vm26ijxu198Z7BnXfafOyZB5Z7RoaA4doXUux5feTeDh9v32nXgdMvfoHV+PX9f3hF/Rbsvo96ghNe9nR3aJNV8ahoaW89UZXv3+kydgQvURDi6PZtbAdGq9/nMoZ4d8ioq0LPy1ld8fDrPfVcGw9jagJdZnKL3QRDG+7u/jfMreAB93ewLqVtWpfekhACbpK+bjwvYIG3WixbLiv/qoOD6f/k/V3YWHprOWgbb2rRji8eUeckE7x9JW3zKqo04xwuEhgzxQBVbWE88LQFJb8gWvvKeIou4/bJumCD1xIItc/UltUBL2stb8TgfoEtmsXviNa+skMfMG+OUwZV/HMWayOtPbgU2aTxnrotxrB9t5A+AXbaqG7DUpy5jIyYYDuFd3HF3kz+TW7BScc9uZ8cUdf4eI0lN60drvEetLPIxcPb7KptxXo1HEwx6RX7duvbVI5/VH/xQxbj0TdBN2HaY2f6qD7jj0bYIC3O7Y8Pgw1ThH33eHg/D4kFant6nBUWifWvUQuetiHWM7s4ApHgZaTK5VP4VpnR/s9SdAUW30j8d/g2nlqjZ7cahQJPXdC/TtnZmfVLMyYuDjoG9scx5HjDpyL4JtN9jC8x7mZ85MPbOeb/0/7PinTJal2VpiqfscML+L1CyIaSJ0iW6DMOK7xVHv0E99Di72WG1QpwgCsUVT39ewK+H5oFEdH/cy8i/OWk4CCIDjPJBP0XOp4HtwirC6HTvkfyKqqEEPyEWP/qQaf4Jd5Ec/m3HOFnVgdP4d04+48QnA//mqf/yvDfnkGJRCKRSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQieS3/AZzjrH8362VjAAAAAElFTkSuQmCC',
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
    documents.add(new Document('iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAkFBMVEX///8iHx8AAAAfHBwbGBgdGhoaFhYYFBQWEhL8/Pz5+fnm5uYRDAz29vYhHR3w8PCMi4s0MTEOCAgrKCjr6+vb29uEg4PDwsJDQUEmIyO4t7dXVVVMSkrl5OQKAADQz8+qqamamZmhoKBmZWU9Ozuwr6/MzMy8vLxubW1gXl4xLy9JR0d1dHRSUFB9fHxcWlo9FQvaAAALMUlEQVR4nO2de3OqOhDAG0J4KCggKKLy8oWK+v2/3d2lnXNPW4KoWNIz+f1z7wzUyZLNvrLJeXuTSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQikUgkEolEIpEIiB3s9tttmuZ5mm6j/W5l9z2iLvH32bQ8LN3JnBmWpdP5xF0eymk28/seWSckWXF2iWFojFJKKuB/mGYY8825yIK+x/ckdnaajzSVfojFmArAf95lpapGJqd80PcoH8aeHRTvXToNVPO4CcPTcrFYnsKNe5wz/f2RGivr2a9clP7+Gus4U6rFNotimu+Dsfn+aDAOomwKyqsaDN8wvMPvW5J+dLBQPmrQ8yXbr+reATHLk27gTOreYf+7ZJyVlXwsdi/bpEEFx7u0GHm0msfLzvy5AT5J4GwslE9x8+TmzAx3zuRdxjCrnWoB2S8oLC8au/mqlZm0A4fgF1HJYvbqsXWBmc1VkM9imd9a68xViVaJavPslUPrhuFZAZXT5sWdGpecq4lXTuPXjKszEtWAcerL6P4/zUIdvo3hJt2PqjvM2QQ0VJ1cho/8dbLGUEfbzMS1qYOtq6JNzB8MUYbOUQMRw0jUOG6QVgIuZg8P0N5uQER9sxVTRDNycXiLZ7IFc3YEe6O5DyzjH6Bag9bpyeAriKuVvOtmTJ2SYBRtLJ/+nSFqAlPEyxuHFkMV7SANCkIdZ1G0QNxegB80rp0EljsUUSvEyhnNDAVcdOOszX2oEjrPhHKL0ZESdbPv6NcG+QR/TqQwPFh0+9HtEgyqvhYnmTIddIRdLpxhiCLmwugpumk1vPnFV5Gzvp6Xi7UT3TSUO4MSdhTFZQwLyF/1bdMr5ji5TBTFMjRV1QxLUdxp0JwmXTwIH0oxUikz8tBRNL2y2p4U66Mc/FEUtpRFY31t7IJ/VcQwNv5ZI5Q1KJQfnRX1QzCmquxDVL25TrpFxTgLsRKjmJDY4T9PLlSrpDMUY4MVYdeLqxIiMY4Of/HahSHKJJ5gCifckZr7szaqCm/HcpsESLJLi0mMtWDGDvwgYaZRop1fMuT72CswhRk3oUsxkCY0Drer8R+VM8er3I2rUjDfr/s4iYYAScYSp5C7ClMd60vGPP32CQZZVfRXNa4Me5WCl+1qnA+zglVoZTyLscXCGxsVtVPsX0lVXuMp6hAmkW16D2wcHaaQp2lJVVl0eUVQ0zliussVIqUQC+YdDPIZBmeVGLwAcrjE2CvkxwLvlR2t4Dj2BP5er5//n2PmgsHjzJE5pThD2wafZucQ8NEJZ54GpU7YqWdb44wICzlKOtvg6PlmFrGnZASOnWOp0gml8/TJIT5JoUOWUx99jS9g7bXyRsYxXmAFkvMZghMjVkMw8QMEsAz1af0zVGC2uZkeJODY1ZDz2lXrO02MXEonae0j24GAnLXYS7pAAKqk9YvVAT3vN9fPwBSE9f4sAENI3RZZsQ8eVVvUa/qMUGI05mUvxpxaMLj6zz/DBI+jwJ/B6MyqV1N7QkncZ6qPUYdWH1fZGUjYrq67w8g2rX8G5tia9pgHBwuNsPp58g+gwG6rXxkfQRWL+ok66P2aml0IlqDeXQ83MOxLq1+xq/iz3l/AMmDLHss1e/TI9aZuBRYybmcjBpVC10u4jQk99rgrHEHmNK//wgEsLm7W8AVMMZV6Y4prlPUYt20tMprX72gn/FHXv8v/UF6fEoIOTeot3QxH3dIIBnwJh/ioRwlTlLDeCO66mUNb6bUcZeYeSFj/rGFevtH0NfBRj3veecyVEKsbLT++ucV3OfFdzxJucQ7rzfwQ3LjXrodr4FgQwdb/TM9a+hYR8Bb1EYcPiY+2aPUrNqZga06G2LOlgRSQ5/Gr5Injxr+A9jLmFDLAVdJ5jx6/IWp7m2Ha1yqowZDG4xgleMZ46fFPgJG3ygk+g5Parihvugxe5JjSviNvLMVo13qHaKP9oC2MRFQpKUefXUosp8+ujEzjK9EeuxfWN0dnn7AWwFlqyRwkTJ8Y4NM01GnexiVMIu/h/zgqbs5xPkRGQfpe6zS4EA1eqQLEJ+rphiHc40shzx+sYS0f+t3NL3WwErwhTDVCrOYBJksdVitvbyIJe6+XvuVzUFOeT1iBKyde2WAKgzX4FI1TUq5+fXTss9QGBFWpiGdOsBg48kruLCYooMopR2J5QyfqU/2qXVCANeWvtRTPinhXjq2IzhY2L3CbxWagpKxdreeF7GGQBqdijfkVNpkYm6ymEBA4Rzxj4XG10ARfRI/9twtPKNEP3N58O8dtbkau+Zd8f5id8fCBqkXcem8Awbu67L/lGyNsj++yzGhijQhV5+HlL5cwKzcET9Z4Tc4uwk4FAQ7RjCHoMg4NFZngMEJhmBErYeFkmVNslBhbpqlGyoaDGcO1QaguQh8tdrU11g3NfEn1qkWIGZbnWe/HKqkxXzQ6gmrjo+x4sA+RYFPIofGVlXOYWNr/jW0jqlvHdd5YqLJxCjkbNj+Mfbk1iW/Yu+dcJwrMH2DFnntwohsnh3aWKFMIY5mPiN7Ym4jglQPZtCyKcprvd8FNE3mFcM4Q5JTXGCZxpLXJAAZjHxi3cQBYUbZuNQH8GHvI4rTmlXg32A/Ibwj7aTATHN3OBO8hxX3hPrdGv4BJnnHt8JTLGBJ/thFmCmF9XVi3HWgZ6n2v9ZmvJJBEPXcu7xPYpqMuBTGkH1QnLrhNmHdiOxgc9Jzbf8WHFIN1dUh5Bxqhnh46S/xCcgzAuzk245fYENd3X+l3sFO0m32wCGs3YRe/1C14jodqHXiMlQuGORYi5P7MYNpNqGxOMaVu1S720wRLrYvdTAxI1VC0M7IV5pZQovI2kdpi4/lRrf/yUy1+icXBJ91YVfa5iBOQfqaqbz7X8Do7Nm1j9E8V2ayfcNXjAyRNhniu8A82DJB4zd35jTjg63VBzlXWswMl4x+huckeXCETWEeRTHni4oHVGqaQCayjyADvVvCmD3Vm26ijxu198Z7BnXfafOyZB5Z7RoaA4doXUux5feTeDh9v32nXgdMvfoHV+PX9f3hF/Rbsvo96ghNe9nR3aJNV8ahoaW89UZXv3+kydgQvURDi6PZtbAdGq9/nMoZ4d8ioq0LPy1ld8fDrPfVcGw9jagJdZnKL3QRDG+7u/jfMreAB93ewLqVtWpfekhACbpK+bjwvYIG3WixbLiv/qoOD6f/k/V3YWHprOWgbb2rRji8eUeckE7x9JW3zKqo04xwuEhgzxQBVbWE88LQFJb8gWvvKeIou4/bJumCD1xIItc/UltUBL2stb8TgfoEtmsXviNa+skMfMG+OUwZV/HMWayOtPbgU2aTxnrotxrB9t5A+AXbaqG7DUpy5jIyYYDuFd3HF3kz+TW7BScc9uZ8cUdf4eI0lN60drvEetLPIxcPb7KptxXo1HEwx6RX7duvbVI5/VH/xQxbj0TdBN2HaY2f6qD7jj0bYIC3O7Y8Pgw1ThH33eHg/D4kFant6nBUWifWvUQuetiHWM7s4ApHgZaTK5VP4VpnR/s9SdAUW30j8d/g2nlqjZ7cahQJPXdC/TtnZmfVLMyYuDjoG9scx5HjDpyL4JtN9jC8x7mZ85MPbOeb/0/7PinTJal2VpiqfscML+L1CyIaSJ0iW6DMOK7xVHv0E99Di72WG1QpwgCsUVT39ewK+H5oFEdH/cy8i/OWk4CCIDjPJBP0XOp4HtwirC6HTvkfyKqqEEPyEWP/qQaf4Jd5Ec/m3HOFnVgdP4d04+48QnA//mqf/yvDfnkGJRCKRSCQSiUQikUgkEolEIpFIJBKJRCKRSCQSiUQieS3/AZzjrH8362VjAAAAAElFTkSuQmCC',
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

let documentTags: ICollection<DocumentTag> = new Collection<DocumentTag>();
let storableDocumentTags = new StorableDocumentTag();
let localDocumentTags = new LocalStorage<DocumentTag>(DocumentTag);
documentTags = localDocumentTags.read(storableDocumentTags.getLocalStorage())
if(documentTags.size() == 0){
    documentTags.add(new DocumentTag('Tag 1'));
    documentTags.add(new DocumentTag('Tag 2'));
    localDocumentTags.write(storableDocumentTags.getLocalStorage(), documentTags);
}

let folders: ICollection<Folder> = new Collection<Folder>();
let api: ICollection<Folder> = new Collection<Folder>();
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
let apiFolders = folders.slice(0,5);

let certificates: ICollection<Certificate> = new Collection<Certificate>();
let storableCertificates = new StorableCertificate();
let localCertificates = new LocalStorage<Certificate>(Certificate);
certificates = localCertificates.read(storableCertificates.getLocalStorage())
if(certificates.size() == 0){
  certificates.add(new Certificate('Andrés Nava Carranza', 'Persona Física', new Date('Fri Jul 28 2023 13:38:29 GMT+0100'), new Date('Thu Aug 31 2025 03:52:27 GMT+0100'), 'And323', 'Público', false, true, false, '35532252N'));
  certificates.add(new Certificate('Andrea Casarez Saldaña', 'Persona Física', new Date('Tue Feb 28 2023 20:37:13 GMT+0100'), new Date('Thu May 23 2025 22:33:06 GMT+0100'), 'Andrea', 'Público', false, true, false, '94385657M'));
  certificates.add(new Certificate('María Elena Tirado Chacón', 'Persona Física', new Date('Wed Mar 22 2023 15:48:30 GMT+0100 '), new Date('Fri Aug 02 2025 12:43:24 GMT+0100'), 'Andrea', 'Público', false, true, false, '11556837G'));
  localCertificates.write(storableCertificates.getLocalStorage(), certificates);
}


let enterprises: ICollection<Enterprise> = new Collection<Enterprise>();
let storableEnterprises = new StorableEnterprise();
let storableRegistryEnterprises = new StorableRegistryEnterprise();
let registryEnterprises : ICollection<RegistryEnterprise> = new Collection<RegistryEnterprise>();
let localRegistryEnterprises = new LocalStorage<RegistryEnterprise>(RegistryEnterprise);
let localEnterprises = new LocalStorage<Enterprise>(Enterprise);
enterprises = localEnterprises.read(storableEnterprises.getLocalStorage())
registryEnterprises = localRegistryEnterprises.read(storableRegistryEnterprises.getLocalStorage());

if(enterprises.size() == 0){
    enterprises.add(new Enterprise('Pet Estudio', 'B16880148'));
    enterprises.add(new Enterprise('MENG SA', 'U14241855'));
    enterprises.add(new Enterprise('PORTABAGE SL', 'U53716270'));
    localEnterprises.write(storableEnterprises.getLocalStorage(), enterprises);
}

if (registryEnterprises.size() == 0){
    registryEnterprises.add(new RegistryEnterprise('B16880148', 'lorem ipsum ...', new Date(), new Date(), 'Esperanza', 'Protocolo 1', 'Inscripción 1'));
    registryEnterprises.add(new RegistryEnterprise('U14241855', 'lorem ipsum ...', new Date(), new Date(), 'Jorge Luis', 'Protocolo 2', 'Inscripción 2'));
    registryEnterprises.add(new RegistryEnterprise('U53716270', 'lorem ipsum ...', new Date(), new Date(), 'Manuela', 'Protocolo 3', 'Inscripción 3'));
    localRegistryEnterprises.write(storableRegistryEnterprises.getLocalStorage(), registryEnterprises);
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
    banks.add(new Bank('Caixa Bank',1500,'whereIsMyPath?','1','ES0402260523912937810527', new Date(), 'syncStatus'));
    banks.add(new Bank('Banco Nación',500,'whereIsMyPath?2','1','ES0402260523912937810527', new Date(), 'syncStatus'));
    banks.add(new Bank('Bankinter',2500,'whereIsMyPath?3','1','ES0402260523912937810527', new Date(), 'syncStatus'));
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
    taxModels.add(new TaxModel( '180','IVA','presentado','a','356',1,2022));
    taxModels.add(new TaxModel( '303','IVA','presentado','b','789',1,2022));
    taxModels.add(new TaxModel( '180','IVA','presentado','a','1245',2,2022));
    taxModels.add(new TaxModel( '303','IVA','presentado','b','1024',2,2022));
    taxModels.add(new TaxModel( '180','IVA','en proceso','a','1538',3,2022));
    taxModels.add(new TaxModel( '303','IVA','pendiente','b','987',3,2022));
    taxModels.add(new TaxModel( '130','IVA','presentado','b','189',3,2022));
    taxModels.add(new TaxModel( '347','IVA','rectificado','b','684',3,2022));
    taxModels.add(new TaxModel( '180','IVA','en proceso','a','1784',4,2022));
    taxModels.add(new TaxModel( '303','IVA','pendiente','a','1345',4,2022));
    taxModels.add(new TaxModel( '130','IVA','presentado','b','541',4,2022));
    taxModels.add(new TaxModel( '347','IVA','rectificado','a','1354',4,2022));

    localTaxModels.write(storableTaxModels.getLocalStorage(), taxModels);
}

let messages: ICollection<Message> = new Collection<Message>();
let storableMessages = new StorableMessage();
let localMessages = new LocalStorage<Message>(Message);
messages = localMessages.read(storableMessages.getLocalStorage())
if(messages.size() == 0){
    messages.add(new Message('Asesor1','Asunto 1','sunt in culpa qui officia deserunt',new Date("2023-06-12 12:00"),'consulta','abierta', new Date("2023-08-24 23:30"), false));
    messages.add(new Message('Asesor2','Asunto 1','sunt in culpa qui officia deserunt',new Date("2023-06-26 14:00"),'consulta','abierta', new Date("2023-08-24 23:30"), true));
    messages.add(new Message('Asesor3','Asunto 1','sunt in culpa qui officia deserunt',new Date("2023-07-16 15:00"),'consulta','cerrada', new Date("2023-08-24 23:30"), true));
    messages.add(new Message('Asesor1','Asunto 2','sunt in culpa qui officia deserunt',new Date("2023-05-16 12:30"),'notificacion','vista'));
    messages.add(new Message('Asesor2','Asunto 2','sunt in culpa qui officia deserunt',new Date("2023-06-17 18:02"),'notificacion','nueva'));
    messages.add(new Message('Asesor3','Asunto 2','sunt in culpa qui officia deserunt',new Date("2023-07-18 08:05"),'notificacion','nueva'));
    messages.add(new Message('Asesor1','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-06-03 10:01"),'tarea','realizada',new Date("2023-08-24 23:30")));
    messages.add(new Message('Asesor2','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-06-19 12:25"),'tarea','pendiente',new Date("2023-08-28 22:30")));
    messages.add(new Message('Asesor3','Asunto 3','sunt in culpa qui officia deserunt',new Date("2023-07-22 15:30"),'tarea','pendiente',new Date("2023-09-05 21:30")));
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
    messageChats.add(new MessageChat(filteredMessages.toArray()[0].Key,'Asesor1','sunt in culpa qui officia deserunt',new Date("2023-06-12 15:30"), 'enviado'));
    messageChats.add(new MessageChat(filteredMessages.toArray()[0].Key,'Asesor1','sunt in culpa qui officia deserunt',new Date("2023-06-12 15:31"), 'recibido'));
    messageChats.add(new MessageChat(filteredMessages.toArray()[0].Key,'Asesor1','sunt in culpa qui officia deserunt',new Date("2023-06-12 15:32"), 'recibido'));
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

let contracts: ICollection<Contract> = new Collection<Contract>();
let storableContracts = new StorableContract();
let localContracts = new LocalStorage<Contract>(Contract);

let marks: ICollection<Mark> = new Collection<Mark>();
let storableMarks = new StorableMark();
let localMarks = new LocalStorage<Mark>(Mark);

let marksDetails: ICollection<MarkDetail> = new Collection<MarkDetail>();
let storableMarksDetails = new StorableMarkDetail();
let localMarksDetails = new LocalStorage<MarkDetail>(MarkDetail);

let users: ICollection<User> = new Collection<User>();
let storableUsers = new StorableUser();
let localUsers = new LocalStorage<User>(User);
users = localUsers.read(storableUsers.getLocalStorage())
if(users.size() == 0){
  users.add(new User('Kathryn','Ledner','35532252N','kathrynledner@gmail.test','test','690619302',true));
  users.add(new User('Eusebio','González','94385657M','eusebiogonzalez@gmail.test','test','656796396',true));
  users.add(new User('Juan','Macejkovic','11556837G','juanmacejkovic@gmail.test','test','619068048',true));
  users.add(new User('Admin','Admin','12345678A','admin','admin','698475145',true));
  users.add(new User('Test','Test','98765432B','test@aonsolutions.test','test','652145784',true));
  localUsers.write(storableUsers.getLocalStorage(), users);
}


/*
    TESTING API FUNCTIONS
*/

if(test){

    /*
        TEST FOR DOCUMENTS
    */
    
    let documentFactory = new DocumentFactory();
    let filterBuider =  new FilterBuilder();
    filterBuider.addField('path','/a_contabilizar');
    documentFactory.createMultipleObjectCrud().getCollection(filterBuider.getFilter()).then((response) => {
        console.log('TEST DOCUMENTS GET LIST A_CONTABILIZAR', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENTS GET LIST A_CONTABILIZAR', error)
    })
    filterBuider.clearAll();
    filterBuider.addField('path','/contabilizado');
    documentFactory.createMultipleObjectCrud().getCollection(filterBuider.getFilter()).then((response) => {
        console.log('TEST DOCUMENTS GET LIST CONTABILIZADO(*)', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENTS GET LIST CONTABILIZADO(*)', error)
    })
    filterBuider.clearAll();
    filterBuider.addField('path','/papelera');
    documentFactory.createMultipleObjectCrud().getCollection(filterBuider.getFilter()).then((response) => {
        console.log('TEST DOCUMENTS GET LIST PAPELERA', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENTS GET LIST PAPELERA', error)
    })
    filterBuider.clearAll();
    filterBuider.addField('path','/fiscal');
    documentFactory.createMultipleObjectCrud().getCollection(filterBuider.getFilter()).then((response) => {
        console.log('TEST DOCUMENTS GET LIST FISCAL',response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENTS GET LIST FISCAL', error)
    })
    filterBuider.clearAll();
    filterBuider.addField('path','/laboral/51198000T');
    documentFactory.createMultipleObjectCrud().getCollection(filterBuider.getFilter()).then((response) => {
        console.log('TEST DOCUMENTS GET LIST LABORAL', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR DOCUMENTS TEST GET LIST LABORAL', error)
    })

    /**
        TEST FOR DOCUMENT_TAGS
     */
    
    let tagFactory = new DocumentTagFactory();
    tagFactory.createMultipleObjectCrud().getCollection().then((response) => {
        console.log('TEST DOCUMENT_TAG GET LIST', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENT_TAG GET LIST', error)
    })
    let tag = new DocumentTag();
    tag.Name = 'Tag 1';
    tagFactory.createSingleObjectCrud().createElement(tag).then((response) => {
        console.log('TEST DOCUMENT_TAG CREATE', response.result);
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENT_TAG CREATE', error)
    })

    tagFactory.createMultipleObjectCrud().getCollection().then((response) => {
        tagFactory.createSingleObjectCrud().updateElement(response.result.toArray()[0]).then((response) => {
        console.log('TEST DOCUMENT_TAG UPDATE', response.result);
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENT_TAG UPDATE', error)
    })
    }).catch((error) => {
        console.log('ERROR TEST DOCUMENT_TAG UPDATE', error)
    })
    
    /*
        TEST FOR ENTERPRISE
    */
    
    let enterpriseFactory = new EnterpriseFactory();
    enterpriseFactory.createMultipleObjectCrud().getCollection().then((response) => {
        console.log('TEST GET ENTERPRISES', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST GET ENTERPRISES', error)
    })
    enterpriseFactory.createSingleObjectCrud().getElement('702378').then((element) => {
        console.log('TEST GET ENTERPRISE BY ID', element.result);
    }).catch((error) => {
        console.log('ERROR TEST GET ENTERPRISE BY ID', error)
    })
    
    
    /*
        TEST FOR MESSAGES
    */
    
    let filterMessage = new FilterBuilder();
    let messageFactory = new MessageFactory();
    filterMessage.addField('type','consulta');
    messageFactory.createMultipleObjectCrud().getCollection(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGES GET CONSULTA', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST MESSAGES GET CONSULTA', error)
    })
    filterMessage.clearAll();
    filterMessage.addField('type','tarea');
    messageFactory.createMultipleObjectCrud().getCollection(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGES GET TAREA', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST MESSAGES GET TAREA', error)
    })
    filterMessage.clearAll();
    filterMessage.addField('type','notificacion');
    messageFactory.createMultipleObjectCrud().getCollection(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGES GET NOTIFICACION', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST MESSAGES GET NOTIFICATION', error)
    })
    filterMessage.clearAll();
    messageFactory.createMultipleObjectCrud().getCollection(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGES GET ALL', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST MESSAGES GET ALL', error)
    })
    filterMessage.clearAll();
    filterMessage.addField('type','consulta');
    messageFactory.createMultipleObjectCrud().getCollection(filterMessage.getFilter()).then((response) => {
        messageFactory.createSingleObjectCrud().getElement(response.result.toArray()[0].Id).then((element) => {
            console.log('TEST MESSAGE GET ONE ', element.result);
        }).catch((error) => {
            console.log('ERROR TEST MESSAGE GET ONE ', error)    
        })
    }).catch((error) => {
        console.log('ERROR TEST GET ONE MESSAGE', error)
    })
    filterMessage.clearAll();
    filterMessage.addField('type','consulta');
    messageFactory.createMultipleObjectCrud().getCollection(filterMessage.getFilter()).then((response) => {
        messageFactory.createMessageSpecificMethods().archiveMessage(response.result.toArray()[0]).then((response) => {
            console.log('TEST MESSAGE ARCHIVE',response);
        }).catch((error) => {
            console.log('ERROR TEST MESSAGE ARCHIVE',error);
        })
        messageFactory.createMessageSpecificMethods().reopenMessage(response.result.toArray()[0]).then((response) => {
            console.log('TEST MESSAGE REOPEN',response);
        }).catch((error) => {
            console.log('ERROR TEST MESSAGE REOPEN',error);
        })
    })
    filterMessage.clearAll();
    filterMessage.addField('type','tarea');
    messageFactory.createMessageSpecificMethods().getMessageCount(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGE COUNT TASK',response.result)
    }).catch((error) => {
        console.log(error)
    })
    filterMessage.clearAll();
    filterMessage.addField('type','consulta');
    messageFactory.createMessageSpecificMethods().getMessageCount(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGE COUNT QUERY',response.result)
    }).catch((error) => {
        console.log(error)
    })
    filterMessage.clearAll();
    filterMessage.addField('type','notificacion');
    messageFactory.createMessageSpecificMethods().getMessageCount(filterMessage.getFilter()).then((response) => {
        console.log('TEST MESSAGE COUNT NOTIFICATION',response.result)
    }).catch((error) => {
        console.log(error)
    })
    filterMessage.clearAll();
    messageFactory.createMessageSpecificMethods().getMessageCount().then((response) => {
        console.log('TEST MESSAGE COUNT ALL',response.result)
    }).catch((error) => {
        console.log(error)
    })
    let taskHolderTest = new TaskHolder();
    taskHolderTest.Name = 'Name'
    taskHolderTest.Id = '713845'
    let testMessage = new Message();
    testMessage.Date = new Date();
    testMessage.Description = 'sunt in culpa qui officia deserunt'
    testMessage.EndDate = new Date();
    testMessage.Name = taskHolderTest.Name
    testMessage.Status = 'pendiente'
    testMessage.Title = 'titulo de ejemplo'
    testMessage.Type = 'tarea'
    testMessage.TaskHolder = taskHolderTest;

    let testMessageCreate = new MessageFactory();
    testMessageCreate.createSingleObjectCrud().createElement(testMessage).then((response) => {
        console.log('TEST MESSAGE CREATE', response.result);
    }).catch((error) => {
        console.log('ERROR TEST MESSAGE CREATE', error)
    })
    let notification = new Message();
    notification.Type = 'notificacion';
    notification.Id = '29500';
    messageFactory.createMessageSpecificMethods().markAsReadNotification(notification).then((response) => {
        console.log('TEST MESSAGE MARK AS READ NOTIFICATION', response.result);
    }).catch((error) => {
        console.log('ERROR TEST MESSAGE MARK AS READ NOTIFICATION', error)
    })


    /*
        TEST FOR MESSAGE CHAT
    */
    
    let filterMessageChat = new FilterBuilder();
    let filterMessage2 = new FilterBuilder();
    let messageChatFactory = new MessageChatFactory();
    let messageFactory2 = new MessageFactory();
    filterMessage2.addField('type','consulta');
    messageFactory2.createMultipleObjectCrud().getCollection(filterMessage2.getFilter()).then((response) => {
        //response.result.toArray()[0].Id
        filterMessageChat.addField('idMessage', '29475');
        messageChatFactory.createMultipleObjectCrud().getCollection(filterMessageChat.getFilter()).then((response) => {
            console.log('TEST MESSAGECHAT GET LIST', response.result.toArray());
        }).catch((error) => {
            console.log('ERROR TEST MESSAGECHAT GET LIST', error)
        })
    })

    let messageChatCreate = new MessageChat();
    messageChatCreate.IdMessage = '29490';
    messageChatCreate.Description = 'sunt in culpa qui officia deserunt';
    messageChatFactory.createSingleObjectCrud().createElement(messageChatCreate).then((response) => {
        console.log('TEST MESSAGECHAT CREATE', response.result);
    }).catch((error) => {
        console.log('ERROR TEST MESSAGECHAT CREATE', error)
    })

    /*
        TEST FOR TASKHOLDERS
    */

    let taskHolderFactory = new TaskHolderFactory();
    taskHolderFactory.createMultipleObjectCrud().getCollection().then((response) => {
        console.log('TEST TASKHOLDERS GET LIST', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST TASKHOLDERS GET LIST', error)
    })
    taskHolderFactory.createSingleObjectCrud().getElement('702381').then((response) => {
        console.log('TEST TASKHOLDERS GET ONE', response.result);
    }).catch((error) => {
        console.log('ERROR TEST TASKHOLDERS GET ONE', error)
    })
    
    
    
    /*
        TEST FOR TAXMODELS
    */
    
    let taxFactory = new TaxModelFactory();
    let filterTax = new FilterBuilder();
    filterTax.addField('trimester','T3')
    taxFactory.createMultipleObjectCrud().getCollection(filterTax.getFilter()).then((response) => {
        console.log('TEST TAXMODELS GET LIST', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST TAXMODELS GET LIST', error)
    })
    
    /*
        TEST FOR BANKS
    */
    
    let bankFactory = new BankFactory();
    bankFactory.createMultipleObjectCrud().getCollection().then((response) => {
        console.log('TEST BANKS GET LIST', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST BANKS GET LIST', error)
    })
    
    
    /*
        TEST FOR FOLDERS
    */
    
    let folderFactory = new FolderFactory();
    folderFactory.createMultipleObjectCrud().getCollection().then((response) =>  {
        console.log('TEST FOLDERS GET LIST', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST FOLDERS GET LIST', error)
    })

    /**
        TEST FOR MARKS
     */

    let markFactory = new MarkFactory();
    let markFilter = new FilterBuilder();
    markFilter.addField('idUser', localStorage.getItem('login'))
    
    markFactory.createMultipleObjectCrud().getCollection().then((response) => {
        console.log('TEST MARKS GET LIST', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST MARKS GET LIST', error)
    })
    
    markFactory.createSpecificMethods().getMarksOfOneUser(localStorage.getItem('registry') || '', markFilter.getFilter()).then((response) => {
        console.log('TEST MARKS GET LIST OF ONE USER', response.result.toArray());
    }).catch((error) => {
        console.log('ERROR TEST MARKS GET LIST OF ONE USER', error)
    })
}



/*
    LAMBDA FUNCTION AMAZON
*/

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