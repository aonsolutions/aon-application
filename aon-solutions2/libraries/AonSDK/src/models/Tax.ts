import { ITax, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class Tax extends SimpleValue implements ITax, IModel {}

export class StorableTax implements IStorable<Tax> {
    getCollection(): ICollection<Tax> {
        return taxes;
    }
    getLocalStorage(): string {
        return 'taxes';
    }
}

export let taxes: ICollection<Tax> = new Collection<Tax>();
export function setTax(value: any) { taxes = value; };