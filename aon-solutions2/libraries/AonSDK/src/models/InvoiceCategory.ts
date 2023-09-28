import { IInvoiceCategory, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class InvoiceCategory extends SimpleValue implements IInvoiceCategory, IModel {}

export class StorableInvoiceCategory implements IStorable<InvoiceCategory> {
    getCollection(): ICollection<InvoiceCategory> {
        return invoiceCategories;
    }
    getLocalStorage(): string {
        return 'invoiceCategories';
    }
}

export let invoiceCategories: ICollection<InvoiceCategory> = new Collection<InvoiceCategory>();
export function setInvoiceCategories(value: any) { invoiceCategories = value; };