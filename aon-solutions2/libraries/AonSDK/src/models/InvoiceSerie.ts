import { IInvoiceSerie, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class InvoiceSerie extends SimpleValue implements IInvoiceSerie, IModel {}

export class StorableInvoiceSerie implements IStorable<InvoiceSerie> {
    getCollection(): ICollection<InvoiceSerie> {
        return invoiceSeries;
    }
    getLocalStorage(): string {
        return 'invoiceSeries';
    }
}

export let invoiceSeries: ICollection<InvoiceSerie> = new Collection<InvoiceSerie>();
export function setInvoiceSeries(value: any) { invoiceSeries = value; };