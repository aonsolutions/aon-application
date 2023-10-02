import { InvoiceFactory } from "../factorys/InvoiceFactory";
import { IIRPF, IInvoice, IInvoiceActivity, IInvoiceCategory, IInvoiceSerie, IInvoiceTransactionType, IPaymentMethod, ITax } from "../interfaces/modelsInterfaces";
import { ICollection, IFilter, IResponse } from "../interfaces/utilitiesInterfaces";

export class InvoiceFunctions {
    private static invoiceFactory: InvoiceFactory = new InvoiceFactory();

    static async getInvoice(key: string): Promise<IResponse<IInvoice>> {
      return (await this.invoiceFactory.createSingleObjectCrud().getElement(key));
    }

    static async getInvoiceList(filter?: IFilter): Promise<IResponse<ICollection<IInvoice>>> {
      return (await this.invoiceFactory.createMultipleObjectCrud().getCollection(filter));
    }

    static async createInvoice(invoice: IInvoice): Promise<IResponse<IInvoice>> {
      return (await this.invoiceFactory.createSingleObjectCrud().createElement(invoice));
    }

    static async updateInvoice(invoice: IInvoice): Promise<IResponse<IInvoice>> {
      return (await this.invoiceFactory.createSingleObjectCrud().updateElement(invoice));
    }

    static async deleteInvoice(key: string): Promise<IResponse<boolean>> {
      return (await this.invoiceFactory.createSingleObjectCrud().deleteElement(key));
    }

    static async getTaxList(filter?: IFilter): Promise<IResponse<ICollection<ITax>>> {
      return (await this.invoiceFactory.createSpecificMethods().getTaxList(filter));
    }

    static async getIRPFList(filter?: IFilter): Promise<IResponse<ICollection<IIRPF>>> {
      return (await this.invoiceFactory.createSpecificMethods().getIRPFList(filter));
    }

    static async getInvoiceCategoryList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceCategory>>> {
      return (await this.invoiceFactory.createSpecificMethods().getInvoiceCategoryList(filter));
    }

    static async getInvoiceSerieList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceSerie>>> {
      return (await this.invoiceFactory.createSpecificMethods().getInvoiceSerieList(filter));
    }

    static async getTransactionTypeList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceTransactionType>>> {
      return (await this.invoiceFactory.createSpecificMethods().getTransactionTypeList(filter));
    }

    static async getPaymentMethodList(filter?: IFilter): Promise<IResponse<ICollection<IPaymentMethod>>> {
      return (await this.invoiceFactory.createSpecificMethods().getPaymentMethodList(filter));
    }

    static async getInvoiceActivityList(filter?: IFilter): Promise<IResponse<ICollection<IInvoiceActivity>>> {
      return (await this.invoiceFactory.createSpecificMethods().getInvoiceActivityList(filter));
    }
}