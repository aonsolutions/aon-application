import { IProductCode, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class ProductCode extends SimpleValue implements IProductCode, IModel{}

export class StorableProductCode implements IStorable<ProductCode> {
    getCollection(): ICollection<ProductCode> {
        return productCodes;
    }
    getLocalStorage(): string {
        return 'productCodes';
    }
}

export let productCodes: ICollection<ProductCode> = new Collection<ProductCode>();
export function setProductCodes(value: any) { productCodes = value; };