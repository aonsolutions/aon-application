import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IProductClass } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { ProductClass, StorableProductClass } from "../models/ProductClass";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ProductClassFactory implements IMultipleObjectCrudFactory<IProductClass> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductClass> {
        return new GenericMultipleObjectCrud<ProductClass>(new GenericMultipleObjectCrudRepository<ProductClass>(new StorableProductClass(), ProductClass), ProductClass);
    }
}