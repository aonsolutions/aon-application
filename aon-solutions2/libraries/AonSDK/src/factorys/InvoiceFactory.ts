import { ISingleObjectCrudFactory, IMultipleObjectCrudFactory } from "../interfaces/factoryInterfaces";
import { IInvoice } from "../interfaces/modelsInterfaces";
import { IMultipleObjectReader, ISingleObjectCrud, IInvoiceSpecificMethods } from "../interfaces/serviceInterfaces";
import { Invoice, StorableInvoice } from "../models/Invoice";
import { LocalInvoiceSpecificMethodsRepository } from "../repositorys/InvoiceRepository";
import { GenericMultipleObjectCrudRepository, GenericSingleObjectCrudRepository } from "../repositorys/GenericRepository";
import { GenericMultipleObjectCrud, GenericSingleObjectCrud } from "../services/GenericCrudService";
import { InvoiceSpecificMethods } from "../services/InvoiceService";

export class InvoiceFactory implements ISingleObjectCrudFactory<IInvoice>, IMultipleObjectCrudFactory<IInvoice> {
    createMultipleObjectCrud(): IMultipleObjectReader<IInvoice> {
        return new GenericMultipleObjectCrud<Invoice>(new GenericMultipleObjectCrudRepository<Invoice>(new StorableInvoice(), Invoice), Invoice);
    }

    createSingleObjectCrud(): ISingleObjectCrud<IInvoice> {
        return new GenericSingleObjectCrud<Invoice>(new GenericSingleObjectCrudRepository<Invoice>(new StorableInvoice(), Invoice), Invoice);
    }

    createSpecificMethods(): IInvoiceSpecificMethods {
        return new InvoiceSpecificMethods(new LocalInvoiceSpecificMethodsRepository());
    }
}