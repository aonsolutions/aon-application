import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IProductCode } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { ProductCode, StorableProductCode } from "../models/ProductCode";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ProductCodeFactory implements IMultipleObjectCrudFactory<IProductCode> {
    createMultipleObjectCrud(): IMultipleObjectReader<IProductCode> {
        return new GenericMultipleObjectCrud<ProductCode>(new GenericMultipleObjectCrudRepository<ProductCode>(new StorableProductCode(), ProductCode), ProductCode);
    }
}