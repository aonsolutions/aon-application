import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { ITax } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { Tax, StorableTax } from "../models/Tax";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class TaxFactory implements IMultipleObjectCrudFactory<ITax> {
    createMultipleObjectCrud(): IMultipleObjectReader<ITax> {
        return new GenericMultipleObjectCrud<Tax>(new GenericMultipleObjectCrudRepository<Tax>(new StorableTax(), Tax), Tax);
    }
}