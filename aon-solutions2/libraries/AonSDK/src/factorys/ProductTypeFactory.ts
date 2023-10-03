import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IProductType } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { ProductType, StorableProductType } from "../models/ProductType";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ProductTypeFactory implements IMultipleObjectCrudFactory<IProductType> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductType> {
        return new GenericMultipleObjectCrud<ProductType>(new GenericMultipleObjectCrudRepository<ProductType>(new StorableProductType(), ProductType), ProductType);
    }
}