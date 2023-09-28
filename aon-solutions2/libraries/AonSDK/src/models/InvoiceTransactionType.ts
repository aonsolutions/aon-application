import { IInvoiceTransactionType, IModel, IStorable, IApiModel } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class InvoiceTransactionType extends SimpleValue implements IInvoiceTransactionType, IModel {}

export class StorableInvoiceTransactionType implements IStorable<InvoiceTransactionType> {
    getCollection(): ICollection<InvoiceTransactionType> {
        return invoiceTransactionTypes;
    }
    getLocalStorage(): string {
        return 'invoiceTransactionTypes';
    }
}

export class ApiInvoiceTransactionType implements IApiModel {
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

export let invoiceTransactionTypes: ICollection<InvoiceTransactionType> = new Collection<InvoiceTransactionType>();
export function setInvoiceTransactionTypes(value: any) { invoiceTransactionTypes = value; };