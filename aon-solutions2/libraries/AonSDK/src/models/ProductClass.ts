import { IProductClass, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class ProductClass extends SimpleValue implements IProductClass, IModel{}

export class StorableProductClass implements IStorable<ProductClass> {
    getCollection(): ICollection<ProductClass> {
        return productClasses;
    }
    getLocalStorage(): string {
        return 'productClasses';
    }
}

export let productClasses: ICollection<ProductClass> = new Collection<ProductClass>();
export function setProductClasses(value: any) { productClasses = value; };