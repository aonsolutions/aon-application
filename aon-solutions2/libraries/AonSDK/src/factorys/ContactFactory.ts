import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IContact } from "../interfaces/modelsInterfaces";
import { ISingleObjectCrud, IMultipleObjectCrud } from "../interfaces/serviceInterfaces";
import { Contact, StorableContact } from "../models/Contact";
import { GenericSingleObjectCrudRepository, GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericSingleObjectCrud, GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class ContactFactory implements ISingleObjectCrudFactory<IContact>, IMultipleObjectCrudFactory<IContact> {
    createSingleObjectCrud(): ISingleObjectCrud<IContact> {
        return new GenericSingleObjectCrud<Contact>(new GenericSingleObjectCrudRepository<Contact>(new StorableContact(), Contact), Contact);
    }
    createMultipleObjectCrud(): IMultipleObjectCrud<IContact> {
        return new GenericMultipleObjectCrud<Contact>(new GenericMultipleObjectCrudRepository<Contact>(new StorableContact(), Contact), Contact);
    }
}