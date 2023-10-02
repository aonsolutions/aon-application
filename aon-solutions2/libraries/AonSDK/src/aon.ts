/**
 * GLOBAL EXPORTS
 */
export { AuthFunctions } from "./exportFunctions/AuthFunctions";
export { BankFunctions } from "./exportFunctions/BankFunctions";
export { CertificateFunctions } from "./exportFunctions/CertificateFunctions";
export { ContactFunctions } from "./exportFunctions/ContactFunctions";
export { DocumentFunctions } from "./exportFunctions/DocumentFunctions";
export { EmployeeFunctions } from "./exportFunctions/EmployeeFunctions";
export { EnterpriseFunctions } from "./exportFunctions/EnterpriseFunctions";
export { FolderFunctions } from "./exportFunctions/FolderFunctions";
export { MarkFunctions } from "./exportFunctions/MarkFunctions";
export { MessageChatFunctions } from "./exportFunctions/MessageChatFunctions";
export { MessageFunctions } from "./exportFunctions/MessageFunctions";
export { ProductFunctions } from "./exportFunctions/ProductFunctions";
export { RegistryEnterpriseFunctions } from "./exportFunctions/RegistryEnterpriseFunctions";
export { ReportingFunctions } from "./exportFunctions/ReportingFunctions";
export { TaxModelFunctions } from "./exportFunctions/TaxModelFunctions";
export { UserFunctions } from "./exportFunctions/UserFunctions";
/**
 * INTERFACES EXPORTS
 */
export { IEnterprise } from "./interfaces/modelsInterfaces";
export { IDocument } from "./interfaces/modelsInterfaces";
export { IBank } from "./interfaces/modelsInterfaces";
export { ITaxModel } from "./interfaces/modelsInterfaces";
export { IFolder } from "./interfaces/modelsInterfaces";
export { IMessage } from "./interfaces/modelsInterfaces";
export { IUser } from "./interfaces/modelsInterfaces";
export { IMessageChat } from "./interfaces/modelsInterfaces";
export { IEmployee } from "./interfaces/modelsInterfaces";
export { IContract } from "./interfaces/modelsInterfaces";
export { ICertificate } from "./interfaces/modelsInterfaces";
export { IDocumentNote } from "./interfaces/modelsInterfaces";
export { ITax } from "./interfaces/modelsInterfaces";
export { IProductCode } from "./interfaces/modelsInterfaces";
export { IProductType } from "./interfaces/modelsInterfaces";
export { IInvoiceSerie } from "./interfaces/modelsInterfaces";
export { IProductClass } from "./interfaces/modelsInterfaces";
export { IIRPF } from "./interfaces/modelsInterfaces";
export { Eng } from "./interfaces/modelsInterfaces";
export { statusTaxModel } from "./interfaces/modelsInterfaces";
export { IPaymentMethod } from "./interfaces/modelsInterfaces";
export { IRegistryEnterprise } from "./interfaces/modelsInterfaces";
export { Demonyms } from "./interfaces/modelsInterfaces";
export { IResponse } from "./interfaces/utilitiesInterfaces";
export { ICollection } from "./interfaces/utilitiesInterfaces";
export { IFilter } from "./interfaces/utilitiesInterfaces";
export { ITaskHolder } from "./interfaces/modelsInterfaces";
export { IInvoice } from "./interfaces/modelsInterfaces";
export { IInvoiceCategory } from "./interfaces/modelsInterfaces";
export { IInvoiceTransactionType } from "./interfaces/modelsInterfaces";
export { IInvoiceActivity } from "./interfaces/modelsInterfaces";
export { IContact } from "./interfaces/modelsInterfaces";
export { IMark } from "./interfaces/modelsInterfaces";
export { IProduct } from "./interfaces/modelsInterfaces";
export { IProductCategory } from "./interfaces/modelsInterfaces";
export { IProductStatus } from "./interfaces/modelsInterfaces";
export { StatusMessage } from "./interfaces/modelsInterfaces";
export { TypeMessage } from "./interfaces/modelsInterfaces";
/**
 * FACTORYS EXPORTS
*/
export { ContactFactory } from "./factorys/ContactFactory";
export { DocumentFactory } from "./factorys/DocumentFactory";
export { DocumentTagFactory } from "./factorys/DocumentTagFactory";
export { EnterpriseFactory } from "./factorys/EnterpriseFactory";
export { RegistryEnterpriseFactory } from "./factorys/RegistryEnterpriseFactory";
export { DocumenNoteFactory } from "./factorys/DocumentNoteFactory";
export { CollectionFactory } from "./factorys/CollectionFactory";
export { UserFactory } from "./factorys/UserFactory";
export { BankFactory } from "./factorys/BankFactory";
export { TaxModelFactory } from "./factorys/TaxModelFactory";
export { TaxFactory } from "./factorys/TaxFactory";
export { FolderFactory } from "./factorys/FolderFactory";
export { MessageFactory } from "./factorys/MessageFactory";
export { MessageChatFactory } from "./factorys/MessageChatFactory";
export { CertificateFactory } from "./factorys/CertificateFactory";
export { InvoiceFactory } from "./factorys/InvoiceFactory";
export { PaymentMethodFactory } from "./factorys/PaymentMethodFactory";
export { InvoiceSerieFactory } from "./factorys/InvoiceSerieFactory";
export { ProductCodeFactory } from "./factorys/ProductCodeFactory";
export { ProductTypeFactory } from "./factorys/ProductTypeFactory";
export { MarkFactory } from "./factorys/MarkFactory";
export { ContractFactory } from "./factorys/ContractFactory";
export { ProductClassFactory } from "./factorys/ProductClassFactory";
export { IRPFFactory } from "./factorys/IRPFFactory";
export { AuthenticationFactory } from "./factorys/AuthenticationFactory";
export { Factory } from "./factorys/ModelFactory";
export { TaskHolderFactory } from "./factorys/TaskHolderFactory";
export { ReportingFactory } from "./factorys/ReportingFactory";
export { ICountry } from "./interfaces/modelsInterfaces";
export { ICommunity } from "./interfaces/modelsInterfaces";
export { EmployeeFactory } from "./factorys/EmployeeFactory";
export { ProductFactory } from "./factorys/ProductFactory";

/**
 * EXPORTS UTILS
 */
export { Response } from "./utils/Response";
export { ErrorResponse } from "./utils/Response";
export { FilterBuilder } from "./utils/FilterBuilder";
import { StatusMessage } from "./interfaces/modelsInterfaces";
import { generateData } from "./utils/GenerateFakeData";
// import { test } from "./utils/Environment";
// import { makeTest } from "./tests/genericTest";

generateData();

// if(test) makeTest();