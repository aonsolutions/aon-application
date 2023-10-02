import { IProductCategory, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class ProductCategory extends SimpleValue implements IProductCategory, IModel{}

export class StorableProductCategory implements IStorable<ProductCategory> {
    getCollection(): ICollection<ProductCategory> {
        return productCategories;
    }
    getLocalStorage(): string {
        return 'productCategories';
    }
}

export let productCategories: ICollection<ProductCategory> = new Collection<ProductCategory>();
export function setProductCategories(value: any) { productCategories = value; };