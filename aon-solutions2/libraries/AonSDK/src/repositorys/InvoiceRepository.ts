import { IRPFFactory } from "../factorys/IRPFFactory";
import { InvoiceActivityFactory } from "../factorys/InvoiceActivityFactory";
import { InvoiceCategoryFactory } from "../factorys/InvoiceCategoryFactory";
import { InvoiceSerieFactory } from "../factorys/InvoiceSerieFactory";
import { InvoiceTransactionTypeFactory } from "../factorys/InvoiceTransactionTypeFactory";
import { PaymentMethodFactory } from "../factorys/PaymentMethodFactory";
import { TaxFactory } from "../factorys/TaxFactory";
import { ITax, IIRPF, IInvoiceCategory, IInvoiceSerie, IInvoiceTransactionType } from "../interfaces/modelsInterfaces";
import { IInvoiceSpecificMethodsRepository } from "../interfaces/repositoryInterfaces";
import { IFilter, ICollection } from "../interfaces/utilitiesInterfaces";

export class LocalInvoiceSpecificMethodsRepository implements IInvoiceSpecificMethodsRepository {
    async getTaxList(filter?: IFilter | undefined): Promise<ICollection<ITax>> {
        return (await new TaxFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getIRPFList(filter?: IFilter | undefined): Promise<ICollection<IIRPF>> {
        return (await new IRPFFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getInvoiceCategoryList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceCategory>> {
        return (await new InvoiceCategoryFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getInvoiceSerieList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceSerie>> {
        return (await new InvoiceSerieFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getTransactionTypeList(filter?: IFilter | undefined): Promise<ICollection<IInvoiceTransactionType>> {
        return (await new InvoiceTransactionTypeFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getPaymentMethodList(filter?: IFilter | undefined): Promise<ICollection<ITax>> {
        return (await new PaymentMethodFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }

    async getInvoiceActivityList(filter?: IFilter | undefined): Promise<ICollection<ITax>> {
        return (await new InvoiceActivityFactory().createMultipleObjectCrud().getCollection(filter)).result;
    }
}