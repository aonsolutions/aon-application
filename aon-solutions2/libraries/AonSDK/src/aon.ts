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

/**
 * FACTORYS EXPORTS
 */
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

/**
 * EXPORTS UTILS
 */
export { Response } from "./utils/Response";
export { ErrorResponse } from "./utils/Response";
export { FilterBuilder } from "./utils/FilterBuilder";

import { generateData } from "./utils/GenerateFakeData";
generateData();