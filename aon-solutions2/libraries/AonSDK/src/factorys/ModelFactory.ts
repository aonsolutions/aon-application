import { IFactory, IProduct, ITax, IIRPF, IInvoiceLine, IPaymentMethod, IInvoiceExpirationLine, IInvoiceSerie, IContact, IInvoiceCategory, IInvoiceTransactionType, IInvoiceActivity, InvoiceType, IInvoice, IProductCode, IProductCategory, IProductClass, IProductType, IProductStatus, IDocument, IFolder, ICertificate, IEnterprise, IRegistryEnterprise, IDocumentNote, IBank, statusTaxModel, ITaxModel, TypeMessage, StatusMessage, IMessage, IMessageChat, IEmployee, IUser } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Bank } from "../models/Bank";
import { Certificate } from "../models/Certificate";
import { Contact } from "../models/Contact";
import { DocumentNote } from "../models/DocumentNote";
import { Employee } from "../models/Employee";
import { Enterprise } from "../models/Enterprise";
import { Folder } from "../models/Folder";
import { Invoice } from "../models/Invoice";
import { InvoiceExpirationLine } from "../models/InvoiceExpirationLine";
import { InvoiceLine } from "../models/InvoiceLine";
import { Message } from "../models/Message";
import { MessageChat } from "../models/MessageChat";
import { Product } from "../models/Product";
import { RegistryEnterprise } from "../models/RegistryEnterprise";
import { TaxModel } from "../models/TaxModel";
import { User } from "../models/User";
import { Document } from "../models/Document";

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

    createMessage(name?: string, title?: string, description?: string, date?: Date, type?: TypeMessage, status?: StatusMessage, endDate?: Date): IMessage {
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