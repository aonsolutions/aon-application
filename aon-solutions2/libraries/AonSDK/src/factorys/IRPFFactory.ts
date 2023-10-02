import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IIRPF } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { IRPF, StorableIRPF } from "../models/IRPF";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class IRPFFactory implements IMultipleObjectCrudFactory<IIRPF> {
    createMultipleObjectCrud(): IMultipleObjectReader<IIRPF> {
        return new GenericMultipleObjectCrud<IRPF>(new GenericMultipleObjectCrudRepository<IRPF>(new StorableIRPF(), IRPF), IRPF);
    }
}