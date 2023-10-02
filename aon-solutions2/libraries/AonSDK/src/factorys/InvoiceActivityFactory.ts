import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IInvoiceActivity } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { InvoiceActivity, StorableInvoiceActivity } from "../models/InvoiceActivity";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class InvoiceActivityFactory implements IMultipleObjectCrudFactory<IInvoiceActivity> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceActivity> {
        return new GenericMultipleObjectCrud<InvoiceActivity>(new GenericMultipleObjectCrudRepository<InvoiceActivity>(new StorableInvoiceActivity(), InvoiceActivity), InvoiceActivity);
    }
}