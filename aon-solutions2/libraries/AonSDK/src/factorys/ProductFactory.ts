import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IProduct } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud, IProductSpecificMethods } from "../interfaces/serviceInterfaces";
import { Product, StorableProduct } from "../models/Product";
import { GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { LocalProductSpecificMethodsRepository } from "../repositorys/ProductRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";
import { ProductSpecificMethods } from "../services/ProductService";

export class ProductFactory implements ISingleObjectCrudFactory<IProduct>, IMultipleObjectCrudFactory<IProduct> {
    createSingleObjectCrud(): ISingleObjectCrud<IProduct> {
        return new GenericSingleObjectCrud<Product>(new GenericSingleObjectCrudRepository<Product>(new StorableProduct(), Product), Product);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IProduct> {
        return new GenericMultipleObjectCrud<Product>(new GenericMultipleObjectCrudRepository<Product>(new StorableProduct(), Product), Product);
    }
    createSpecificMethods(): IProductSpecificMethods {
        return new ProductSpecificMethods(new LocalProductSpecificMethodsRepository());
    }
}