import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IInvoiceCategory } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { InvoiceCategory, StorableInvoiceCategory } from "../models/InvoiceCategory";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class InvoiceCategoryFactory implements IMultipleObjectCrudFactory<IInvoiceCategory> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceCategory> {
        return new GenericMultipleObjectCrud<InvoiceCategory>(new GenericMultipleObjectCrudRepository<InvoiceCategory>(new StorableInvoiceCategory(), InvoiceCategory), InvoiceCategory);
    }
}