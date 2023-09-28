import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IInvoiceTransactionType } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { InvoiceTransactionType, StorableInvoiceTransactionType } from "../models/InvoiceTransactionType";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class InvoiceTransactionTypeFactory implements IMultipleObjectCrudFactory<IInvoiceTransactionType> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceTransactionType> {
        return new GenericMultipleObjectCrud<InvoiceTransactionType>(new GenericMultipleObjectCrudRepository<InvoiceTransactionType>(new StorableInvoiceTransactionType(), InvoiceTransactionType), InvoiceTransactionType);
    }
}