import { IInvoiceActivity, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class InvoiceActivity extends SimpleValue implements IInvoiceActivity, IModel {}

export class StorableInvoiceActivity implements IStorable<InvoiceActivity> {
    getCollection(): ICollection<InvoiceActivity> {
        return invoiceActivities;
    }
    getLocalStorage(): string {
        return 'invoiceActivities';
    }
}

export let invoiceActivities: ICollection<InvoiceActivity> = new Collection<InvoiceActivity>();
export function setInvoiceActivities(value: any) { invoiceActivities = value; };