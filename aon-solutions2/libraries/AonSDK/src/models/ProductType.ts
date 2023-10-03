import { IProductType, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class ProductType extends SimpleValue implements IProductType, IModel{}

export class StorableProductType implements IStorable<ProductType> {
    getCollection(): ICollection<ProductType> {
        return productTypes;
    }
    getLocalStorage(): string {
        return 'productTypes';
    }
}

export let productTypes: ICollection<ProductType> = new Collection<ProductType>();
export function setProductTypes(value: any) { productTypes = value; };