import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IProductCategory } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { ProductCategory, StorableProductCategory } from "../models/ProductCategory";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ProductCategoryFactory implements IMultipleObjectCrudFactory<IProductCategory> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductCategory> {
        return new GenericMultipleObjectCrud<ProductCategory>(new GenericMultipleObjectCrudRepository<ProductCategory>(new StorableProductCategory(), ProductCategory), ProductCategory);
    }
}