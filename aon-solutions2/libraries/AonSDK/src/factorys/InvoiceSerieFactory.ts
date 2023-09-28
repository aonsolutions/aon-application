import { IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IInvoiceSerie } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader } from "../interfaces/serviceInterfaces";
import { InvoiceSerie, StorableInvoiceSerie } from "../models/InvoiceSerie";
import { GenericMultipleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud } from "../services/GenericCrudService";

export class InvoiceSerieFactory implements IMultipleObjectCrudFactory<IInvoiceSerie> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoiceSerie> {
        return new GenericMultipleObjectCrud<InvoiceSerie>(new GenericMultipleObjectCrudRepository<InvoiceSerie>(new StorableInvoiceSerie(), InvoiceSerie), InvoiceSerie);
    }
}