import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IProductStatus } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { ProductStatus, StorableProductStatus } from "../models/ProductStatus";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ProductStatusFactory implements IMultipleObjectCrudFactory<IProductStatus> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductStatus> {
        return new GenericMultipleObjectCrud<ProductStatus>(new GenericMultipleObjectCrudRepository<ProductStatus>(new StorableProductStatus(), ProductStatus), ProductStatus);
    }
}