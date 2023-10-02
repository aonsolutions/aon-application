import { ICollectionFactory, IInvoice, IProduct, IContact, IDocument, IFolder, ICertificate, IEnterprise, IRegistryEnterprise, IDocumentNote, IBank, ITaxModel, IMessage, IMessageChat, IEmployee, IMark, IUser, IContract } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Bank } from "../models/Bank";
import { Certificate } from "../models/Certificate";
import { Contact } from "../models/Contact";
import { Contract } from "../models/Contract";
import { DocumentNote } from "../models/DocumentNote";
import { Employee } from "../models/Employee";
import { Enterprise } from "../models/Enterprise";
import { Folder } from "../models/Folder";
import { Invoice } from "../models/Invoice";
import { Mark } from "../models/Mark";
import { Message } from "../models/Message";
import { MessageChat } from "../models/MessageChat";
import { Product } from "../models/Product";
import { RegistryEnterprise } from "../models/RegistryEnterprise";
import { TaxModel } from "../models/TaxModel";
import { User } from "../models/User";
import { Collection } from "../utils/Collection";
import { Document } from "../models/Document";

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