import { IProductStatus, IModel, IStorable } from "../interfaces/modelsInterfaces";
import { ICollection } from "../interfaces/utilitiesInterfaces";
import { Collection } from "../utils/Collection";
import { SimpleValue } from "./SimpleValue";

export class ProductStatus extends SimpleValue implements IProductStatus, IModel{}

export class StorableProductStatus implements IStorable<ProductStatus> {
    getCollection(): ICollection<ProductStatus> {
        return productStatuses;
    }
    getLocalStorage(): string {
        return 'productStatuses';
    }
}

export let productStatuses: ICollection<ProductStatus> = new Collection<ProductStatus>();
export function setProductStatuses(value: any) { productStatuses = value; };